package tech.tinkmaster.fluent.common.entity.pipeline;

import java.util.Map;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class Pipeline {
  String name;
  PipelineStages stages;
  Map<String, String> parameters;
  String environment;
}
