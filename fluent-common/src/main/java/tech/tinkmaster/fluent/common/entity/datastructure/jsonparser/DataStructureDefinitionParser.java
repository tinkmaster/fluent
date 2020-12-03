package tech.tinkmaster.fluent.common.entity.datastructure.jsonparser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import tech.tinkmaster.fluent.common.FluentObjectMappers;
import tech.tinkmaster.fluent.common.entity.datastructure.DataStructureDefinition;

public class DataStructureDefinitionParser {
  private static ObjectMapper mapper = FluentObjectMappers.getNewConfiguredYamlMapper();

  public static String toYaml(DataStructureDefinition definition) throws JsonProcessingException {
    return mapper.writeValueAsString(definition);
  }

  public static <T> T fromYaml(String yaml, Class<T> clazz) throws IOException {
    return mapper.treeToValue(mapper.readTree(yaml), clazz);
  }
}
