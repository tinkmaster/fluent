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
  private String name;
  private String pipelineName;
  private String pipelineGroupName;
  private List<Integer> sources;
  private Map<Integer, ExecutionDiagramNode> nodes;
  private Integer currentNode;
  private ExecutionStatus status;
  private Date createdTime;
  private Map<String, String> results;

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
          if (v.getUpstreamNodes().size() == 0) {
            sources.add(v.getId());
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
          if (node.getStatus() == ExecutionStatus.FINISHED) {
            nodesHaveBeenExecuted.put(id, Boolean.TRUE);
          }
        });
    ExecutionDiagramNode res = new ExecutionDiagramNode();
    res.setId(null);
    this.nodes.forEach(
        (id, node) -> {
          if (node.getStatus() == ExecutionStatus.RUNNING
              || node.getStatus() == ExecutionStatus.CREATED
              || node.getStatus() == ExecutionStatus.WAITING_TO_BE_SCHEDULED) {
            if (node.getUpstreamNodes().size() == 0
                || node.getUpstreamNodes().stream()
                    .allMatch(
                        upstreamNodeId -> nodesHaveBeenExecuted.get(upstreamNodeId) != null)) {
              res.setId(id);
            }
          }
        });

    return res.getId();
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
    if (nodesForDetection.get(node.getId()) != null) {
      return true;
    } else {
      nodesForDetection.put(node.getId(), Boolean.TRUE);
      nodesHaveBeenReviewed.put(node.getId(), Boolean.TRUE);
    }
    for (int i = 0; i < node.getNext().size(); i++) {
      Map<Integer, Boolean> nodes = new HashMap<>(nodesForDetection);
      if (this.detectCircle(this.nodes.get(node.getNext().get(i)), nodesHaveBeenReviewed, nodes)) {
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
    return this.nodes.get(id).getStatus();
  }

  private List<Integer> getNodeNext(Integer id) {
    if (id == null) {
      return Collections.emptyList();
    }
    return this.nodes.get(id).getNext();
  }

  private void setCurrentNodeStatus(ExecutionStatus status) {
    this.getNode(this.currentNode).setStatus(status);
  }
}
