package tech.tinkmaster.fluent.api.server.resources.v1.operator;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.tinkmaster.fluent.api.server.exceptions.FluentNotFoundException;
import tech.tinkmaster.fluent.api.server.responses.ResponseEntity;
import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.service.operator.OperatorService;

@RestController
@RequestMapping("/api/v1/operators")
public class OperatorController {

  @Autowired OperatorService service;

  @GetMapping(path = "")
  public ResponseEntity list() {
    return ResponseEntity.ok(this.service.list());
  }

  @GetMapping(path = "{name}")
  public ResponseEntity get(@PathVariable("name") String name) throws IOException {
    Operator operator = this.service.get(name);
    if (operator == null) {
      throw new FluentNotFoundException("Can't find operator named " + name);
    } else {
      return ResponseEntity.ok(operator);
    }
  }

  @PostMapping(path = "")
  public ResponseEntity updateOrCreate(@RequestBody Operator operator) throws IOException {
    this.service.updateOrCreate(operator);
    return ResponseEntity.ok(null);
  }

  @DeleteMapping(path = "{name}")
  public ResponseEntity delete(@PathVariable("name") String name) throws IOException {
    this.service.delete(name);
    return ResponseEntity.ok(null);
  }
}
