package tech.tinkmaster.fluent.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tech.tinkmaster.fluent.common.FluentObjectMappers;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagram;
import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.core.failure.ExecutionFailuresFactory;

/** This class is used for resolving the variables in the operators parameters */
public class VariablesResolver {
  public static final Pattern VARIABLE_PATTERN = Pattern.compile("#\\{(.+)\\}");
  public static final Pattern OPERATOR_VARIABLE_NAME_PATTERN =
      Pattern.compile("operators\\[(\\d+)\\]");
  public static final Pattern JSON_ARRAY_NAME_PATTERN = Pattern.compile(".+\\[(\\d+)\\]");
  private static final ObjectMapper MAPPER = FluentObjectMappers.getNewConfiguredJsonMapper();

  public String resolve(ExecutionDiagram diagram, String content) {
    Matcher matcher = VARIABLE_PATTERN.matcher(content);

    if (matcher.find()) {
      int length = matcher.groupCount();
      for (int i = 0; i < length; i++) {
        String placement = matcher.group(i);
        String value =
            this.resolvePlacement(placement.substring(2, placement.length() - 1), diagram);
        content = content.replace(placement, value);
      }
    }
    return content;
  }

  private String resolvePlacement(String varName, ExecutionDiagram diagram) {
    String[] keys = varName.split("\\.");

    String[] remainingKeys = Arrays.copyOfRange(keys, 1, keys.length);

    // only check pipeline and env here, cause the operators have different formation
    switch (keys[0]) {
      case "pipeline":
        return this.getPipelineValue(varName, keys, remainingKeys, diagram);
      default:
        break;
    }

    // check if it's operator parameters
    Matcher matcher = OPERATOR_VARIABLE_NAME_PATTERN.matcher(keys[0]);
    if (matcher.find()) {
      Integer operatorIndex = Integer.valueOf(matcher.group(1));

      return this.getOperatorValue(varName, keys, remainingKeys, operatorIndex, diagram);
    }

    throw ExecutionFailuresFactory.failToResolveVariable(this.getVariableName(keys));
  }

  private String getPipelineValue(
      String varName, String[] keys, String[] remainingKeys, ExecutionDiagram diagram) {
    if (remainingKeys.length == 1) {
      switch (remainingKeys[0]) {
        case "name":
          return diagram.getPipelineName();
        default:
          break;
      }
    }
    throw ExecutionFailuresFactory.failToResolveVariable(varName);
  }

  /**
   * This method is in charge of referring the value in operators. * *
   *
   * <p>So for different operators in the execution information, you can use 'operators[${index}]' *
   * to select it.
   *
   * @param varName
   * @param keys
   * @param remainingKeys
   * @param operatorIndex
   * @return
   */
  private String getOperatorValue(
      String varName,
      String[] keys,
      String[] remainingKeys,
      Integer operatorIndex,
      ExecutionDiagram diagram) {
    Operator op = diagram.getNodes().get(operatorIndex).getOperator();

    if (remainingKeys.length == 1) {
      switch (remainingKeys[0]) {
        case "name":
          return op.getName();
        case "type":
          return op.getType().name();
        default:
          break;
      }
    }
    if (remainingKeys.length > 1) {
      switch (remainingKeys[0]) {
        case "params":
          String result = op.getParams().get(remainingKeys[1]);
          if (result != null) {
            return result;
          }
        case "result":
          return this.getValueInNodeExecutionResult(
              Arrays.copyOfRange(remainingKeys, 1, remainingKeys.length), operatorIndex, diagram);
        default:
          break;
      }
    }
    throw ExecutionFailuresFactory.failToResolveVariable(varName);
  }

  private String getValueInNodeExecutionResult(
      String[] remainingKeys, Integer operatorIndex, ExecutionDiagram diagram) {
    String result = diagram.getResults().get(operatorIndex.toString());
    if (result == null) {
      throw ExecutionFailuresFactory.unableToGetResultFromUnexecutedNode(operatorIndex);
    }
    JsonNode node = this.isJsonFormation(result);
    if (node == null) {
      throw ExecutionFailuresFactory.invalidJsonFormation(operatorIndex);
    }

    for (int i = 0; i < remainingKeys.length; i++) {
      Matcher matcher = JSON_ARRAY_NAME_PATTERN.matcher(remainingKeys[i]);
      if (matcher.find()) {
        node = node.get(Integer.parseInt(matcher.group(1)));
      } else {
        node = node.get(remainingKeys[i]);
      }
      if (node == null) {
        throw ExecutionFailuresFactory.unableToGetValueInNodeResult(
            String.join(".", Arrays.copyOfRange(remainingKeys, 0, i)));
      }
    }

    return node.asText();
  }

  private String getVariableName(String[] keys) {
    return String.join(".", keys);
  }

  private String getVariableName(String[] keys, int start) {
    return String.join(".", Arrays.<String>copyOfRange(keys, start, keys.length));
  }

  private String getVariableName(String[] keys, int start, int end) {
    return String.join(".", Arrays.<String>copyOfRange(keys, start, end));
  }

  private JsonNode isJsonFormation(String content) {
    try {
      return MAPPER.readTree(content);
    } catch (IOException e) {
      // ignored
    } catch (Throwable t) {
      return null;
    }
    return null;
  }
}
