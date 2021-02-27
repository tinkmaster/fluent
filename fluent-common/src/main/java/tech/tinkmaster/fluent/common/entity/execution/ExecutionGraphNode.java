package tech.tinkmaster.fluent.common.entity.execution;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import tech.tinkmaster.fluent.common.entity.operator.Operator;

import java.util.Date;
import java.util.List;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class ExecutionGraphNode {
  Integer id;
  List<Integer> upstreamNodes;
  List<Integer> next;
  Operator operator;
  Date executionTime;
  ExecutionStatus status;
  Long usedTime;

  public ExecutionGraphNode addNext(int id) {
    this.next.add(id);
    return this;
  }

  public ExecutionGraphNode addUpper(int id) {
    this.upstreamNodes.add(id);
    return this;
  }
}
