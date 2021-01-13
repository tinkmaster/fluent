package tech.tinkmaster.fluent.core.scheduler.executors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagram;
import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.core.VariablesResolver;
import tech.tinkmaster.fluent.core.failure.ExecutionFailure;
import tech.tinkmaster.fluent.core.failure.ExecutionFailuresFactory;

/**
 * This class is used for checking if the data is as expected.
 *
 * <p>The operations listed below is supported: 1. check if two char sequence is equal 2. check if
 * the content is json formation
 *
 * <p>All the variables need to be validated is in the field params. The key is the field to be
 * checked and its value is usually some constant value. Of course, both key and value can be
 * variables reference.
 */
public class DataValidationOperatorExecutor implements OperatorExecutor {
  private static VariablesResolver variablesResolver = new VariablesResolver();
  private static final Pattern EQUALS_PATTERN = Pattern.compile("equals\\(([0-9A-Za-z]+)\\)");
  private static final Pattern NOT_EQUALS_PATTERN =
      Pattern.compile("notEquals\\(([0-9A-Za-z]+)\\)");
  private static final String CHECK_RESULT_KEY = "result";
  private static final String CHECK_RESULT_MESSAGE_KEY = "message";
  private static final String EQUAL_RESULT_MESSAGE_TEMPLATE = "var:%s equals with value:%s.";
  private static final String NOT_EQUAL_RESULT_MESSAGE_TEMPLATE =
      "key:%s does not equal with value:%s.";
  private Operator operator;
  private ExecutionDiagram diagram;

  public DataValidationOperatorExecutor(ExecutionDiagram diagram, Operator operator) {
    this.operator = operator;
    this.diagram = diagram;
  }

  @Override
  public Object execute() throws IOException {
    Map<String, Map<String, String>> validationResult = new HashMap<>();
    Map<String, String> params = this.operator.getParams();
    params.forEach(
        (k, v) -> {
          try {
            String var = variablesResolver.resolve(this.diagram, k);
            String value = variablesResolver.resolve(this.diagram, v);

            validationResult.put(k, this.check(var, value));
          } catch (ExecutionFailure e) {
            validationResult.put(
                k,
                Map.of(
                    CHECK_RESULT_KEY,
                    Boolean.FALSE.toString(),
                    CHECK_RESULT_MESSAGE_KEY,
                    e.getMessage()));
          }
        });

    validationResult.forEach(
        (k, v) -> {
          if (v.get(CHECK_RESULT_KEY).equals(Boolean.FALSE.toString())) {
            throw ExecutionFailuresFactory.invalidDataException(validationResult);
          }
        });
    return validationResult;
  }

  private Map<String, String> check(String key, String value) {
    Map<String, String> result = new HashMap<>();

    // check if has used the function
    Matcher equalMatcher = EQUALS_PATTERN.matcher(value);
    Matcher notEqualMatcher = NOT_EQUALS_PATTERN.matcher(value);

    if (equalMatcher.find()) {
      String var = equalMatcher.group(0);
      if (var.equals(value)) {
        result.put(CHECK_RESULT_KEY, Boolean.TRUE.toString());
        result.put(
            CHECK_RESULT_MESSAGE_KEY, String.format(EQUAL_RESULT_MESSAGE_TEMPLATE, var, value));
      } else {
        result.put(CHECK_RESULT_KEY, Boolean.FALSE.toString());
        result.put(
            CHECK_RESULT_MESSAGE_KEY, String.format(NOT_EQUAL_RESULT_MESSAGE_TEMPLATE, var, value));
      }
    } else if (notEqualMatcher.find()) {
      String var = equalMatcher.group(0);
      if (var.equals(value)) {
        result.put(CHECK_RESULT_KEY, Boolean.TRUE.toString());
        result.put(
            CHECK_RESULT_MESSAGE_KEY, String.format(NOT_EQUAL_RESULT_MESSAGE_TEMPLATE, var, value));
      } else {
        result.put(CHECK_RESULT_KEY, Boolean.FALSE.toString());
        result.put(
            CHECK_RESULT_MESSAGE_KEY, String.format(EQUAL_RESULT_MESSAGE_TEMPLATE, var, value));
      }
    } else {
      if (key.equals(value)) {
        result.put(CHECK_RESULT_KEY, Boolean.TRUE.toString());
        result.put(
            CHECK_RESULT_MESSAGE_KEY, String.format(EQUAL_RESULT_MESSAGE_TEMPLATE, key, value));
      } else {
        result.put(CHECK_RESULT_KEY, Boolean.FALSE.toString());
        result.put(
            CHECK_RESULT_MESSAGE_KEY, String.format(NOT_EQUAL_RESULT_MESSAGE_TEMPLATE, key, value));
      }
    }

    return result;
  }

  @Override
  public Class returnType() {
    return Map.class;
  }

  @Override
  public Operator getOperator() {
    return this.operator;
  }
}
