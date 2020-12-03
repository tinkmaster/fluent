package tech.tinkmaster.fluent.core.pipeline.failure;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

@Value
@Wither
@Builder(toBuilder = true, builderClassName = "Builder")
public class Failure {
  String message;
  String reason;
  Instant failedAt;
}
