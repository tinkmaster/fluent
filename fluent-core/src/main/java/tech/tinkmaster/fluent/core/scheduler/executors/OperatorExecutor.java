package tech.tinkmaster.fluent.core.scheduler.executors;

import tech.tinkmaster.fluent.common.entity.operator.Operator;

import java.io.IOException;

public interface OperatorExecutor {
  Object execute() throws IOException;

  Class returnType();

  Operator getOperator();
}
