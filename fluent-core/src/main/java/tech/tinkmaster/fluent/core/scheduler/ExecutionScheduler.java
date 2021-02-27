package tech.tinkmaster.fluent.core.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tinkmaster.fluent.common.FluentObjectMappers;
import tech.tinkmaster.fluent.common.entity.execution.Execution;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionStatus;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionUtils;
import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.core.PipelineSchedulerService;
import tech.tinkmaster.fluent.core.failure.ExecutionFailure;
import tech.tinkmaster.fluent.core.scheduler.executors.OperatorExecutionFetcher;
import tech.tinkmaster.fluent.core.scheduler.executors.OperatorExecutor;
import tech.tinkmaster.fluent.service.variable.VariableService;

@Getter
public class ExecutionScheduler extends Thread {
  private static final Logger LOG = LoggerFactory.getLogger(ExecutionScheduler.class);

  int scheduleId;
  SchedulerStatus schdulerStatus;
  Execution execution;
  VariableService variableService;

  public ExecutionScheduler(int id) {
    this.scheduleId = id;
    this.setName("ExecutionScheduler-" + id);
    this.schdulerStatus = SchedulerStatus.IDLE;
    this.setUncaughtExceptionHandler(
        (t, e) -> LOG.error("Uncaught exception found in execution scheduler.", e));
  }

  /** First overall recheck the graph status and then find the node to run */
  @Override
  public void run() {
    LOG.info("Execution scheduler-{} starts.", this.scheduleId);

    while (true) {
      if (this.execution == null) {
        this.sleep();
        continue;
      }
      if (this.variableService == null) {
        this.variableService = PipelineSchedulerService.getVariableService();
      }
      this.execution = ExecutionSchedulerUtils.updateExecutionGraphStatus(this.execution);

      while (ExecutionStatus.needToRun(this.execution.getStatus())) {
        Integer nodeId = null;
        String currentStage = null;
        try {
          nodeId = ExecutionSchedulerUtils.findNextNodeToExecute(this.execution);
          currentStage = this.execution.getStages().getCurrentExecutionStage();
          currentStage = this.execution.getStages().getCurrentExecutionStage();
          this.execution = ExecutionUtils.setCurrentStage(this.execution, currentStage);

          // execution log store in execution graph snapshot
          LOG.debug(
              "Start to execute operator id {} in stage {} in execution {}",
              nodeId,
              currentStage,
              this.execution.getName());
          long current = System.currentTimeMillis();
          ExecutionUtils.setGraphNodeStatus(
              this.execution, currentStage, nodeId, ExecutionStatus.RUNNING);

          Object result =
              this.executeOperator(
                  ExecutionUtils.getCurrentExecutionGraph(this.execution)
                      .getNodes()
                      .get(nodeId)
                      .getOperator());

          ExecutionUtils.setGraphNodeUsedTime(
              this.execution, currentStage, nodeId, System.currentTimeMillis() - current);
          ExecutionUtils.setGraphNodeStatus(
              this.execution, currentStage, nodeId, ExecutionStatus.FINISHED);
          ExecutionUtils.getCurrentExecutionGraph(this.execution)
              .getResults()
              .put(
                  nodeId,
                  FluentObjectMappers.getNewConfiguredJsonMapper().writeValueAsString(result));

          this.execution = ExecutionSchedulerUtils.updateExecutionGraphStatus(this.execution);
          this.updateExecutionStatus();
          LOG.debug(
              "Finish executing operator id {} in stage {} in execution {}",
              nodeId,
              currentStage,
              this.execution.getName());
        } catch (Throwable t) {
          LOG.error(
              "Failed to execute operator id {} in stage {} in execution {}",
              nodeId,
              currentStage,
              this.execution.getName(),
              t);
          Map<String, Object> result = new HashMap<>();
          if (t instanceof ExecutionFailure) {
            try {
              result.put(
                  "error",
                  String.format(
                      "Failed to execute operator, caused by: %s",
                      t.getMessage().replaceAll("\"", "'")));
              result.put("data", ((ExecutionFailure) t).getResult());
              ExecutionUtils.getCurrentExecutionGraph(this.execution)
                  .getResults()
                  .put(
                      nodeId,
                      FluentObjectMappers.getNewConfiguredJsonMapper().writeValueAsString(result));
            } catch (JsonProcessingException e) {
              LOG.error("Failed to store node execution result", t);
            }
          } else {
            ExecutionUtils.getCurrentExecutionGraph(this.execution)
                .getResults()
                .put(
                    nodeId,
                    "{ \"error\": \"Failed to executor operator, caused by: "
                        + (t.getMessage() != null
                            ? t.getMessage().replaceAll("\"", "'")
                            : t.getMessage())
                        + "\"}");
          }
          ExecutionUtils.setGraphNodeStatus(
              this.execution, currentStage, nodeId, ExecutionStatus.FAILED);
          this.execution = this.execution.toBuilder().status(ExecutionStatus.RUNNING).build();
        }
      }
      this.updateExecutionStatus();
      this.execution = null;
      this.schdulerStatus = SchedulerStatus.IDLE;
    }
  }

  private Object executeOperator(Operator operator) throws IOException {
    OperatorExecutor executor =
        OperatorExecutionFetcher.getPairedExecutor(this.execution, operator, this.variableService);
    return executor.execute();
  }

  private void setExecutionStatus(ExecutionStatus status) {
    this.execution = this.execution.toBuilder().status(status).build();
  }

  public void assign(Execution execution) {
    this.execution = execution;
    this.schdulerStatus = SchedulerStatus.BUSY;

    this.updateExecutionStatus();
  }

  private void sleep() {
    try {
      Thread.sleep(5_000);
    } catch (InterruptedException e) {
      LOG.error("Pipeline scheduler-{} is interrupted.", this.scheduleId, e);
    }
  }

  private void updateExecutionStatus() {
    PipelineSchedulerService.updateGraphStatus(this.execution);
  }
}
