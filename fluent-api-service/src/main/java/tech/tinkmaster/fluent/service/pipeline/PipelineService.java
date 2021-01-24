package tech.tinkmaster.fluent.service.pipeline;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagram;
import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.common.entity.pipeline.Pipeline;
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
    Map<Integer, Operator> operators = new HashMap<>();
    if (pipeline.getOperators() != null && pipeline.getOperators().size() != 0) {
      pipeline.getOperators().forEach((k, v) -> operators.put(k, Operator.builder().build()));
      List<Pair<Integer, Integer>> cons = new LinkedList<>();
      pipeline
          .getConnections()
          .forEach(
              value -> {
                String[] arr = value.split("->");
                cons.add(Pair.of(Integer.valueOf(arr[0]), Integer.valueOf(arr[1])));
              });
      ExecutionDiagram executionDiagram =
          new ExecutionDiagram(pipeline.getName(), operators, cons, pipeline.getEnvironment());
      if (executionDiagram.checkIfHasCircle()) {
        throw new FluentPipelineCircleDetectedException(
            "Find circle in pipeline, please check your pipeline graph.");
      }
    }
    this.storage.updateOrCreate(pipeline);
  }

  public void delete(String name) throws IOException {
    this.storage.delete(name);
  }
}
