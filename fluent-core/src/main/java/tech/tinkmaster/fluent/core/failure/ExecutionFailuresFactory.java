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
}
