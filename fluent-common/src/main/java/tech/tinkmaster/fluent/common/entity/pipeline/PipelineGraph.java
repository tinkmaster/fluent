package tech.tinkmaster.fluent.common.entity.pipeline;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PipelineGraph {
  Map<Integer, String> operators;
  List<String> connections;
}
