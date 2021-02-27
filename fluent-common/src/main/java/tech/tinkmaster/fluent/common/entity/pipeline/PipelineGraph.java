package tech.tinkmaster.fluent.common.entity.pipeline;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PipelineGraph {
  Map<Integer, String> operators;
  List<String> connections;
}
