package tech.tinkmaster.fluent.common.entity.datastructure;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;
import lombok.With;

@Value
@With
@lombok.Builder(toBuilder = true, builderClassName = "Builder")
@JsonPropertyOrder({"name", "required", "description", "type", "defaults"})
public class Field {
  String name;
  boolean required;
  String description;
  FieldType type;
  String defaults;
}
