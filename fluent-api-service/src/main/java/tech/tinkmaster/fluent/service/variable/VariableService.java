package tech.tinkmaster.fluent.service.variable;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.tinkmaster.fluent.common.entity.variable.Environment;
import tech.tinkmaster.fluent.common.entity.variable.Global;
import tech.tinkmaster.fluent.common.entity.variable.Secret;
import tech.tinkmaster.fluent.common.exceptions.FluentSecretDecryptException;
import tech.tinkmaster.fluent.common.exceptions.FluentSecretEncryptException;
import tech.tinkmaster.fluent.persistence.file.EnvironmentStorage;
import tech.tinkmaster.fluent.persistence.file.GlobalVariableStorage;
import tech.tinkmaster.fluent.persistence.file.SecretStorage;

@Service
public class VariableService {
  @Autowired EnvironmentStorage envStorage;
  @Autowired GlobalVariableStorage globalStorage;
  @Autowired SecretStorage secretStorage;

  public List<String> listEnv() {
    return this.envStorage.list();
  }

  public Environment getEnv(String name) throws IOException {
    return this.envStorage.get(name);
  }

  public void updateOrCreateEnv(Environment env) throws IOException {
    this.envStorage.updateOrCreate(env);
  }

  public void deleteEnv(String name) throws IOException {
    this.envStorage.delete(name);
  }

  public Global getGlobal() throws IOException {
    return this.globalStorage.get();
  }

  public void updateGlobal(Global global) throws IOException {
    this.globalStorage.update(global);
  }

  public List<String> listSecrets() {
    return this.secretStorage.list();
  }

  public void createOrUpdateSecrets(Secret secret) throws IOException {
    try {
      this.secretStorage.updateOrCreate(secret);
    } catch (GeneralSecurityException e) {
      throw new FluentSecretEncryptException(secret.getName(), e);
    }
  }

  /**
   * This method is only allowed used in variable resolver
   *
   * @param name secret name
   * @return deciphered secret
   */
  public Secret getTranslucentSecret(String name) throws IOException {
    try {
      return this.secretStorage.get(name);
    } catch (GeneralSecurityException e) {
      throw new FluentSecretDecryptException(name, e);
    }
  }

  public void deleteSecret(String name) throws IOException {
    this.secretStorage.delete(name);
  }
}
