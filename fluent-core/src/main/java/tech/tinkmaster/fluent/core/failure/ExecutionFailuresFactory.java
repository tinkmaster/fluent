package tech.tinkmaster.fluent.core.failure;

import java.util.Date;

public class ExecutionFailuresFactory {
  public static ExecutionFailure failToResolveVariable(String variable) {
    return ExecutionFailure.builder()
        .failedAt(new Date())
        .reason("ResolveVariablesFailure")
        .message(String.format("Can't resolve the variable [%s].", variable))
        .build();
  }

  public static ExecutionFailure invalidDataException(Object validationResult) {
    return ExecutionFailure.builder()
        .failedAt(new Date())
        .reason("DataValidationFailed")
        .message(String.format("Data Validation Failed, please check validation result."))
        .result(validationResult)
        .build();
  }

  public static ExecutionFailure unableToGetResultFromUnexecutedNode(Integer operatorId) {
    return ExecutionFailure.builder()
        .failedAt(new Date())
        .reason("DataValidationFailed")
        .message(
            String.format(
                "Data Validation Failed, unable to get result from unexecuted node, node id: %s.",
                operatorId))
        .build();
  }

  public static ExecutionFailure invalidJsonFormation(Integer operatorId) {
    return ExecutionFailure.builder()
        .failedAt(new Date())
        .reason("DataValidationFailed")
        .message(
            String.format(
                "Data Validation Failed, result from node id: %s is not json formation.",
                operatorId))
        .build();
  }

  public static ExecutionFailure unableToGetValueInNodeResult(String key) {
    return ExecutionFailure.builder()
        .failedAt(new Date())
        .reason("DataValidationFailed")
        .message(String.format("Data Validation Failed, unable to fetch value from key:%s", key))
        .build();
  }
}
