package tech.tinkmaster.fluent.service.pipeline;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.tinkmaster.fluent.common.entity.execution.Execution;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionUtils;
import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.common.entity.pipeline.Pipeline;
import tech.tinkmaster.fluent.common.entity.pipeline.PipelineGraph;
import tech.tinkmaster.fluent.common.exceptions.FluentPipelineCircleDetectedException;
import tech.tinkmaster.fluent.persistence.file.PipelineStorage;

@Service
public class PipelineService {

  @Autowired PipelineStorage storage;

  public List<String> list() {
    return this.storage.list();
  }

  public Pipeline get(String name) throws IOException {
    return this.storage.get(name);
  }

  public void updateOrCreate(Pipeline pipeline) throws IOException {
    if (pipeline.getStages() != null) {
      this.checkCircleInPipelineGraph(pipeline, pipeline.getStages().getBefore(), "Before");
      this.checkCircleInPipelineGraph(pipeline, pipeline.getStages().getExecute(), "Execute");
      this.checkCircleInPipelineGraph(pipeline, pipeline.getStages().getClean(), "Clean");
    }

    this.storage.updateOrCreate(pipeline);
  }

  public void delete(String name) throws IOException {
    this.storage.delete(name);
  }

  private void checkCircleInPipelineGraph(
      Pipeline pipeline, PipelineGraph graph, String stageName) {
    if (graph != null && graph.getOperators() != null && graph.getOperators().size() > 0) {
      Map<String, Operator> operators = new HashMap<>();
      graph.getOperators().forEach((k, v) -> operators.put(v, Operator.builder().build()));
      List<Pair<Integer, Integer>> cons = new LinkedList<>();
      if (graph.getConnections() != null) {
        graph
            .getConnections()
            .forEach(
                value -> {
                  String[] arr = value.split("->");
                  cons.add(Pair.of(Integer.valueOf(arr[0]), Integer.valueOf(arr[1])));
                });
      }
      Execution execution =
          ExecutionUtils.generateExecution(
              pipeline, operators, pipeline.getParameters(), pipeline.getEnvironment());
      if (ExecutionUtils.checkIfHasCircle(execution)) {
        throw new FluentPipelineCircleDetectedException(
            String.format(
                "Find circle in pipeline, please check your pipeline graph in stage %s.",
                stageName));
      }
    }
  }
}
