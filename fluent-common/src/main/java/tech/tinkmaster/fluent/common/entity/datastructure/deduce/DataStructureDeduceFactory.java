package tech.tinkmaster.fluent.common.entity.datastructure.deduce;

import java.util.HashMap;
import java.util.Map;
import tech.tinkmaster.fluent.common.entity.datastructure.DataStructureDefinition;
import tech.tinkmaster.fluent.common.entity.datastructure.Field;
import tech.tinkmaster.fluent.common.entity.datastructure.FieldType;

/**
 * This class is responsible for deserialize data structure definition
 *
 * <p>Usually used by translating front-end messages
 */
public class DataStructureDeduceFactory {
  Map<Integer, Map<String, Object>> inferenceCache = new HashMap<>();

  public static Map<String, Object> deduceDataStructure(
      DataStructureDefinition definition, Map<String, DataStructureDefinition> allDefinitions) {
    Map<String, Object> deduceResult = new HashMap<>();

    for (Field field : definition.getSpec().getFields()) {
      if (field.getType().equals(FieldType.DATA_STRUCTURE_REFERENCE)) {
        deduceResult.put(
            field.getName(), deduceDataStructure(allDefinitions.get(null), allDefinitions));
      } else {
        deduceResult.put(field.getName(), null);
      }
    }

    return deduceResult;
  }
}
