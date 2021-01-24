package tech.tinkmaster.fluent.persistence.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tech.tinkmaster.fluent.common.FluentObjectMappers;
import tech.tinkmaster.fluent.common.entity.variable.Environment;

/** ${base}/namespaces/default/variables/envs/${name} */
@Component
public class EnvironmentStorage {

  @Value("${data.dir}")
  String baseDir;

  ObjectMapper mappers = FluentObjectMappers.getNewConfiguredYamlMapper();

  @PostConstruct
  public void init() throws IOException {
    Files.createDirectories(Paths.get(this.baseDir, this.getEnvironmentFilePath()));
  }

  public List<String> list() {
    File file = Paths.get(this.baseDir, this.getEnvironmentFilePath()).toFile();
    if (file.exists() && file.isDirectory()) {
      return FileUtils.listFiles(file, null, false)
          .stream()
          .map(File::getName)
          .collect(Collectors.toList());
    } else {
      return Collections.emptyList();
    }
  }

  public Environment get(String envName) throws IOException {
    File file = Paths.get(this.baseDir, this.getEnvironmentFilePath(), envName).toFile();
    if (file.exists()) {
      Environment env =
          this.mappers.readValue(
              FileUtils.readFileToString(file, Charset.defaultCharset()), Environment.class);
      return env;
    } else {
      return null;
    }
  }

  public void updateOrCreate(Environment env) throws IOException {
    File file = Paths.get(this.baseDir, this.getEnvironmentFilePath(), env.getName()).toFile();
    if (!file.exists()) {
      this.createFile(file);
    }
    String operatorAsYaml = this.mappers.writeValueAsString(env);
    FileUtils.writeStringToFile(file, operatorAsYaml, Charset.defaultCharset());
  }

  public boolean delete(String envName) throws IOException {
    File fileToDelete = Paths.get(this.baseDir, this.getEnvironmentFilePath(), envName).toFile();
    return fileToDelete.delete();
  }

  private String getEnvironmentFilePath() {
    return "namespaces/default/variables/environments";
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
}
