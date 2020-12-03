package tech.tinkmaster.fluent.core.pipeline.scheduler.executors;

import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.core.pipeline.ExecutionSchedulerContext;

public class OperatorExecutionFetcher {
  public static OperatorExecutor getPairedExecutor(
      Operator operator, ExecutionSchedulerContext context) {
    switch (operator.getType()) {
      case HTTP_REQUEST:
        return new HttpOperatorExecutor(context, operator);
      default:
        throw new UnsupportedOperationException(
            "Not supported operator " + operator.getType().name());
    }
  }
}
