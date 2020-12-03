package tech.tinkmaster.fluent.core.pipeline.scheduler;

import java.io.IOException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tinkmaster.fluent.common.FluentObjectMappers;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagram;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagramNode;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionStatus;
import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.core.pipeline.ExecutionSchedulerContext;
import tech.tinkmaster.fluent.core.pipeline.PipelineSchedulerService;
import tech.tinkmaster.fluent.core.pipeline.scheduler.executors.OperatorExecutionFetcher;
import tech.tinkmaster.fluent.core.pipeline.scheduler.executors.OperatorExecutor;

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

    ExecutionSchedulerContext context = new ExecutionSchedulerContext();
    while (true) {
      if (this.executionDiagram == null) {
        this.sleep();
        continue;
      }
      ExecutionDiagramNode node = this.executionDiagram.findNextNodeToRun();
      if (node == null) {
        this.executionDiagram.status = ExecutionStatus.FINISHED;
        this.executionDiagram.result = context.executionInfo;
        this.executionDiagram.currentNode = null;
        this.updateDiagramStatus();
        this.executionDiagram = null;
        this.status = SchedulerStatus.IDLE;
        continue;
      }
      try {
        // execution log store in execution diagram snapshot
        LOG.info("Start to execute operator id {}", node.id);
        long current = System.currentTimeMillis();
        node.status = ExecutionStatus.RUNNING;
        Object result = this.executeOperator(node.operator, context);
        node.usedTime = System.currentTimeMillis() - current;
        LOG.info("Finish executing operator id {}", node.id);
        node.status = ExecutionStatus.FINISHED;
        context.executionInfo.put(
            String.valueOf(node.id),
            FluentObjectMappers.getNewConfiguredJsonMapper().writeValueAsString(result));
      } catch (Throwable t) {
        context.executionInfo.put(
            String.valueOf(node.id),
            "{ \"error\": \"Failed to executor operator, caused by: "
                + t.getMessage().replaceAll("\"", "\'")
                + "\"}");
        node.status = ExecutionStatus.FAILED;
        this.executionDiagram.result = context.executionInfo;
        this.executionDiagram.status = ExecutionStatus.FAILED;
        this.updateDiagramStatus();
        this.executionDiagram = null;
        this.status = SchedulerStatus.IDLE;
      }
    }
  }

  private Object executeOperator(Operator operator, ExecutionSchedulerContext context)
      throws IOException {
    OperatorExecutor executor = OperatorExecutionFetcher.getPairedExecutor(operator, context);
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
