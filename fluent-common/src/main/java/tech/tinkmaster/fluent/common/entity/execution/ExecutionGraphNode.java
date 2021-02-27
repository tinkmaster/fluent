package tech.tinkmaster.fluent.common.entity.execution;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import lombok.With;
import tech.tinkmaster.fluent.common.entity.operator.Operator;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class ExecutionGraphNode {
  Integer id;
  @Default List<Integer> upstreamNodes = new LinkedList<>();
  @Default List<Integer> next = new LinkedList<>();
  Operator operator;
  Date executionTime;
  @Default ExecutionStatus status = ExecutionStatus.CREATED;
  @Default Long usedTime = 0L;

  public ExecutionGraphNode addNext(int id) {
    this.next.add(id);
    return this;
  }

  public ExecutionGraphNode addUpper(int id) {
    this.upstreamNodes.add(id);
    return this;
  }
}
