package tech.tinkmaster.fluent.api.server.resources.v1.execution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.tinkmaster.fluent.api.server.responses.ResponseEntity;
import tech.tinkmaster.fluent.common.entity.execution.Execution;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionOverview;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionUtils;
import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.common.entity.pipeline.Pipeline;
import tech.tinkmaster.fluent.common.entity.pipeline.PipelineUtils;
import tech.tinkmaster.fluent.common.exceptions.FluentNotFoundException;
import tech.tinkmaster.fluent.service.execution.ExecutionService;
import tech.tinkmaster.fluent.service.operator.OperatorService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/executions")
public class ExecutionController {

  @Autowired ExecutionService service;
  @Autowired OperatorService operatorService;

  @GetMapping(path = "{pipelineName}/diagram")
  public ResponseEntity list(@PathVariable("pipelineName") String pipelineName) throws IOException {
    List<Execution> diagrams = this.service.list(pipelineName);
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
    Execution operator = this.service.get(pipelineName, name);
    if (operator == null) {
      throw new FluentNotFoundException("Can't find execution named " + name);
    } else {
      return ResponseEntity.ok(operator);
    }
  }

  @PostMapping(path = "")
  public ResponseEntity updateOrCreate(@RequestBody Pipeline pipeline) throws IOException {
    Map<String, Operator> operatorMap = new HashMap<>();
    PipelineUtils.getAllOperatorsName(pipeline)
        .forEach(
            name -> {
              try {
                operatorMap.put(name, this.operatorService.get(name));
              } catch (IOException e) {
                throw new FluentNotFoundException("Didn't find operator " + name);
              }
            });

    Execution diagram =
        ExecutionUtils.generateExecution(
            pipeline, operatorMap, pipeline.getParameters(), pipeline.getEnvironment());
    this.service.updateOrCreate(diagram);
    return ResponseEntity.ok(diagram);
  }
}
