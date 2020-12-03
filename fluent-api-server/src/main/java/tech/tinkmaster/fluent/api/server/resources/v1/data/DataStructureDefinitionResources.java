package tech.tinkmaster.fluent.api.server.resources.v1.data;

import org.springframework.web.bind.annotation.*;
import tech.tinkmaster.fluent.common.entity.datastructure.DataStructureDefinition;
import tech.tinkmaster.fluent.service.data.DataStructureDefinitionService;

@RequestMapping("/api/v1/namespaces/{namespace}/datastructuredefinition")
@RestController
public class DataStructureDefinitionResources {

  DataStructureDefinitionService service;

  @RequestMapping(value = "/{name}")
  public DataStructureDefinition queryDataStructureDefinition(
      @PathVariable("namespace") String namespace, @PathVariable("name") String name) {

    return null;
  }

  @RequestMapping(value = "", method = RequestMethod.POST)
  public void addNewDataStructureDefinition(
      @PathVariable("namespace") String namespace,
      @RequestBody DataStructureDefinition definition) {

    // service.save(definition);
  }
}
