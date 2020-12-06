package tech.tinkmaster.fluent.core.scheduler;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tinkmaster.fluent.common.FluentObjectMappers;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagram;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagramNode;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionStatus;
import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.core.PipelineSchedulerService;
import tech.tinkmaster.fluent.core.scheduler.executors.OperatorExecutionFetcher;
import tech.tinkmaster.fluent.core.scheduler.executors.OperatorExecutor;

import java.io.IOException;

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
          .nodes
          .keySet()
          .forEach(
              id -> {
                if (this.executionDiagram.nodes.get(id).status == ExecutionStatus.FAILED) {
                  this.executionDiagram.status = ExecutionStatus.FAILED;
                  this.executionDiagram = null;
                  this.status = SchedulerStatus.IDLE;
                }
              });
      if (this.executionDiagram.status == ExecutionStatus.FAILED) {
        continue;
      }

      while (true) {
        ExecutionDiagramNode node =
            this.executionDiagram.nodes.get(this.executionDiagram.findNextNodeToRun());
        if (node == null) {
          this.executionDiagram.status = ExecutionStatus.FINISHED;
          this.executionDiagram.currentNode = null;
          this.updateDiagramStatus();
          this.executionDiagram = null;
          this.status = SchedulerStatus.IDLE;
          break;
        }
        try {
          // execution log store in execution diagram snapshot
          LOG.info("Start to execute operator id {}", node.id);
          long current = System.currentTimeMillis();
          node.status = ExecutionStatus.RUNNING;
          Object result = this.executeOperator(node.operator);
          node.usedTime = System.currentTimeMillis() - current;
          LOG.info("Finish executing operator id {}", node.id);
          node.status = ExecutionStatus.FINISHED;
          this.executionDiagram.results.put(
              String.valueOf(node.id),
              FluentObjectMappers.getNewConfiguredJsonMapper().writeValueAsString(result));
        } catch (Throwable t) {
          this.executionDiagram.results.put(
              String.valueOf(node.id),
              "{ \"error\": \"Failed to executor operator, caused by: "
                  + t.getMessage().replaceAll("\"", "\'")
                  + "\"}");
          node.status = ExecutionStatus.FAILED;
          this.executionDiagram.status = ExecutionStatus.FAILED;
          this.updateDiagramStatus();
          this.executionDiagram = null;
          this.status = SchedulerStatus.IDLE;
          break;
        }
      }
    }
  }

  private Object executeOperator(Operator operator) throws IOException {
    OperatorExecutor executor = OperatorExecutionFetcher.getPairedExecutor(operator);
    return executor.execute();
  }

  public void assign(ExecutionDiagram diagram) {
    this.executionDiagram = diagram;
    this.status = SchedulerStatus.BUSY;
    this.executionDiagram.status = ExecutionStatus.RUNNING;
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
