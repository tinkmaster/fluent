package tech.tinkmaster.fluent.common.entity.execution;

import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.common.entity.pipeline.Pipeline;
import tech.tinkmaster.fluent.common.entity.pipeline.PipelineGraph;
import tech.tinkmaster.fluent.common.exceptions.FluentNotFoundException;

import java.text.SimpleDateFormat;
import java.util.*;

import static tech.tinkmaster.fluent.common.entity.execution.ExecutionStages.*;

public class ExecutionUtils {
  public static SimpleDateFormat smp = new SimpleDateFormat("yyMMddHHmmss");

  public static String generateExecutionName(String pipelineName) {
    return String.format("%s-%s", pipelineName, smp.format(new Date()));
  }

  public static Execution generateExecution(
      Pipeline pipeline,
      Map<String, Operator> operators,
      Map<String, String> parameters,
      String env) {
    ExecutionStages stages =
        ExecutionStages.builder()
            .before(
                translatePipelineGraphToFreshNewExecutionGraph(
                    pipeline.getStages().getBefore(), operators))
            .execute(
                translatePipelineGraphToFreshNewExecutionGraph(
                    pipeline.getStages().getExecute(), operators))
            .clean(
                translatePipelineGraphToFreshNewExecutionGraph(
                    pipeline.getStages().getClean(), operators))
            .build();

    return Execution.builder()
        .name(generateExecutionName(pipeline.getName()))
        .pipelineName(pipeline.getName())
        .status(ExecutionStatus.CREATED)
        .stages(stages)
        .createdTime(new Date())
        .parameters(parameters)
        .environment(env)
        .build();
  }

  public static Execution generateSimpleExecution(
      String name, String pipelineName, Date createdTime, ExecutionStatus status) {

    return Execution.builder()
        .name(name)
        .pipelineName(pipelineName)
        .status(status)
        .createdTime(createdTime)
        .build();
  }

  public static List<String> getAllOperatorsName(Execution execution) {
    List<String> result = new LinkedList<>();
    Iterator<ExecutionGraph> graphs = getGraphs(execution).iterator();
    while (graphs.hasNext()) {
      ExecutionGraph graph = graphs.next();
      graph.getNodes().forEach((k, v) -> result.add(v.getOperator().getName()));
    }
    return result;
  }

  public static ExecutionGraph translatePipelineGraphToFreshNewExecutionGraph(
      PipelineGraph graph, Map<String, Operator> operatorsMap) {
    HashMap<Integer, ExecutionGraphNode> nodes = new HashMap<>();
    graph
        .getOperators()
        .forEach(
            (k, v) -> {
              nodes.put(
                  k, ExecutionGraphNode.builder().id(k).operator(operatorsMap.get(v)).build());
            });
    graph
        .getConnections()
        .forEach(
            value -> {
              String[] arr = value.split("->");

              int source = Integer.parseInt(arr[0]);
              int target = Integer.parseInt(arr[1]);

              nodes.get(source).addNext(target);
              nodes.get(target).addUpper(source);
            });

    List<Integer> sources = new LinkedList<>();
    nodes.forEach(
        (k, v) -> {
          if (v.getUpstreamNodes().size() == 0) {
            sources.add(v.getId());
          }
        });
    return ExecutionGraph.builder().nodes(nodes).sources(sources).build();
  }

  public static List<ExecutionGraph> getGraphs(Execution execution) {
    return List.of(
        execution.getStages().getBefore(),
        execution.getStages().getExecute(),
        execution.getStages().getClean());
  }

  public static boolean checkIfHasCircle(Execution execution) {
    Iterator<ExecutionGraph> graphs = getGraphs(execution).iterator();
    boolean result = false;
    while (graphs.hasNext() && !result) {
      ExecutionGraph graph = graphs.next();

      Map<Integer, Boolean> nodesHaveBeenReviewed = new HashMap<>();
      Map<Integer, Boolean> nodesForDetection = new HashMap<>();
      for (int i = 0; i < graph.getSources().size(); i++) {
        ExecutionGraphNode node = graph.getNodes().get(graph.getSources().get(i));
        Map<Integer, Boolean> nodes = new HashMap<>();
        if (detectCircle(graph, node, nodes, nodesForDetection)) {
          result = true;
          break;
        }
        nodes.forEach((k, v) -> nodesHaveBeenReviewed.put(k, null));
      }
      if (result) {
        break;
      }
      result = nodesHaveBeenReviewed.size() != graph.getNodes().size();
    }
    return result;
  }

  private static boolean detectCircle(
      ExecutionGraph graph,
      ExecutionGraphNode node,
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
      if (detectCircle(
          graph, graph.getNodes().get(node.getNext().get(i)), nodesHaveBeenReviewed, nodes)) {
        return true;
      }
    }
    return false;
  }

  public static ExecutionGraph getCurrentExecutionGraph(Execution execution) {
    switch (execution.getStages().getCurrentExecutionStage()) {
      case BEFORE_STAGE:
        return execution.getStages().getBefore();
      case EXECUTE_STAGE:
        return execution.getStages().getExecute();
      case CLEAN_STAGE:
        return execution.getStages().getClean();
    }
    throw new FluentNotFoundException(
        "Can't get current execution graph, caused by NULL value in currentExecutionStag");
  }

  public static ExecutionGraph getExecutionGraph(Execution execution, String graphName) {
    switch (graphName) {
      case BEFORE_STAGE:
        return execution.getStages().getBefore();
      case EXECUTE_STAGE:
        return execution.getStages().getExecute();
      case CLEAN_STAGE:
        return execution.getStages().getClean();
    }
    throw new FluentNotFoundException(
        String.format("Can't get execution graph named %s", graphName));
  }

  public static Execution setExecutionGraph(
      Execution execution, ExecutionGraph graph, String graphName) {
    switch (graphName) {
      case BEFORE_STAGE:
        return execution.withStages(execution.getStages().withBefore(graph));
      case EXECUTE_STAGE:
        return execution.withStages(execution.getStages().withExecute(graph));
      case CLEAN_STAGE:
        return execution.withStages(execution.getStages().withClean(graph));
    }
    throw new FluentNotFoundException(
        String.format("Can't get execution graph named %s", graphName));
  }

  public static Execution setCurrentStage(Execution execution, String currentStage) {
    return execution.withStages(execution.getStages().withCurrentExecutionStage(currentStage));
  }

  public static void setGraphNodeStatus(
      Execution execution, String currentStage, Integer id, ExecutionStatus status) {
    Map<Integer, ExecutionGraphNode> nodes = getExecutionGraph(execution, currentStage).getNodes();
    nodes.put(id, nodes.get(id).withStatus(status));
  }

  public static void setGraphNodeUsedTime(
      Execution execution, String currentStage, Integer id, Long time) {
    Map<Integer, ExecutionGraphNode> nodes = getExecutionGraph(execution, currentStage).getNodes();
    nodes.put(id, nodes.get(id).withUsedTime(time));
  }
}
