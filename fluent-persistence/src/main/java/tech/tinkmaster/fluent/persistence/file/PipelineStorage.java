package tech.tinkmaster.fluent.persistence.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tech.tinkmaster.fluent.common.FluentObjectMappers;
import tech.tinkmaster.fluent.common.entity.pipeline.Pipeline;

@Component
public class PipelineStorage {
  @Value("${data.dir}")
  String baseDir;

  ObjectMapper mappers = FluentObjectMappers.getNewConfiguredYamlMapper();

  @PostConstruct
  public void init() throws IOException {
    Files.createDirectories(Paths.get(this.baseDir, this.getPipelineFilePath()));
  }

  public List<String> list() {
    return FileUtils.listFiles(
            Paths.get(this.baseDir, this.getPipelineFilePath()).toFile(), null, false)
        .stream()
        .map(File::getName)
        .collect(Collectors.toList());
  }

  public Pipeline get(String name) throws IOException {
    File file = Paths.get(this.baseDir, this.getPipelineFilePath(), name).toFile();
    if (file.exists()) {
      return this.mappers.readValue(
          FileUtils.readFileToString(file, Charset.defaultCharset()), Pipeline.class);
    } else {
      return null;
    }
  }

  public void updateOrCreate(Pipeline pipeline) throws IOException {
    File file = Paths.get(this.baseDir, this.getPipelineFilePath(), pipeline.getName()).toFile();
    if (!file.exists()) {
      this.createFile(file);
    }
    String PipelineAsYaml = this.mappers.writeValueAsString(pipeline);
    FileUtils.writeStringToFile(
        Paths.get(this.baseDir, this.getPipelineFilePath(), pipeline.getName()).toFile(),
        PipelineAsYaml,
        Charset.defaultCharset());
  }

  public boolean delete(String name) throws IOException {
    File fileToDelete = Paths.get(this.baseDir, this.getPipelineFilePath(), name).toFile();
    return fileToDelete.delete();
  }

  private String getPipelineFilePath() {
    return "namespaces/default/pipelines";
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
