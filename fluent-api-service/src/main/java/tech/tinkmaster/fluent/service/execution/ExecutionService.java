package tech.tinkmaster.fluent.service.execution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagram;
import tech.tinkmaster.fluent.persistence.file.ExecutionStorage;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExecutionService {

  @Autowired ExecutionStorage storage;

  public List<ExecutionDiagram> list(String pipelineName) throws IOException {
    List<String> records = this.storage.list(pipelineName);
    if (records.size() == 0) {
      return Collections.emptyList();
    }
    return records.stream()
        .map(
            name -> {
              String[] arr = name.split("-");
              SimpleDateFormat dateStr = new SimpleDateFormat("yyMMddHHmmss");
              try {
                ExecutionDiagram diagram = this.storage.get(pipelineName, name);
                ExecutionDiagram res = new ExecutionDiagram(name, arr[1], dateStr.parse(arr[1]));
                res.status = diagram.status;
                return res;
              } catch (ParseException | IOException e) {
                throw new RuntimeException("Failed to list execution diagram in service.", e);
              }
            })
        .sorted((dia1, dia2) -> dia2.createdTime.compareTo(dia1.createdTime))
        .collect(Collectors.toList());
  }

  public ExecutionDiagram get(String pipelineName, String name) throws IOException {
    return this.storage.get(pipelineName, name);
  }

  public void updateOrCreate(ExecutionDiagram diagram) throws IOException {
    this.storage.updateOrCreate(diagram);
  }

  public void delete(String pipelineName, String name) throws IOException {
    this.storage.delete(pipelineName, name);
  }
}
