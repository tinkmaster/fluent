package tech.tinkmaster.fluent.common.entity.execution;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class ExecutionStages {
  public static final String BEFORE_STAGE = "BEFORE";
  public static final String EXECUTE_STAGE = "EXECUTE";
  public static final String CLEAN_STAGE = "CLEAN";
  private ExecutionGraph before;
  private ExecutionGraph execute;
  private ExecutionGraph clean;
  private String currentExecutionStage;
}
