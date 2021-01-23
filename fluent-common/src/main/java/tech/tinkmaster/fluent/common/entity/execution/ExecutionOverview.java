package tech.tinkmaster.fluent.common.entity.execution;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecutionOverview {

  Long totalExecutionTimes = 0L;
  Long averageUsedTimeMs = 0L;
  Long totalSuccessfulExecutionTimes = 0L;

  Integer totalExecutionNumber = 0;
  Integer successfulExecutionNumber = 0;
  Integer failedExecutionNumber = 0;
  Integer runningExecutionNumber = 0;
  Integer createdExecutionNumber = 0;
  Date lastExecution;
  Date lastSuccessfulExecution;
  Date lastFailedExecution;

  public void addSuccessfulExecutionTime(Long time) {
    this.totalSuccessfulExecutionTimes += time;
  }

  public void addTotalExecutionTime(Long time) {
    this.totalExecutionTimes += time;
  }
}
