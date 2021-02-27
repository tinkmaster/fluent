package tech.tinkmaster.fluent.common.entity.execution;

public enum ExecutionStatus {
  CREATED,
  SKIPPED,
  RUNNING,
  FAILED,
  CANCELLED,
  FINISHED;

  public static boolean needToRun(ExecutionStatus status) {
    if (status == CREATED || status == RUNNING) {
      return true;
    }
    return false;
  }

  public static boolean noNeedToRun(ExecutionStatus status) {
    if (status == FAILED || status == FINISHED || status == SKIPPED || status == CANCELLED) {
      return true;
    }
    return false;
  }
}
