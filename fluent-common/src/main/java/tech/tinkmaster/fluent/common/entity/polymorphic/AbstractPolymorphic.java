package tech.tinkmaster.fluent.common.entity.polymorphic;

public abstract class AbstractPolymorphic implements Polymorphic {
  static class UnknownKind implements Polymorphic {
    public static final String KIND = "unknown";

    @Override
    public String getKind() {
      return KIND;
    }

    @Override
    public void setKind(String ignored) {}
  }
}
