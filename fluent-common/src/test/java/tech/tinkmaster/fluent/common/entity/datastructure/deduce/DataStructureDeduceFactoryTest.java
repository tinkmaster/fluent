package tech.tinkmaster.fluent.common.entity.datastructure.deduce;

import java.util.HashMap;
import tech.tinkmaster.fluent.common.entity.datastructure.DataStructureDefinition;

public class DataStructureDeduceFactoryTest {
  HashMap<String, DataStructureDefinition> definitionHashMap = new HashMap<>();

  //  @Before
  //  public void setup() {
  //    definitionHashMap.put(
  //        DataStructureDefinitionFixture.definition.getMetadata().getName(),
  //        DataStructureDefinitionFixture.definition);
  //  }
  //
  //  @Test
  //  public void testInference() throws JsonProcessingException {
  //
  //    System.out.println(
  //        FluentObjectMappers.getNewConfiguredYamlMapper()
  //            .writeValueAsString(
  //                DataStructureDeduceFactory.deduceDataStructure(
  //                    DataStructureDefinitionFixture.definition, definitionHashMap)));
  //  }
}
