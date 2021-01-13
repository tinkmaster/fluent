package tech.tinkmaster.fluent.core.scheduler.executors;

import java.io.IOException;
import tech.tinkmaster.fluent.common.entity.operator.Operator;

public interface OperatorExecutor {
  Object execute() throws IOException;

  Class returnType();

  Operator getOperator();
}
