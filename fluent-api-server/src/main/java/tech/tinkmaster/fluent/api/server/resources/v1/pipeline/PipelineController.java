package tech.tinkmaster.fluent.api.server.resources.v1.pipeline;

import java.io.IOException;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.tinkmaster.fluent.api.server.responses.ResponseEntity;
import tech.tinkmaster.fluent.common.entity.pipeline.Pipeline;
import tech.tinkmaster.fluent.common.exceptions.FluentEntityIllegalException;
import tech.tinkmaster.fluent.common.exceptions.FluentNotFoundException;
import tech.tinkmaster.fluent.service.pipeline.PipelineService;

@RestController
@RequestMapping("/api/v1/pipelines")
public class PipelineController {

  @Autowired PipelineService service;

  @GetMapping(path = "")
  public ResponseEntity list() {
    return ResponseEntity.ok(this.service.list());
  }

  @GetMapping(path = "{name}")
  public ResponseEntity get(@PathVariable("name") String name) throws IOException {
    Pipeline operator = this.service.get(name);
    if (operator == null) {
      throw new FluentNotFoundException("Can't find operator named " + name);
    } else {
      return ResponseEntity.ok(operator);
    }
  }

  @PostMapping(path = "")
  public ResponseEntity updateOrCreate(@RequestBody Pipeline pipeline) throws IOException {
    String[] forbiddenKeys =
        new String[] {"env.", "pipeline.", "currentEnv.", "operators", "global.", "secret."};
    // check pipeline parameters formation
    if (pipeline.getParameters() != null && pipeline.getParameters().size() > 0) {
      if (!pipeline
          .getParameters()
          .keySet()
          .stream()
          .allMatch(
              p ->
                  p.matches("[a-z0-9A-Z]+")
                      && Arrays.stream(forbiddenKeys).noneMatch(p::startsWith))) {
        throw new FluentEntityIllegalException(
            "Illegal pipeline parameters, only letters and number are allowed.");
      }
    }
    this.service.updateOrCreate(pipeline);
    return ResponseEntity.ok(pipeline);
  }

  @DeleteMapping(path = "{name}")
  public ResponseEntity delete(@PathVariable("name") String name) throws IOException {
    this.service.delete(name);
    return ResponseEntity.ok(null);
  }
}
