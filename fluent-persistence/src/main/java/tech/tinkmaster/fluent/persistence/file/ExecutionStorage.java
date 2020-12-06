package tech.tinkmaster.fluent.persistence.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tech.tinkmaster.fluent.common.FluentObjectMappers;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagram;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/** ${base}/namespaces/default/executions/${name} */
@Component
public class ExecutionStorage {

  @Value("${data.dir}")
  String baseDir;

  ObjectMapper mappers = FluentObjectMappers.getNewConfiguredYamlMapper();

  @PostConstruct
  public void init() throws IOException {
    Files.createDirectories(Paths.get(this.baseDir, this.getExecutionDiagramFilePath()));
  }

  public List<String> list(String pipelineName) {
    File file = Paths.get(this.baseDir, this.getExecutionDiagramFilePath(), pipelineName).toFile();
    if (file.exists() && file.isDirectory()) {
      return FileUtils.listFiles(file, null, false).stream()
          .map(File::getName)
          .collect(Collectors.toList());
    } else {
      return Collections.emptyList();
    }
  }

  public ExecutionDiagram get(String pipelineName, String name) throws IOException {
    File file =
        Paths.get(this.baseDir, this.getExecutionDiagramFilePath(), pipelineName, name).toFile();
    if (file.exists()) {
      ExecutionDiagram diagram =
          this.mappers.readValue(
              FileUtils.readFileToString(file, Charset.defaultCharset()), ExecutionDiagram.class);
      return diagram;
    } else {
      return null;
    }
  }

  public void updateOrCreate(ExecutionDiagram diagram) throws IOException {
    File file =
        Paths.get(
                this.baseDir,
                this.getExecutionDiagramFilePath(),
                diagram.getPipelineName(),
                diagram.getName())
            .toFile();
    if (!file.exists()) {
      this.createFile(file);
    }
    String operatorAsYaml = this.mappers.writeValueAsString(diagram);
    FileUtils.writeStringToFile(file, operatorAsYaml, Charset.defaultCharset());
  }

  public boolean delete(String pipelineName, String name) throws IOException {
    File fileToDelete = Paths.get(this.baseDir, this.getExecutionDiagramFilePath(), name).toFile();
    return fileToDelete.delete();
  }

  private String getExecutionDiagramFilePath() {
    return "namespaces/default/executions";
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
