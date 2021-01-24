package tech.tinkmaster.fluent.common.exceptions;

public class FluentSecretEncryptException extends RuntimeException {
  public FluentSecretEncryptException(String secretName, Exception e) {
    super(String.format("Failed to encrypt secret, name:[%s]", secretName), e);
  }
}
