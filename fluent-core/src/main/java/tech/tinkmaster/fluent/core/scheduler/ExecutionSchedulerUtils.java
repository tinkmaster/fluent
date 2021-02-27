package tech.tinkmaster.fluent.core.scheduler;

import static tech.tinkmaster.fluent.common.entity.execution.ExecutionStages.*;
import static tech.tinkmaster.fluent.common.entity.execution.ExecutionStatus.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import tech.tinkmaster.fluent.common.entity.execution.*;

public class ExecutionSchedulerUtils {
  public static Integer findNextNodeToExecute(Execution execution) {
    ExecutionGraph graph = ExecutionUtils.getCurrentExecutionGraph(execution);
    return find(graph);
  }

  private static Integer find(ExecutionGraph graph) {
    Map<Integer, ExecutionGraphNode> nodes = graph.getNodes();
    HashMap<Integer, Boolean> nodesHaveBeenExecuted = new HashMap<>();
    nodes.forEach(
        (id, node) -> {
          if (node.getStatus() == FINISHED) {
            nodesHaveBeenExecuted.put(id, Boolean.TRUE);
          }
        });
    ExecutionGraphNode res = ExecutionGraphNode.builder().build();
    res = res.withId(null);
    Iterator<Integer> idIterator = nodes.keySet().iterator();
    while (idIterator.hasNext()) {
      Integer id = idIterator.next();
      ExecutionGraphNode node = nodes.get(id);
      if (ExecutionStatus.needToRun(node.getStatus())) {
        if (node.getUpstreamNodes().size() == 0
            || node.getUpstreamNodes()
                .stream()
                .allMatch(upstreamNodeId -> nodesHaveBeenExecuted.get(upstreamNodeId) != null)) {
          res = res.withId(id);
        }
      }
    }
    return res.getId();
  }

  public static Execution updateExecutionGraphStatus(Execution execution) {
    ExecutionStages stages = execution.getStages();
    // first update each graph status
    ExecutionGraph before = stages.getBefore();
    ExecutionGraph execute = stages.getExecute();
    ExecutionGraph clean = stages.getClean();
    before = updateGraphStatus(before);

    // update current stage
    if (ExecutionStatus.noNeedToRun(before.getStatus())) {
      if (FAILED.equals(before.getStatus())) {
        // skip all nodes in execute
        execute = skipAllNodes(execute);
        clean = updateGraphStatus(clean);
        if (ExecutionStatus.needToRun(clean.getStatus())) {
          stages = stages.withCurrentExecutionStage(CLEAN_STAGE);
        } else {
          stages = stages.withCurrentExecutionStage(null);
        }
      } else {
        execute = updateGraphStatus(execute);
        if (ExecutionStatus.needToRun(execute.getStatus())) {
          stages = stages.withCurrentExecutionStage(EXECUTE_STAGE);
        } else if (ExecutionStatus.noNeedToRun(execute.getStatus())) {
          clean = updateGraphStatus(clean);
          if (ExecutionStatus.needToRun(clean.getStatus())) {
            stages = stages.withCurrentExecutionStage(CLEAN_STAGE);
          } else {
            stages = stages.withCurrentExecutionStage(null);
          }
        }
      }
    } else {
      stages = stages.withCurrentExecutionStage(BEFORE_STAGE);
    }

    stages = stages.withBefore(before).withExecute(execute).withClean(clean);
    execution = execution.withStages(stages);

    // update execution status
    List<ExecutionGraph> graphs = ExecutionUtils.getGraphs(execution);
    if (graphs.stream().allMatch(graph -> ExecutionStatus.noNeedToRun(graph.getStatus()))) {
      if (graphs.stream().anyMatch(graph -> FAILED.equals(graph.getStatus()))) {
        execution = execution.withStatus(FAILED);
      } else if (graphs.stream().allMatch(graph -> SKIPPED.equals(graph.getStatus()))) {
        execution = execution.withStatus(SKIPPED);
      } else {
        execution = execution.withStatus(FINISHED);
      }
    } else if (graphs.stream().allMatch(graph -> CREATED.equals(graph.getStatus()))) {
      execution = execution.withStatus(CREATED);
    } else {
      execution = execution.withStatus(RUNNING);
    }

    return execution;
  }

  private static ExecutionGraph skipAllNodes(ExecutionGraph graph) {
    ExecutionGraph finalGraph = graph;
    graph.getNodes().forEach((k, v) -> finalGraph.getNodes().put(k, v.withStatus(SKIPPED)));
    graph = graph.withStatus(SKIPPED);
    return graph;
  }

  private static ExecutionGraph updateGraphStatus(ExecutionGraph graph) {
    Map<Integer, ExecutionGraphNode> nodes = graph.getNodes();
    if (nodes.values().stream().allMatch(node -> node.getStatus().equals(FINISHED))) {
      graph = graph.withStatus(FINISHED);
    } else if (nodes.values().stream().anyMatch(node -> node.getStatus().equals(FAILED))) {
      graph = graph.withStatus(FAILED);
    } else if (nodes.values().stream().anyMatch(node -> node.getStatus().equals(CANCELLED))) {
      graph = graph.withStatus(CANCELLED);
    } else if (nodes.values().stream().allMatch(node -> node.getStatus().equals(SKIPPED))) {
      graph = graph.withStatus(SKIPPED);
    } else if (nodes.values().stream().allMatch(node -> node.getStatus().equals(CREATED))) {
      graph = graph.withStatus(CREATED);
    } else {
      graph = graph.withStatus(RUNNING);
    }

    return graph;
  }
}
