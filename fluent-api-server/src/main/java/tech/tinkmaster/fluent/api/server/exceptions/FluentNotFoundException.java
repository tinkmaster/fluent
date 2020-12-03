package tech.tinkmaster.fluent.api.server.exceptions;

public class FluentNotFoundException extends RuntimeException {
  public FluentNotFoundException(String message) {
    super(message);
  }
}
