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
  Map<String, String> parameters;

  public Environment deepClone() {
    return Environment.builder()
        .name(this.name)
        .host(this.host)
        .port(this.port)
        .isHttps(this.isHttps)
        .parameters(new HashMap<>(this.parameters))
        .build();
  }
}
