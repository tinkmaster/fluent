package tech.tinkmaster.fluent.core.variableresolver.functions;

import static java.util.Map.entry;

import java.util.Map;
import tech.tinkmaster.fluent.common.entity.execution.Execution;
import tech.tinkmaster.fluent.core.failure.ExecutionFailuresFactory;
import tech.tinkmaster.fluent.service.variable.VariableService;

public class FluentVariableResolveFunctionCenter {
  private static final FluentVariableResolveFunction parseJson = new ParseJsonFunction();
  private static final Map<String, FluentVariableResolveFunction> functions =
      Map.ofEntries(entry(parseJson.name(), parseJson));

  public static String resolve(
      Execution graph, String funcName, String paramsContent, VariableService variableService) {
    FluentVariableResolveFunction function = functions.get(funcName);

    if (function == null) {
      throw ExecutionFailuresFactory.cantFindSpecifiedFunction(funcName);
    }

    return function.resolve(graph, paramsContent, variableService);
  }
}
