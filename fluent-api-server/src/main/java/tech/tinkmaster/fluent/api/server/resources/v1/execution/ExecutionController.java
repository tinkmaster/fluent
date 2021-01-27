package tech.tinkmaster.fluent.api.server.resources.v1.execution;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.tinkmaster.fluent.api.server.responses.ResponseEntity;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagram;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionOverview;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionStatus;
import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.common.entity.pipeline.Pipeline;
import tech.tinkmaster.fluent.common.exceptions.FluentNotFoundException;
import tech.tinkmaster.fluent.service.execution.ExecutionService;
import tech.tinkmaster.fluent.service.operator.OperatorService;

@RestController
@RequestMapping("/api/v1/executions")
public class ExecutionController {

  @Autowired ExecutionService service;
  @Autowired OperatorService operatorService;

  @GetMapping(path = "{pipelineName}/diagram")
  public ResponseEntity list(@PathVariable("pipelineName") String pipelineName) throws IOException {
    List<ExecutionDiagram> diagrams = this.service.list(pipelineName);
    if (diagrams == null) {
      throw new FluentNotFoundException("Can't find execution named " + pipelineName);
    } else {
      return ResponseEntity.ok(diagrams);
    }
  }

  @GetMapping(path = "{pipelineName}/overview")
  public ResponseEntity overview(@PathVariable("pipelineName") String pipelineName)
      throws IOException {
    ExecutionOverview overview = this.service.getOverview(pipelineName);
    if (overview == null) {
      throw new FluentNotFoundException("Can't find pipeline named " + pipelineName);
    } else {
      return ResponseEntity.ok(overview);
    }
  }

  @GetMapping(path = "{pipelineName}/diagram/{name}")
  public ResponseEntity get(
      @PathVariable("pipelineName") String pipelineName, @PathVariable("name") String name)
      throws IOException {
    ExecutionDiagram operator = this.service.get(pipelineName, name);
    if (operator == null) {
      throw new FluentNotFoundException("Can't find execution named " + name);
    } else {
      return ResponseEntity.ok(operator);
    }
  }

  @PostMapping(path = "")
  public ResponseEntity updateOrCreate(@RequestBody Pipeline pipeline) throws IOException {
    Map<Integer, Operator> operatorMap = new HashMap<>();
    pipeline
        .getOperators()
        .forEach(
            (k, v) -> {
              try {
                operatorMap.put(k, this.operatorService.get(v));
              } catch (IOException e) {
                throw new FluentNotFoundException("Didn't find operator " + v);
              }
            });
    List<Pair<Integer, Integer>> cons = new LinkedList<>();
    pipeline
        .getConnections()
        .forEach(
            value -> {
              String[] arr = value.split("->");
              cons.add(Pair.of(Integer.valueOf(arr[0]), Integer.valueOf(arr[1])));
            });
    System.out.println(pipeline.getEnvironment());
    ExecutionDiagram diagram =
        new ExecutionDiagram(
            pipeline.getName(),
            operatorMap,
            cons,
            pipeline.getEnvironment(),
            pipeline.getParameters());
    diagram.setStatus(ExecutionStatus.WAITING_TO_BE_SCHEDULED);
    this.service.updateOrCreate(diagram);
    return ResponseEntity.ok(diagram);
  }
}
