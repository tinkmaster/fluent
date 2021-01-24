package tech.tinkmaster.fluent.persistence.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tech.tinkmaster.fluent.common.FluentObjectMappers;
import tech.tinkmaster.fluent.common.entity.variable.Secret;

/** ${base}/namespaces/default/variables/secrets/${name} */
@Component
public class SecretStorage {
  private static final Logger LOG = LoggerFactory.getLogger(SecretStorage.class);
  private static final String SECRET_CIPHER_FILE_PATH =
      "namespaces/default/variables/secret.cipher";

  @Value("${data.dir}")
  String baseDir;

  byte[] secretCipherBytes;

  ObjectMapper mappers = FluentObjectMappers.getNewConfiguredYamlMapper();

  @PostConstruct
  public void init() throws IOException {
    Files.createDirectories(Paths.get(this.baseDir, this.getSecretFilePath()));

    // initialize the secret file
    File file = Paths.get(this.baseDir, SECRET_CIPHER_FILE_PATH).toFile();
    if (!file.exists()) {
      if (file.createNewFile()) {
        String keyStr = this.generateRandomKey();
        FileUtils.writeStringToFile(file, keyStr);
        this.secretCipherBytes = keyStr.getBytes();
      } else {
        LOG.error("Failed to create secret cipher file, file path: " + file.getAbsolutePath());
        throw new RuntimeException(
            "Failed to create secret cipher file, file path: " + file.getAbsolutePath());
      }
    } else {
      this.secretCipherBytes = FileUtils.readFileToByteArray(file);
    }
  }

  public List<String> list() {
    File file = Paths.get(this.baseDir, this.getSecretFilePath()).toFile();
    if (file.exists() && file.isDirectory()) {
      return FileUtils.listFiles(file, null, false)
          .stream()
          .map(File::getName)
          .collect(Collectors.toList());
    } else {
      return Collections.emptyList();
    }
  }

  public Secret get(String secretName) throws IOException, GeneralSecurityException {
    File file = Paths.get(this.baseDir, this.getSecretFilePath(), secretName).toFile();
    if (file.exists()) {
      Secret secret =
          this.mappers.readValue(
              FileUtils.readFileToString(file, Charset.defaultCharset()), Secret.class);
      secret.setValue(
          Arrays.toString(decrypt(this.secretCipherBytes, secret.getValue().getBytes())));
      return secret;
    } else {
      return null;
    }
  }

  public void updateOrCreate(Secret secret) throws IOException, GeneralSecurityException {
    File file = Paths.get(this.baseDir, this.getSecretFilePath(), secret.getName()).toFile();
    if (!file.exists()) {
      this.createFile(file);
    }
    secret.setValue(
        Base64.getEncoder()
            .encodeToString(encrypt(this.secretCipherBytes, secret.getValue().getBytes())));
    String operatorAsYaml = this.mappers.writeValueAsString(secret);
    FileUtils.writeStringToFile(file, operatorAsYaml, Charset.defaultCharset());
  }

  public boolean delete(String secretName) throws IOException {
    File fileToDelete = Paths.get(this.baseDir, this.getSecretFilePath(), secretName).toFile();
    return fileToDelete.delete();
  }

  private String getSecretFilePath() {
    return "namespaces/default/variables/secrets";
  }

  void createFile(File file) throws IOException {
    Stack<File> files = new Stack<>();
    do {
      files.push(file);
      file = file.getParentFile();
    } while (file != null);

    while (!files.empty()) {
      file = files.pop();
      if (!file.exists() && !files.empty()) {
        file.mkdir();
      } else if (!file.exists()) {
        file.createNewFile();
      }
    }
  }

  public static byte[] encrypt(byte[] key, byte[] input) throws GeneralSecurityException {
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
    // CBC模式需要生成一个16 bytes的initialization vector:
    SecureRandom sr = SecureRandom.getInstanceStrong();
    byte[] iv = sr.generateSeed(16);
    IvParameterSpec ivps = new IvParameterSpec(iv);
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivps);
    byte[] data = cipher.doFinal(input);
    // IV不需要保密，把IV和密文一起返回:
    return join(iv, data);
  }

  // 解密:
  public static byte[] decrypt(byte[] key, byte[] input) throws GeneralSecurityException {
    // 把input分割成IV和密文:
    byte[] iv = new byte[16];
    byte[] data = new byte[input.length - 16];
    System.arraycopy(input, 0, iv, 0, 16);
    System.arraycopy(input, 16, data, 0, data.length);
    // 解密:
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
    IvParameterSpec ivps = new IvParameterSpec(iv);
    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivps);
    return cipher.doFinal(data);
  }

  public static byte[] join(byte[] bs1, byte[] bs2) {
    byte[] r = new byte[bs1.length + bs2.length];
    System.arraycopy(bs1, 0, r, 0, bs1.length);
    System.arraycopy(bs2, 0, r, bs1.length, bs2.length);
    return r;
  }

  private String generateRandomKey() {
    StringBuilder sb = new StringBuilder();
    Random random = new Random();
    while (sb.length() < 32) {
      int charValue = random.nextInt(128);
      if (charValue > 0) {
        sb.append((char) charValue);
      }
    }
    return sb.toString();
  }
}
