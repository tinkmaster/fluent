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

  public static ExecutionFailure unableToTranslateExecutionInfoToJsonNode() {
    return ExecutionFailure.builder()
        .failedAt(new Date())
        .reason("ResolveVariablesFailure")
        .message("Unable to translate execution information to in the process of variable resolve.")
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

  public static ExecutionFailure unableToGetResultFromUnknownNode(Integer operatorId) {
    return ExecutionFailure.builder()
        .failedAt(new Date())
        .reason("DataValidationFailed")
        .message(
            String.format(
                "Data Validation Failed, unable to get result from unknown node, node id: %s.",
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

  /** ------------------------- Exceptions about resolve functions ------------------------------ */
  public static ExecutionFailure cantFindSpecifiedFunction(String funcName) {
    return ExecutionFailure.builder()
        .failedAt(new Date())
        .reason("DataValidationFailed")
        .message(
            String.format(
                "Can't find specified function while validating data, function name: [%s]",
                funcName))
        .build();
  }

  public static ExecutionFailure invalidJsonFormation(String path) {
    return ExecutionFailure.builder()
        .failedAt(new Date())
        .reason("DataValidationFailed")
        .message(String.format("Can't find valid json node in path: [%s]", path))
        .build();
  }
}
