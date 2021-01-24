package tech.tinkmaster.fluent.persistence.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tech.tinkmaster.fluent.common.FluentObjectMappers;
import tech.tinkmaster.fluent.common.entity.variable.Global;

/** ${base}/namespaces/default/variables/globals/${name} */
@Component
public class GlobalVariableStorage {
  private static final String GLOBAL_FILE_NAME = "globals";

  @Value("${data.dir}")
  String baseDir;

  ObjectMapper mappers = FluentObjectMappers.getNewConfiguredYamlMapper();

  @PostConstruct
  public void init() throws IOException {
    Files.createDirectories(Paths.get(this.baseDir, this.getGlobalFilePath()));
  }

  public Global get() throws IOException {
    File file = Paths.get(this.baseDir, this.getGlobalFilePath(), GLOBAL_FILE_NAME).toFile();
    if (file.exists()) {
      return this.mappers.readValue(
          FileUtils.readFileToString(file, Charset.defaultCharset()), Global.class);
    } else {
      return null;
    }
  }

  public void update(Global global) throws IOException {
    File file = Paths.get(this.baseDir, this.getGlobalFilePath(), GLOBAL_FILE_NAME).toFile();
    if (!file.exists()) {
      this.createFile(file);
    }
    String operatorAsYaml = this.mappers.writeValueAsString(global);
    FileUtils.writeStringToFile(file, operatorAsYaml, Charset.defaultCharset());
  }

  private String getGlobalFilePath() {
    return "namespaces/default/variables/globals";
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
