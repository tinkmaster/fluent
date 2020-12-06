package tech.tinkmaster.fluent.core;

import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagram;
import tech.tinkmaster.fluent.core.failure.ExecutionFailuresFactory;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** This class is used for resolving the variables in the operators parameters */
public class VariablesResolver {
  public static final Pattern VARIABLE_PATTERN = Pattern.compile("#\\{.+\\}");

  public ExecutionDiagram diagram;

  public String resolve(String content) {
    Matcher matcher = VARIABLE_PATTERN.matcher(content);

    if (matcher.find()) {
      int length = matcher.groupCount();
      for (int i = 0; i < length; i++) {
        String placement = matcher.group(i);
      }

      return null;

    } else {
      return content;
    }
  }

  private String resolvePlacement(String content) {
    String[] keys = content.split("\\.");

    String[] leftKeys = Arrays.copyOfRange(keys, 1, keys.length);

    switch (keys[0]) {
      case "pipeline":
        return this.getPipelineValue(leftKeys);
      default:
        throw ExecutionFailuresFactory.failToResolveVariable(this.getVariableName(keys));
    }
  }

  public String getPipelineValue(String[] keys) {
    String variableName = this.getVariableName(keys, 1);
    switch (keys[0]) {
      case "name":
        return this.diagram.pipelineName;
      default:
        throw ExecutionFailuresFactory.failToResolveVariable(variableName);
    }
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
}
