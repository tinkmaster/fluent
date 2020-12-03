package tech.tinkmaster.fluent.common.entity.pipeline;

public enum PipelineStatus {
  INITIALIZED,

  WAITING_FOR_SCHEDULER,

  RUNNING,

  FAILED,

  SUCCEED
}
