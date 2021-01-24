package tech.tinkmaster.fluent.common.exceptions;

public class FluentSecretDecryptException extends RuntimeException {
  public FluentSecretDecryptException(String secretName, Exception e) {
    super(String.format("Failed to decrypt secret, name:[%s]", secretName), e);
  }
}
