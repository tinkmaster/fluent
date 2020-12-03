package tech.tinkmaster.fluent.common.entity.execution;

import java.text.SimpleDateFormat;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import tech.tinkmaster.fluent.common.entity.operator.Operator;

@Getter
@Setter
public class ExecutionDiagram {
  public String name;
  public String pipelineName;
  public List<ExecutionDiagramNode> sources;
  public Map<Integer, ExecutionDiagramNode> nodes;
  public ExecutionDiagramNode currentNode;
  public ExecutionStatus status;
  public Date createdTime;
  public Map<String, String> result;

  public ExecutionDiagram() {
    this.result = new HashMap<>();
  }

  public ExecutionDiagram(String name, String pipelineName, Date createdTime) {
    this.name = name;
    this.pipelineName = pipelineName;
    this.createdTime = createdTime;
    this.result = new HashMap<>();
  }

  public ExecutionDiagram(
      String pipelineName,
      Map<Integer, Operator> operators,
      List<Pair<Integer, Integer>> connections) {
    HashMap<Integer, ExecutionDiagramNode> nodes = new HashMap<>();
    operators.forEach(
        (k, v) -> {
          nodes.put(k, new ExecutionDiagramNode(k, v));
        });

    connections.forEach(
        value -> {
          nodes.get(value.getKey()).addNext(nodes.get(value.getValue()));
          nodes.get(value.getValue()).addUpper(nodes.get(value.getKey()));
        });

    List<ExecutionDiagramNode> sources = new LinkedList<>();
    nodes.forEach(
        (k, v) -> {
          if (v.upstreamNodes.size() == 0) {
            sources.add(v);
          }
        });

    String dateStr = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
    this.name = pipelineName + "-" + dateStr;
    this.pipelineName = pipelineName;
    this.sources = sources;
    this.nodes = nodes;
    this.status = ExecutionStatus.CREATED;
    this.createdTime = new Date();
  }

  public ExecutionDiagramNode findNextNodeToRun() {
    if (this.currentNode != null
        && (this.currentNode.status == ExecutionStatus.RUNNING
            || this.currentNode.status == ExecutionStatus.WAITING_TO_BE_SCHEDULED)) {
      return this.currentNode;
    }
    if (this.status == ExecutionStatus.FINISHED || this.status == ExecutionStatus.FAILED) {
      return null;
    }

    if (this.currentNode == null) {
      this.currentNode =
          this.sources
              .stream()
              .filter(
                  node ->
                      node.status != ExecutionStatus.FINISHED
                          && node.status != ExecutionStatus.FAILED)
              .findFirst()
              .orElse(null);
      if (this.currentNode == null) {
        this.status = ExecutionStatus.FINISHED;
        return null;
      } else {
        return this.currentNode;
      }
    }

    if (this.currentNode.next == null || this.currentNode.next.size() == 0) {
      // firstly check if all source nodes have been in FINISHED status
      ExecutionDiagramNode nodeToRun =
          this.sources
              .stream()
              .filter(
                  n ->
                      n.status == ExecutionStatus.CREATED
                          || n.status == ExecutionStatus.WAITING_TO_BE_SCHEDULED)
              .findFirst()
              .orElse(null);
      if (nodeToRun == null) {
        this.status = ExecutionStatus.FINISHED;
        return null;
      } else {
        this.currentNode = nodeToRun;
        return this.currentNode;
      }
    } else {
      this.currentNode =
          this.nodes.get(
              this.currentNode
                  .next
                  .stream()
                  .filter(
                      v ->
                          this.nodes.get(v).status == ExecutionStatus.CREATED
                              || this.nodes.get(v).status
                                  == ExecutionStatus.WAITING_TO_BE_SCHEDULED)
                  .findFirst()
                  .orElse(null));
    }
    this.currentNode.status = ExecutionStatus.WAITING_TO_BE_SCHEDULED;
    return this.currentNode;
  }
}
