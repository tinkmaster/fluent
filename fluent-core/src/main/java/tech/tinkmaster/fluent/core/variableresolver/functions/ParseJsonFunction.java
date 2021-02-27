package tech.tinkmaster.fluent.core.variableresolver.functions;

import com.fasterxml.jackson.databind.JsonNode;
import tech.tinkmaster.fluent.common.entity.execution.Execution;
import tech.tinkmaster.fluent.common.util.JsonUtils;
import tech.tinkmaster.fluent.core.failure.ExecutionFailuresFactory;
import tech.tinkmaster.fluent.core.variableresolver.VariablesResolver;
import tech.tinkmaster.fluent.service.variable.VariableService;

public class ParseJsonFunction implements FluentVariableResolveFunction {
  private static VariablesResolver variablesResolver = new VariablesResolver();

  @Override
  public String name() {
    return "parseJson";
  }

  /**
   * Return the value that matches the certain path
   *
   * @param graph execution information
   * @param parameter the original data and the certain path, two parts only
   * @return the value matches the certain path
   */
  @Override
  public String resolve(Execution graph, String parameter, VariableService variableService) {
    String path = parameter.substring(parameter.lastIndexOf(",") + 1, parameter.length()).trim();
    String var = parameter.substring(0, parameter.lastIndexOf(",")).trim();

    String result = variablesResolver.resolve(graph, var, variableService);

    JsonNode node = JsonUtils.isJsonFormation(result);

    if (node == null) {
      throw ExecutionFailuresFactory.invalidJsonFormation(var);
    }

    try {
      node = node.at(path);
    } catch (IllegalArgumentException e) {
      throw ExecutionFailuresFactory.jsonPathIllegalWhileParsingJacksonPathPointer(e.getMessage());
    }

    if (node.isMissingNode()) {
      throw ExecutionFailuresFactory.cantFindSpecifiedJsonNode(path);
    }

    return node.asText();
  }
}
