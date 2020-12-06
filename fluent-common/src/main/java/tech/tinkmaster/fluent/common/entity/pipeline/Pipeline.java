package tech.tinkmaster.fluent.common.entity.pipeline;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class Pipeline {
  String name;
  Map<Integer, String> operators;
  List<String> connections;
  Map<String, String> parameters;

  public Pipeline deepClone() {
    return Pipeline.builder()
        .operators(new HashMap<>(this.operators))
        .parameters(new HashMap<>(this.parameters))
        .connections(new LinkedList<>(this.connections))
        .build();
  }
}
