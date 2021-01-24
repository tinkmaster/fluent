package tech.tinkmaster.fluent.core.variableresolver.functions;

import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagram;
import tech.tinkmaster.fluent.service.variable.VariableService;

public interface FluentVariableResolveFunction {
  String name();

  String resolve(ExecutionDiagram diagram, String parameter, VariableService variableService);
}
