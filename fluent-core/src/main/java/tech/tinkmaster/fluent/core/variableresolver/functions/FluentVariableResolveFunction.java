package tech.tinkmaster.fluent.core.variableresolver.functions;

import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagram;

public interface FluentVariableResolveFunction {
  String name();

  String resolve(ExecutionDiagram diagram, String parameter);
}
