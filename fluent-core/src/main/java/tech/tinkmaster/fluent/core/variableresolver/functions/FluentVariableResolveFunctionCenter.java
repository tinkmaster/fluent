package tech.tinkmaster.fluent.core.variableresolver.functions;

import static java.util.Map.entry;

import java.util.Map;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagram;
import tech.tinkmaster.fluent.core.failure.ExecutionFailuresFactory;

public class FluentVariableResolveFunctionCenter {
  private static final FluentVariableResolveFunction parseJson = new ParseJsonFunction();
  private static final Map<String, FluentVariableResolveFunction> functions =
      Map.ofEntries(entry(parseJson.name(), parseJson));

  public static String resolve(ExecutionDiagram diagram, String funcName, String paramsContent) {
    FluentVariableResolveFunction function = functions.get(funcName);

    if (function == null) {
      throw ExecutionFailuresFactory.cantFindSpecifiedFunction(funcName);
    }

    return function.resolve(diagram, paramsContent);
  }
}
