package tech.tinkmaster.fluent.common.entity.pipeline;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class Environment {
  String name;
  String host;
  int port;
  boolean isHttps;
  Map<String, String> replacements;

  public Environment deepClone() {
    return Environment.builder()
        .name(name)
        .host(host)
        .port(port)
        .isHttps(isHttps)
        .replacements(new HashMap<>(replacements))
        .build();
  }
}
