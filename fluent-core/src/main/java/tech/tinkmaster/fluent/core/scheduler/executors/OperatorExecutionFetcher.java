package tech.tinkmaster.fluent.core.scheduler.executors;

import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagram;
import tech.tinkmaster.fluent.common.entity.operator.Operator;

public class OperatorExecutionFetcher {
  public static OperatorExecutor getPairedExecutor(ExecutionDiagram diagram, Operator operator) {
    switch (operator.getType()) {
      case HTTP_REQUEST:
        return new HttpOperatorExecutor(diagram, operator);
      case DATA_VALIDATION:
        return new DataValidationOperatorExecutor(diagram, operator);
      default:
        throw new UnsupportedOperationException(
            "Not supported operator " + operator.getType().name());
    }
  }
}
