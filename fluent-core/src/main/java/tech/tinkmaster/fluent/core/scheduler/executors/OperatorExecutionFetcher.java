package tech.tinkmaster.fluent.core.scheduler.executors;

import tech.tinkmaster.fluent.common.entity.operator.Operator;

public class OperatorExecutionFetcher {
  public static OperatorExecutor getPairedExecutor(Operator operator) {
    switch (operator.getType()) {
      case HTTP_REQUEST:
        return new HttpOperatorExecutor(operator);
      default:
        throw new UnsupportedOperationException(
            "Not supported operator " + operator.getType().name());
    }
  }
}
