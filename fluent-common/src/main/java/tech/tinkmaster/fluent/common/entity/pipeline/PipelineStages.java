package tech.tinkmaster.fluent.common.entity.pipeline;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PipelineStages {
  private PipelineGraph before;
  private PipelineGraph execute;
  private PipelineGraph clean;
}
