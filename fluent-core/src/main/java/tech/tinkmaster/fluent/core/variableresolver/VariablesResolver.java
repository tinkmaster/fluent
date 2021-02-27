package tech.tinkmaster.fluent.core.variableresolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tinkmaster.fluent.common.FluentObjectMappers;
import tech.tinkmaster.fluent.common.entity.execution.Execution;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionGraph;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionUtils;
import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.common.entity.variable.Environment;
import tech.tinkmaster.fluent.common.exceptions.FluentSecretDecryptException;
import tech.tinkmaster.fluent.core.failure.ExecutionFailuresFactory;
import tech.tinkmaster.fluent.core.variableresolver.functions.FluentVariableResolveFunctionCenter;
import tech.tinkmaster.fluent.service.variable.VariableService;

/** This class is used for resolving the variables in the operators parameters */
public class VariablesResolver {
  private static final Logger LOG = LoggerFactory.getLogger(VariablesResolver.class);
  public static final Pattern MULTIPLE_VARIABLE_PATTERN = Pattern.compile(".*(#\\{.*\\}).*");
  public static final Pattern VARIABLE_PATTERN = Pattern.compile("#\\{(.+)\\}");
  public static final Pattern OPERATOR_VARIABLE_NAME_PATTERN =
      Pattern.compile("operators\\[(\\d+)\\]");
  public static final Pattern FUNCTION_PATTERN = Pattern.compile("#\\{(\\w+)\\((.*)\\)\\}");
  private static final ObjectMapper MAPPER = FluentObjectMappers.getNewConfiguredJsonMapper();

  /**
   * Right now, only one whole parameter name or one function is allowed, but you can put multiple
   * variable reference in the function's parameters
   *
   * @param execution
   * @param content
   * @return
   */
  public String resolve(Execution execution, String content, VariableService variableService) {
    // find all the level-top variable replacement
    List<String> variables = findPotentialVariables(content);
    Map<String, String> maps = new HashMap<>();

    String finalContent = content;
    variables.forEach(
        var -> {
          Matcher matcher = VARIABLE_PATTERN.matcher(finalContent);
          Matcher functionMatcher = FUNCTION_PATTERN.matcher(finalContent);

          if (functionMatcher.find()) {
            String functionName = functionMatcher.group(1);
            String variableContent = functionMatcher.group(2);

            maps.put(
                var,
                FluentVariableResolveFunctionCenter.resolve(
                    execution, functionName, variableContent, variableService));
          } else if (matcher.find()) {
            String placement = matcher.group(1);
            maps.put(var, this.resolvePlacement(placement, execution, variableService));
          }
        });

    Map.Entry<String, String>[] kvEntries = new Map.Entry[0];
    kvEntries = maps.entrySet().toArray(kvEntries);
    for (int i = 0; i < kvEntries.length; i++) {
      content = content.replace(kvEntries[i].getKey(), kvEntries[i].getValue());
    }

    return content;
  }

  private String resolvePlacement(
      String varName, Execution execution, VariableService variableService) {
    if (execution.getParameters() != null && execution.getParameters().get(varName) != null) {
      return this.resolve(execution, execution.getParameters().get(varName), variableService);
    }

    String[] keys = varName.split("\\.");

    String[] remainingKeys = Arrays.copyOfRange(keys, 1, keys.length);

    // only check pipeline and env here, cause the operators have different formation
    switch (keys[0]) {
      case "pipeline":
        return this.getPipelineValue(varName, keys, remainingKeys, execution);
      case "env":
        return this.getEnvValue(varName, remainingKeys, variableService);
      case "currentEnv":
        return this.getCurrentEnvValue(varName, remainingKeys, variableService, execution);
      case "global":
        return this.getGlobalValue(varName, remainingKeys, variableService);
      case "secret":
        return this.getSecretValue(varName, remainingKeys, variableService);
      case "currentStage":
        return execution.getStages().getCurrentExecutionStage();
      default:
        break;
    }

    // check if it's operator parameters
    Matcher matcher = OPERATOR_VARIABLE_NAME_PATTERN.matcher(keys[0]);
    if (matcher.find()) {
      Integer operatorIndex = Integer.valueOf(matcher.group(1));
      return this.getOperatorValue(
          varName,
          keys,
          remainingKeys,
          operatorIndex,
          execution,
          ExecutionUtils.getCurrentExecutionGraph(execution));
    }

    throw ExecutionFailuresFactory.failToResolveVariable(this.getVariableName(keys));
  }

  private String getPipelineValue(
      String varName, String[] keys, String[] remainingKeys, Execution graph) {
    if (remainingKeys.length == 1) {
      switch (remainingKeys[0]) {
        case "name":
          return graph.getPipelineName();
        default:
          break;
      }
    }
    throw ExecutionFailuresFactory.failToResolveVariable(varName);
  }

  private String getEnvValue(String wholeName, String[] keys, VariableService variableService) {
    String name = this.getVariableName(keys, 1);
    try {
      Environment env = variableService.getEnv(keys[0]);
      if (env == null) {
        throw ExecutionFailuresFactory.cantFindEnv(keys[0]);
      }
      String value = env.getVariables().get(name);
      if (value == null) {
        throw ExecutionFailuresFactory.cantFindEnvVariable(wholeName);
      }
      return value;
    } catch (IOException e) {
      LOG.error("Error occurred while fetching environment variables", e);
      throw ExecutionFailuresFactory.failToResolveVariable(wholeName, e.getMessage());
    }
  }

  private String getCurrentEnvValue(
      String wholeName, String[] remainingKeys, VariableService variableService, Execution graph) {
    if (graph.getEnvironment() == null) {
      throw ExecutionFailuresFactory.cantUseCurrentEnvVariable();
    }
    String name = this.getVariableName(remainingKeys);
    try {
      Environment env = variableService.getEnv(graph.getEnvironment());
      if (env == null) {
        throw ExecutionFailuresFactory.cantFindEnv(graph.getEnvironment());
      }
      String value = env.getVariables().get(name);
      if (value == null) {
        throw ExecutionFailuresFactory.cantFindEnvVariable(wholeName);
      }
      return value;
    } catch (IOException e) {
      LOG.error("Error occurred while fetching environment variables", e);
      throw ExecutionFailuresFactory.failToResolveVariable(wholeName, e.getMessage());
    }
  }

  private String getGlobalValue(
      String wholeName, String[] remainingKeys, VariableService variableService) {
    String name = this.getVariableName(remainingKeys);
    try {
      String value = variableService.getGlobal().getVariables().get(name);
      if (value == null) {
        throw ExecutionFailuresFactory.cantFindGlobalVariable(wholeName);
      }
      return value;
    } catch (IOException e) {
      LOG.error("Error occurred while fetching global variables", e);
      throw ExecutionFailuresFactory.failToResolveVariable(wholeName, e.getMessage());
    }
  }

  private String getSecretValue(
      String wholeName, String[] remainingKeys, VariableService variableService) {
    String name = this.getVariableName(remainingKeys);
    try {
      String value = variableService.getTranslucentSecret(name).getValue();
      if (value == null) {
        throw ExecutionFailuresFactory.cantFindSecretVariable(wholeName);
      }
      return value;
    } catch (IOException | FluentSecretDecryptException e) {
      LOG.error("Error occurred while fetching secret variables", e);
      throw ExecutionFailuresFactory.failToResolveVariable(wholeName, e.getMessage());
    }
  }

  private String getStagesValue(
      Execution execution,
      String wholeName,
      String[] remainingKeys,
      VariableService variableService) {
    if (remainingKeys.length == 0) {
      return execution.getStages().getCurrentExecutionStage();
    } else {
      switch (remainingKeys[0]) {
        case "before":
          remainingKeys = Arrays.copyOfRange(remainingKeys, 0, remainingKeys.length);
          return this.getStageValue(
              execution,
              execution.getStages().getBefore(),
              wholeName,
              remainingKeys,
              variableService);
        case "execute":
          remainingKeys = Arrays.copyOfRange(remainingKeys, 0, remainingKeys.length);
          return this.getStageValue(
              execution,
              execution.getStages().getExecute(),
              wholeName,
              remainingKeys,
              variableService);
        case "clean":
          remainingKeys = Arrays.copyOfRange(remainingKeys, 0, remainingKeys.length);
          return this.getStageValue(
              execution,
              execution.getStages().getClean(),
              wholeName,
              remainingKeys,
              variableService);
        default:
          throw ExecutionFailuresFactory.cantFindSecretVariable(wholeName);
      }
    }
  }

  private String getStageValue(
      Execution execution,
      ExecutionGraph graph,
      String wholeName,
      String[] remainingKeys,
      VariableService variableService) {
    if (remainingKeys.length != 0) {
      Matcher matcher = OPERATOR_VARIABLE_NAME_PATTERN.matcher(remainingKeys[0]);
      if (matcher.find()) {
        Integer operatorIndex = Integer.valueOf(matcher.group(1));
        return this.getOperatorValue(
            wholeName,
            remainingKeys,
            remainingKeys,
            operatorIndex,
            execution,
            ExecutionUtils.getCurrentExecutionGraph(execution));
      }
    }

    throw ExecutionFailuresFactory.cantFindSecretVariable(wholeName);
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
      Execution execution,
      ExecutionGraph graph) {
    if (graph.getNodes().get(operatorIndex) == null) {
      throw ExecutionFailuresFactory.unableToGetResultFromUnknownNode(operatorIndex);
    }
    Operator op = graph.getNodes().get(operatorIndex).getOperator();

    if (remainingKeys.length == 1) {
      switch (remainingKeys[0]) {
        case "name":
          return op.getName();
        case "type":
          return op.getType().name();
        case "result":
          return this.getValueInNodeExecutionResult(operatorIndex, execution, graph);
        default:
          break;
      }
    }
    if (remainingKeys.length > 1) {
      if ("params".equals(remainingKeys[0])) {
        String result = op.getParams().get(remainingKeys[1]);
        if (result != null) {
          return result;
        }
      }
    }
    throw ExecutionFailuresFactory.failToResolveVariable(varName);
  }

  private String getValueInNodeExecutionResult(
      Integer operatorIndex, Execution execution, ExecutionGraph graph) {
    String result = graph.getResults().get(operatorIndex.toString());
    if (result == null) {
      throw ExecutionFailuresFactory.unableToGetResultFromUnexecutedNode(operatorIndex);
    }
    return result;
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

  public static JsonNode translateExecutionGraph(Execution graph) {
    try {
      return MAPPER.readTree(MAPPER.writeValueAsString(graph));
    } catch (JsonProcessingException e) {
      LOG.error(
          "Unable to translate execution information to in the process of variable resolve.", e);
      throw ExecutionFailuresFactory.unableToTranslateExecutionInfoToJsonNode();
    }
  }

  private static List<String> findPotentialVariables(String content) {
    List<String> result = new LinkedList<>();
    char[] cs = content.toCharArray();

    boolean variableStartDetect = false;
    int variableStarterFoundTimes = 0;
    int variableStart = 0;
    for (int i = 0; i < cs.length; i++) {
      if (variableStartDetect) {
        if (i + 1 < cs.length && cs[i] == '#' && cs[i + 1] == '{') {
          i += 2;
          variableStarterFoundTimes++;
        } else if (cs[i] == '}') {
          if (variableStarterFoundTimes == 1) {
            variableStarterFoundTimes = 0;
            result.add(content.substring(variableStart, i + 1));
            variableStartDetect = false;
          } else {
            variableStarterFoundTimes--;
          }
        }
      } else if (i + 1 < cs.length && cs[i] == '#' && cs[i + 1] == '{') {
        variableStartDetect = true;
        variableStarterFoundTimes++;
        variableStart = i;
      }
    }

    return result;
  }
}
