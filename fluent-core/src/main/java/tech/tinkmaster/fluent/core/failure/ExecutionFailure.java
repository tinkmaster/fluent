package tech.tinkmaster.fluent.core.failure;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.Date;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class ExecutionFailure extends RuntimeException {
  String message;
  String reason;
  Date failedAt;
  Object result;
}
