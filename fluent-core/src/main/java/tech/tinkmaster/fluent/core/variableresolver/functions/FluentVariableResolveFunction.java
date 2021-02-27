package tech.tinkmaster.fluent.core.variableresolver.functions;

import tech.tinkmaster.fluent.common.entity.execution.Execution;
import tech.tinkmaster.fluent.service.variable.VariableService;

public interface FluentVariableResolveFunction {
  String name();

  String resolve(Execution graph, String parameter, VariableService variableService);
}
