package tech.tinkmaster.fluent.common.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
@JsonPropertyOrder({
  "namespace",
  "name",
  "creationTimestamp",
  "modificationTimestamp",
  "labels",
  "annotations",
  "resourceVersion"
})
public class ObjectMetadata {
  @lombok.Builder.Default String namespace = "default";
  String name;

  @lombok.Builder.Default Instant creationTimestamp = Instant.now();
  @lombok.Builder.Default Instant modificationTimestamp = Instant.now();

  @lombok.Builder.Default Map<String, String> labels = new HashMap<>();
  @lombok.Builder.Default Map<String, String> annotations = new HashMap<>();

  @lombok.Builder.Default Integer resourceVersion = 1;
}
