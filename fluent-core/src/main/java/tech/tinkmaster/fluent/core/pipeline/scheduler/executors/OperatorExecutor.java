package tech.tinkmaster.fluent.core.pipeline.scheduler.executors;

import java.io.IOException;
import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.core.pipeline.ExecutionSchedulerContext;

public interface OperatorExecutor {
  Object execute() throws IOException;

  Class returnType();

  // Getter
  ExecutionSchedulerContext getContext();

  Operator getOperator();
}
