package tech.tinkmaster.fluent.common.entity.datastructure;

public abstract class FieldValidator {
  abstract boolean validate(Object object, String rules);
}
