package tech.tinkmaster.fluent.core.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tinkmaster.fluent.common.FluentObjectMappers;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagram;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagramNode;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionStatus;
import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.core.PipelineSchedulerService;
import tech.tinkmaster.fluent.core.failure.ExecutionFailure;
import tech.tinkmaster.fluent.core.scheduler.executors.OperatorExecutionFetcher;
import tech.tinkmaster.fluent.core.scheduler.executors.OperatorExecutor;

@Getter
public class ExecutionScheduler extends Thread {
  private static final Logger LOG = LoggerFactory.getLogger(ExecutionScheduler.class);

  int scheduleId;
  SchedulerStatus status;
  ExecutionDiagram executionDiagram;

  public ExecutionScheduler(int id) {
    this.scheduleId = id;
    this.setName("ExecutionScheduler-" + id);
    this.status = SchedulerStatus.IDLE;
    this.setUncaughtExceptionHandler(
        (t, e) -> LOG.error("Uncaught exception found in execution scheduler.", e));
  }

  @Override
  public void run() {
    LOG.info("Execution scheduler-{} starts.", this.scheduleId);

    while (true) {
      if (this.executionDiagram == null) {
        this.sleep();
        continue;
      }
      // to detest if execution diagram rerun, we fail it ASAP if one operator is FAILED.
      this.executionDiagram
          .getNodes()
          .keySet()
          .forEach(
              id -> {
                if (this.executionDiagram.getNodes().get(id).getStatus()
                    == ExecutionStatus.FAILED) {
                  this.executionDiagram.setStatus(ExecutionStatus.FAILED);
                  this.executionDiagram = null;
                  this.status = SchedulerStatus.IDLE;
                }
              });
      if (this.executionDiagram.getStatus() == ExecutionStatus.FAILED) {
        continue;
      }

      while (true) {
        ExecutionDiagramNode node =
            this.executionDiagram.getNodes().get(this.executionDiagram.findNextNodeToRun());
        if (node == null) {
          this.executionDiagram.setStatus(ExecutionStatus.FINISHED);
          this.executionDiagram.setCurrentNode(null);
          this.updateDiagramStatus();
          this.executionDiagram = null;
          this.status = SchedulerStatus.IDLE;
          break;
        }
        try {
          // execution log store in execution diagram snapshot
          LOG.info("Start to execute operator id {}", node.getId());
          long current = System.currentTimeMillis();
          node.setStatus(ExecutionStatus.RUNNING);
          Object result = this.executeOperator(node.getOperator());
          node.setUsedTime(System.currentTimeMillis() - current);
          LOG.info("Finish executing operator id {}", node.getId());
          node.setStatus(ExecutionStatus.FINISHED);
          this.executionDiagram
              .getResults()
              .put(
                  String.valueOf(node.getId()),
                  FluentObjectMappers.getNewConfiguredJsonMapper().writeValueAsString(result));
        } catch (Throwable t) {
          LOG.error("Failed to execute node:{}", node.getId(), t);
          Map<String, Object> result = new HashMap<>();
          if (t instanceof ExecutionFailure) {
            try {
              result.put(
                  "error",
                  String.format(
                      "Failed to executor operator, caused by: %s",
                      t.getMessage().replaceAll("\"", "'")));
              result.put("data", ((ExecutionFailure) t).getResult());
              this.executionDiagram
                  .getResults()
                  .put(
                      String.valueOf(node.getId()),
                      FluentObjectMappers.getNewConfiguredJsonMapper().writeValueAsString(result));
            } catch (JsonProcessingException e) {
              LOG.error("Failed to store node execution result", t);
            }
          } else {
            this.executionDiagram
                .getResults()
                .put(
                    String.valueOf(node.getId()),
                    "{ \"error\": \"Failed to executor operator, caused by: "
                        + (t.getMessage() != null
                            ? t.getMessage().replaceAll("\"", "\'")
                            : t.getMessage())
                        + "\"}");
          }
          node.setStatus(ExecutionStatus.FAILED);
          this.executionDiagram.setStatus(ExecutionStatus.FAILED);
          this.updateDiagramStatus();
          this.executionDiagram = null;
          this.status = SchedulerStatus.IDLE;
          break;
        }
      }
    }
  }

  private Object executeOperator(Operator operator) throws IOException {
    OperatorExecutor executor =
        OperatorExecutionFetcher.getPairedExecutor(this.executionDiagram, operator);
    return executor.execute();
  }

  public void assign(ExecutionDiagram diagram) {
    this.executionDiagram = diagram;
    this.status = SchedulerStatus.BUSY;
    this.executionDiagram.setStatus(ExecutionStatus.RUNNING);
    this.updateDiagramStatus();
  }

  private void sleep() {
    try {
      Thread.sleep(5_000);
    } catch (InterruptedException e) {
      LOG.error("Pipeline scheduler-{} is interrupted.", this.scheduleId, e);
    }
  }

  private void updateDiagramStatus() {
    PipelineSchedulerService.updateDiagramStatus(this.executionDiagram);
  }
}
