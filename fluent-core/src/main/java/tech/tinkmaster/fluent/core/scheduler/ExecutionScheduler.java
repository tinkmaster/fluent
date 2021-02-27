package tech.tinkmaster.fluent.core.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

      while (true) {
        Pair<String, Integer> nodeNextToRunPair =
            ExecutionSchedulerUtils.findNextNodeToExecute(this.execution);
        if (nodeNextToRunPair == null) {
          this.setExecutionStatus(ExecutionStatus.FINISHED);
          this.updateExecutionStatus();
          this.execution = null;
          this.schdulerStatus = SchedulerStatus.IDLE;
          break;
        }
        String currentStage = nodeNextToRunPair.getLeft();
        Integer nodeId = nodeNextToRunPair.getRight();
        try {

          this.execution = ExecutionUtils.setCurrentStage(this.execution, currentStage);
          // execution log store in execution diagram snapshot
          LOG.debug(
              "Start to execute operator id {} in stage {} in execution {}",
              nodeId,
              currentStage,
              this.execution.getName());
          long current = System.currentTimeMillis();
          ExecutionUtils.setGraphNodeStatus(
              this.execution, currentStage, nodeId, ExecutionStatus.RUNNING);
          this.updateExecutionStatus();

          Object result =
              this.executeOperator(
                  ExecutionUtils.getCurrentExecutionGraph(this.execution)
                      .getNodes()
                      .get(nodeId)
                      .getOperator());

          ExecutionUtils.setGraphNodeUsedTime(
              this.execution, currentStage, nodeId, System.currentTimeMillis() - current);
          LOG.debug(
              "Finish executing operator id {} in stage {}  in execution {}",
              nodeId,
              currentStage,
              this.execution.getName());
          ExecutionUtils.setGraphNodeStatus(
              this.execution, currentStage, nodeId, ExecutionStatus.FINISHED);
          ExecutionUtils.getCurrentExecutionGraph(this.execution)
              .getResults()
              .put(
                  String.valueOf(nodeId),
                  FluentObjectMappers.getNewConfiguredJsonMapper().writeValueAsString(result));
        } catch (Throwable t) {
          LOG.error("Failed to execute node:{}", nodeId, t);
          Map<String, Object> result = new HashMap<>();
          if (t instanceof ExecutionFailure) {
            try {
              result.put(
                  "error",
                  String.format(
                      "Failed to executor operator, caused by: %s",
                      t.getMessage().replaceAll("\"", "'")));
              result.put("data", ((ExecutionFailure) t).getResult());
              ExecutionUtils.getCurrentExecutionGraph(this.execution)
                  .getResults()
                  .put(
                      String.valueOf(nodeId),
                      FluentObjectMappers.getNewConfiguredJsonMapper().writeValueAsString(result));
            } catch (JsonProcessingException e) {
              LOG.error("Failed to store node execution result", t);
            }
          } else {
            ExecutionUtils.getCurrentExecutionGraph(this.execution)
                .getResults()
                .put(
                    String.valueOf(nodeId),
                    "{ \"error\": \"Failed to executor operator, caused by: "
                        + (t.getMessage() != null
                            ? t.getMessage().replaceAll("\"", "'")
                            : t.getMessage())
                        + "\"}");
          }
          ExecutionUtils.setGraphNodeStatus(
                  this.execution, currentStage, nodeId, ExecutionStatus.FAILED);
          this.execution = this.execution.toBuilder().status(ExecutionStatus.RUNNING).build();
          this.updateExecutionStatus();
          this.execution = null;
          this.schdulerStatus = SchedulerStatus.IDLE;
          break;
        }
      }
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
    PipelineSchedulerService.updateDiagramStatus(this.execution);
  }
}
