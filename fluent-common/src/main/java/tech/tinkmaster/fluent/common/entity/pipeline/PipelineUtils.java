package tech.tinkmaster.fluent.common.entity.pipeline;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PipelineUtils {
  public static List<String> getAllOperatorsName(Pipeline pipeline) {
    List<String> result = new LinkedList<>();

    Iterator<PipelineGraph> graphs = getAllGraphs(pipeline).iterator();
    while (graphs.hasNext()) {
      PipelineGraph graph = graphs.next();
      graph.getOperators().forEach((k, v) -> result.add(v));
    }

    return result;
  }

  public static List<PipelineGraph> getAllGraphs(Pipeline pipeline) {
    return List.of(
        pipeline.getStages().getBefore(),
        pipeline.getStages().getExecute(),
        pipeline.getStages().getClean());
  }
}
