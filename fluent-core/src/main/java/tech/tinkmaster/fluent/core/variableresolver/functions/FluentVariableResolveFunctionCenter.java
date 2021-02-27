package tech.tinkmaster.fluent.core.variableresolver.functions;

import tech.tinkmaster.fluent.common.entity.execution.Execution;
import tech.tinkmaster.fluent.core.failure.ExecutionFailuresFactory;
import tech.tinkmaster.fluent.service.variable.VariableService;

import java.util.Map;

import static java.util.Map.entry;

public class FluentVariableResolveFunctionCenter {
  private static final FluentVariableResolveFunction parseJson = new ParseJsonFunction();
  private static final Map<String, FluentVariableResolveFunction> functions =
      Map.ofEntries(entry(parseJson.name(), parseJson));

  public static String resolve(
      Execution diagram, String funcName, String paramsContent, VariableService variableService) {
    FluentVariableResolveFunction function = functions.get(funcName);

    if (function == null) {
      throw ExecutionFailuresFactory.cantFindSpecifiedFunction(funcName);
    }

    return function.resolve(diagram, paramsContent, variableService);
  }
}
