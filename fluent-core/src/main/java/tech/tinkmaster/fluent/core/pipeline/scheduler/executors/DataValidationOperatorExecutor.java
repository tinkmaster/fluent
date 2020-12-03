package tech.tinkmaster.fluent.core.pipeline.scheduler.executors;

import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.common.entity.pipeline.Environment;
import tech.tinkmaster.fluent.core.pipeline.ExecutionSchedulerContext;

public class DataValidationOperatorExecutor implements OperatorExecutor {
  Environment environment;
  ExecutionSchedulerContext context;
  Operator operator;

  @Override
  public ExecutionSchedulerContext execute() {
    return null;
  }

  @Override
  public ExecutionSchedulerContext getContext() {
    return this.context;
  }

  @Override
  public Operator getOperator() {
    return this.operator;
  }

  @Override
  public Class returnType() {
    return null;
  }
}
