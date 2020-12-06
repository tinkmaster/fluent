package tech.tinkmaster.fluent.common.entity.execution;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import tech.tinkmaster.fluent.common.entity.operator.Operator;

import java.text.SimpleDateFormat;
import java.util.*;

@Getter
@Setter
public class ExecutionDiagram {
  public String name;
  public String pipelineName;
  public String pipelineGroupName;
  public List<Integer> sources;
  public Map<Integer, ExecutionDiagramNode> nodes;
  public Integer currentNode;
  public ExecutionStatus status;
  public Date createdTime;
  public Map<String, String> results;

  public ExecutionDiagram() {
    this.results = new HashMap<>();
  }

  public ExecutionDiagram(String name, String pipelineName, Date createdTime) {
    this.name = name;
    this.pipelineName = pipelineName;
    this.createdTime = createdTime;
    this.results = new HashMap<>();
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

    List<Integer> sources = new LinkedList<>();
    nodes.forEach(
        (k, v) -> {
          if (v.upstreamNodes.size() == 0) {
            sources.add(v.id);
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

  public Integer findNextNodeToRun() {
    HashMap<Integer, Boolean> nodesHaveBeenExecuted = new HashMap<>();
    this.nodes.forEach(
        (id, node) -> {
          if (node.status == ExecutionStatus.FINISHED) {
            nodesHaveBeenExecuted.put(id, Boolean.TRUE);
          }
        });
    ExecutionDiagramNode res = new ExecutionDiagramNode();
    res.id = null;
    this.nodes.forEach(
        (id, node) -> {
          if (node.status == ExecutionStatus.RUNNING
              || node.status == ExecutionStatus.CREATED
              || node.status == ExecutionStatus.WAITING_TO_BE_SCHEDULED) {
            if (node.upstreamNodes.size() == 0
                || node.upstreamNodes.stream()
                    .allMatch(
                        upstreamNodeId -> nodesHaveBeenExecuted.get(upstreamNodeId) != null)) {
              res.id = id;
            }
          }
        });

    return res.id;
  }

  public boolean checkIfHasCircle() {
    Map<Integer, Boolean> nodesHaveBeenReviewed = new HashMap<>();
    Map<Integer, Boolean> nodesForDetection = new HashMap<>();
    for (int i = 0; i < this.sources.size(); i++) {
      ExecutionDiagramNode node = this.nodes.get(this.sources.get(i));
      Map<Integer, Boolean> nodes = new HashMap<>();
      if (this.detectCircle(node, nodes, nodesForDetection)) {
        return true;
      }
      nodes.forEach((k, v) -> nodesHaveBeenReviewed.put(k, null));
    }
    return nodesHaveBeenReviewed.size() != this.nodes.size();
  }

  private boolean detectCircle(
      ExecutionDiagramNode node,
      Map<Integer, Boolean> nodesHaveBeenReviewed,
      Map<Integer, Boolean> nodesForDetection) {
    if (nodesForDetection.get(node.id) != null) {
      return true;
    } else {
      nodesForDetection.put(node.id, Boolean.TRUE);
      nodesHaveBeenReviewed.put(node.id, Boolean.TRUE);
    }
    for (int i = 0; i < node.next.size(); i++) {
      Map<Integer, Boolean> nodes = new HashMap<>(nodesForDetection);
      if (this.detectCircle(this.nodes.get(node.next.get(i)), nodesHaveBeenReviewed, nodes)) {
        return true;
      }
    }
    return false;
  }

  private ExecutionDiagramNode getNode(Integer id) {
    if (id == null) {
      return null;
    }
    return this.nodes.get(id);
  }

  private ExecutionStatus getStatus(Integer id) {
    if (id == null) {
      return null;
    }
    return this.nodes.get(id).status;
  }

  private List<Integer> getNodeNext(Integer id) {
    if (id == null) {
      return Collections.emptyList();
    }
    return this.nodes.get(id).next;
  }

  private void setCurrentNode(Integer id) {
    this.currentNode = id;
  }

  private void setCurrentNodeStatus(ExecutionStatus status) {
    this.getNode(this.currentNode).status = status;
  }
}
