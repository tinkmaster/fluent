package tech.tinkmaster.fluent.core.pipeline;

import java.util.HashMap;
import java.util.Map;

public class ExecutionSchedulerContext {
  public Map<String, String> executionInfo;

  public ExecutionSchedulerContext() {
    this.executionInfo = new HashMap<>();
  }
}
