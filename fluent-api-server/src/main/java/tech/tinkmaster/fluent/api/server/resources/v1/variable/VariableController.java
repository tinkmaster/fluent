package tech.tinkmaster.fluent.api.server.resources.v1.variable;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.tinkmaster.fluent.api.server.responses.ResponseEntity;
import tech.tinkmaster.fluent.common.entity.variable.Environment;
import tech.tinkmaster.fluent.common.entity.variable.Global;
import tech.tinkmaster.fluent.common.entity.variable.Secret;
import tech.tinkmaster.fluent.common.exceptions.FluentNotFoundException;
import tech.tinkmaster.fluent.service.variable.VariableService;

@RestController
@RequestMapping("/api/v1/variables")
public class VariableController {

  @Autowired VariableService service;

  @GetMapping(path = "/environments")
  public ResponseEntity listEnv() {
    return ResponseEntity.ok(this.service.listEnv());
  }

  @GetMapping(path = "/environments/{name}")
  public ResponseEntity getEnv(@PathVariable("name") String name) throws IOException {
    Environment env = this.service.getEnv(name);
    if (env == null) {
      throw new FluentNotFoundException("Can't find environment named " + name);
    } else {
      return ResponseEntity.ok(env);
    }
  }

  @PostMapping(path = "/environments")
  public ResponseEntity updateOrCreateEnv(@RequestBody Environment env) throws IOException {
    this.service.updateOrCreateEnv(env);
    return ResponseEntity.ok(null);
  }

  @DeleteMapping(path = "/environments/{name}")
  public ResponseEntity deleteEnv(@PathVariable("name") String name) throws IOException {
    this.service.deleteEnv(name);
    return ResponseEntity.ok(null);
  }

  @GetMapping(path = "/secrets")
  public ResponseEntity listSecret() {
    return ResponseEntity.ok(this.service.listSecrets());
  }

  @PostMapping(path = "/secrets")
  public ResponseEntity updateOrCreateSecret(@RequestBody Secret secret) throws IOException {
    this.service.createOrUpdateSecrets(secret);
    return ResponseEntity.ok(null);
  }

  @DeleteMapping(path = "/secrets/{name}")
  public ResponseEntity deleteSecret(@PathVariable("name") String name) throws IOException {
    this.service.deleteSecret(name);
    return ResponseEntity.ok(null);
  }

  @GetMapping(path = "/globals")
  public ResponseEntity getGlobals() throws IOException {
    return ResponseEntity.ok(this.service.getGlobal());
  }

  @PostMapping(path = "/globals")
  public ResponseEntity updateOrCreateGlobal(@RequestBody Global global) throws IOException {
    this.service.updateGlobal(global);
    return ResponseEntity.ok(global);
  }
}
