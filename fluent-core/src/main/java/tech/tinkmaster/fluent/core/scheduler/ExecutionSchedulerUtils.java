package tech.tinkmaster.fluent.core.scheduler;

import org.apache.commons.lang3.tuple.Pair;
import tech.tinkmaster.fluent.common.entity.execution.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static tech.tinkmaster.fluent.common.entity.execution.ExecutionStages.BEFORE_STAGE;

public class ExecutionSchedulerUtils {
  public static Pair<String, Integer> findNextNodeToExecute(Execution execution) {
    ExecutionStages stages = execution.getStages();
    ExecutionGraph before = stages.getBefore();
    ExecutionGraph execute = stages.getExecute();
    ExecutionGraph clean = stages.getClean();

    // first check stage before
    if (before.getStatus() == ExecutionStatus.FAILED) {
      return Pair.of(ExecutionStages.CLEAN_STAGE, find(clean));
    } else if (before.getStatus() == ExecutionStatus.RUNNING) {
      return Pair.of(BEFORE_STAGE, find(before));
    } else if (before.getStatus() == ExecutionStatus.FINISHED) {
      if (execute.getStatus() == ExecutionStatus.CREATED
          || execute.getStatus() == ExecutionStatus.WAITING_TO_BE_SCHEDULED
          || execute.getStatus() == ExecutionStatus.RUNNING) {
        return Pair.of(ExecutionStages.EXECUTE_STAGE, find(execute));
      } else {
        return Pair.of(ExecutionStages.CLEAN_STAGE, find(clean));
      }
    }

    // if no more nodes are needed to be executed
    return null;
  }

  private static Integer find(ExecutionGraph graph) {
    Map<Integer, ExecutionGraphNode> nodes = graph.getNodes();
    HashMap<Integer, Boolean> nodesHaveBeenExecuted = new HashMap<>();
    nodes.forEach(
        (id, node) -> {
          if (node.getStatus() == ExecutionStatus.FINISHED) {
            nodesHaveBeenExecuted.put(id, Boolean.TRUE);
          }
        });
    ExecutionGraphNode res = ExecutionGraphNode.builder().build();
    res = res.withId(null);
    Iterator<Integer> idIterator = nodes.keySet().iterator();
    while (idIterator.hasNext()) {
      Integer id = idIterator.next();
      ExecutionGraphNode node = nodes.get(id);
      if (node.getStatus() == ExecutionStatus.RUNNING
          || node.getStatus() == ExecutionStatus.CREATED
          || node.getStatus() == ExecutionStatus.WAITING_TO_BE_SCHEDULED) {
        if (node.getUpstreamNodes().size() == 0
            || node.getUpstreamNodes().stream()
                .allMatch(upstreamNodeId -> nodesHaveBeenExecuted.get(upstreamNodeId) != null)) {
          res = res.withId(id);
        }
      }
    }
    return res.getId();
  }
}
