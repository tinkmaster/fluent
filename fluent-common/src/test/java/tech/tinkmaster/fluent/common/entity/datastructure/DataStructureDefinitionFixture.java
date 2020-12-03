package tech.tinkmaster.fluent.common.entity.datastructure;

import java.util.Arrays;
import tech.tinkmaster.fluent.common.entity.ObjectMetadata;

public class DataStructureDefinitionFixture {
  public static DataStructureDefinition anotherDefinition =
      DataStructureDefinition.builder()
          .metadata(ObjectMetadata.builder().name("test").build())
          .spec(
              DataStructureDefinition.DataStructureDefinitionSpec.builder()
                  .fields(
                      Arrays.asList(
                          Field.builder()
                              .name("anotherDefinition")
                              .description("No description")
                              .required(true)
                              .type(FieldType.CHAR_SEQUENCE)
                              .build()))
                  .build())
          .build();

  public static DataStructureDefinition definition =
      DataStructureDefinition.builder()
          .metadata(ObjectMetadata.builder().name("test").build())
          .spec(
              DataStructureDefinition.DataStructureDefinitionSpec.builder()
                  .fields(
                      Arrays.asList(
                          Field.builder()
                              .name("field1")
                              .description("No description")
                              .required(true)
                              .type(FieldType.BOOLEAN)
                              .build(),
                          Field.builder()
                              .name("field2")
                              .description("No description")
                              .required(true)
                              .type(FieldType.NUMBER)
                              .build(),
                          Field.builder()
                              .name("field3")
                              .description("No description")
                              .required(true)
                              .type(FieldType.CHAR_SEQUENCE)
                              .build(),
                          Field.builder()
                              .name("anotherDS")
                              .description("No description")
                              .required(true)
                              .type(FieldType.DATA_STRUCTURE_REFERENCE)
                              .build()))
                  .build())
          .build();

  public static String dsdString = "apiVersion: ";
}
