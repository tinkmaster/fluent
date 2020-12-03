package tech.tinkmaster.fluent.common.entity.execution;

import java.util.*;
import tech.tinkmaster.fluent.common.entity.operator.Operator;

public class ExecutionDiagramNode {
  public int id;
  public List<Integer> upstreamNodes;
  public List<Integer> next;
  public Operator operator;
  public Map<String, String> executionResult;
  public Date executionTime;
  public ExecutionStatus status;
  public Long usedTime;

  public ExecutionDiagramNode() {}

  public ExecutionDiagramNode(int id, Operator operator) {
    this.id = id;
    this.upstreamNodes = new LinkedList<>();
    this.next = new LinkedList<>();
    this.executionResult = new HashMap<>();
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

  public void setExecutionTime(Date executionTime) {
    this.executionTime = executionTime;
  }
}
