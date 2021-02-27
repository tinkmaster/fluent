package tech.tinkmaster.fluent.core.scheduler.executors;

import tech.tinkmaster.fluent.common.entity.execution.Execution;
import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.service.variable.VariableService;

public class OperatorExecutionFetcher {
  public static OperatorExecutor getPairedExecutor(
      Execution graph, Operator operator, VariableService variableService) {
    switch (operator.getType()) {
      case HTTP_REQUEST:
        return new HttpOperatorExecutor(graph, operator);
      case DATA_VALIDATION:
        return new DataValidationOperatorExecutor(graph, operator, variableService);
      default:
        throw new UnsupportedOperationException(
            "Not supported operator " + operator.getType().name());
    }
  }
}
