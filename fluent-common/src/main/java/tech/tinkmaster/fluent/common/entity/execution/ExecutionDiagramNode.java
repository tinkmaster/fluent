package tech.tinkmaster.fluent.common.entity.execution;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import tech.tinkmaster.fluent.common.entity.operator.Operator;

@Getter
@Setter
public class ExecutionDiagramNode {
  private Integer id;
  private List<Integer> upstreamNodes;
  private List<Integer> next;
  private Operator operator;
  private Date executionTime;
  private ExecutionStatus status;
  private Long usedTime;

  public ExecutionDiagramNode() {}

  public ExecutionDiagramNode(int id, Operator operator) {
    this.id = id;
    this.upstreamNodes = new LinkedList<>();
    this.next = new LinkedList<>();
    this.operator = operator;
    this.status = ExecutionStatus.CREATED;
  }

  public ExecutionDiagramNode addNext(ExecutionDiagramNode node) {
    this.next.add(node.id);
    return this;
  }

  public ExecutionDiagramNode addUpper(ExecutionDiagramNode node) {
    this.upstreamNodes.add(node.id);
    return this;
  }
}
