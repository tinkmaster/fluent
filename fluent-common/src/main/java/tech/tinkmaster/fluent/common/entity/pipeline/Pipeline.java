package tech.tinkmaster.fluent.common.entity.pipeline;

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
  String environment;
}
