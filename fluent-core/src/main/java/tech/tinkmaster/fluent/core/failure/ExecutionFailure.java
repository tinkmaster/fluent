package tech.tinkmaster.fluent.core.failure;

import java.util.Date;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class ExecutionFailure extends RuntimeException {
  String message;
  String reason;
  Date failedAt;
}
