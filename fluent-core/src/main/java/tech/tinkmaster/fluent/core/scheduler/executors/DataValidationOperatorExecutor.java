package tech.tinkmaster.fluent.core.scheduler.executors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagram;
import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.core.failure.ExecutionFailure;
import tech.tinkmaster.fluent.core.failure.ExecutionFailuresFactory;
import tech.tinkmaster.fluent.core.variableresolver.VariablesResolver;

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

            // extract function name from value
            String functionName = v.substring(0, v.indexOf(","));
            String unresolvedValue = v.substring(v.indexOf(",") + 1);
            String value = variablesResolver.resolve(this.diagram, unresolvedValue);

            validationResult.put(k, this.check(functionName, var, value));
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

  private Map<String, String> check(String funcName, String key, String value) {
    Map<String, String> result = new HashMap<>();

    if ("equals".equals(funcName)) {
      if (key.equals(value)) {
        result.put(CHECK_RESULT_KEY, Boolean.TRUE.toString());
        result.put(
            CHECK_RESULT_MESSAGE_KEY, String.format(EQUAL_RESULT_MESSAGE_TEMPLATE, key, value));
      } else {
        result.put(CHECK_RESULT_KEY, Boolean.FALSE.toString());
        result.put(
            CHECK_RESULT_MESSAGE_KEY, String.format(NOT_EQUAL_RESULT_MESSAGE_TEMPLATE, key, value));
      }
    } else if ("notEquals".equals(funcName)) {
      if (!key.equals(value)) {
        result.put(CHECK_RESULT_KEY, Boolean.TRUE.toString());
        result.put(
            CHECK_RESULT_MESSAGE_KEY, String.format(NOT_EQUAL_RESULT_MESSAGE_TEMPLATE, key, value));
      } else {
        result.put(CHECK_RESULT_KEY, Boolean.FALSE.toString());
        result.put(
            CHECK_RESULT_MESSAGE_KEY, String.format(EQUAL_RESULT_MESSAGE_TEMPLATE, key, value));
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
