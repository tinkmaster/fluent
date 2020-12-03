package tech.tinkmaster.fluent.common.entity.datastructure;

public enum FieldType {
  NUMBER,
  BOOLEAN,
  CHAR_SEQUENCE,
  ARRAY_NUMBER,
  ARRAY_BOOLEAN,
  ARRAY_CHAR_SEQUENCE,
  DATA_STRUCTURE_REFERENCE;

  FieldValidator validator;

  FieldType() {
    this.validator = null;
  }
}
