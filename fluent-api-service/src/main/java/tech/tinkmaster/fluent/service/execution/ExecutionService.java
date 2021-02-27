package tech.tinkmaster.fluent.service.execution;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.tinkmaster.fluent.common.entity.execution.*;
import tech.tinkmaster.fluent.persistence.file.ExecutionStorage;

@Service
public class ExecutionService {

  @Autowired ExecutionStorage storage;

  public List<Execution> list(String pipelineName) throws IOException {
    List<String> records = this.storage.list(pipelineName);
    if (records.size() == 0) {
      return Collections.emptyList();
    }
    return records
        .stream()
        .map(
            name -> {
              String[] arr = name.split("-");
              SimpleDateFormat dateStr = new SimpleDateFormat("yyMMddHHmmss");
              try {
                Execution execution = this.storage.get(pipelineName, name);
                Execution res =
                    ExecutionUtils.generateSimpleExecution(
                        name, arr[0], dateStr.parse(arr[1]), execution.getStatus());
                return res;
              } catch (ParseException | IOException e) {
                throw new RuntimeException("Failed to list execution graph in service.", e);
              }
            })
        .sorted((dia1, dia2) -> dia2.getCreatedTime().compareTo(dia1.getCreatedTime()))
        .collect(Collectors.toList());
  }

  public ExecutionOverview getOverview(String pipelineName) {
    ExecutionOverview result = new ExecutionOverview();
    List<String> records = this.storage.list(pipelineName);
    if (records.size() == 0) {
      return result;
    }
    records
        .stream()
        .forEach(
            name -> {
              String[] arr = name.split("-");
              SimpleDateFormat dateStr = new SimpleDateFormat("yyMMddHHmmss");
              try {
                Execution graph = this.storage.get(pipelineName, name);
                result.setTotalExecutionTimes(result.getTotalExecutionTimes() + 1);
                this.countStatus(graph, result);

              } catch (IOException e) {
                throw new RuntimeException("Failed to list execution graph in service.", e);
              }
            });
    if (result.getSuccessfulExecutionNumber() > 0) {
      result.setAverageUsedTimeMs(
          result.getTotalSuccessfulExecutionTimes() / result.getSuccessfulExecutionNumber());
    }
    return result;
  }

  public Execution get(String pipelineName, String name) throws IOException {
    return this.storage.get(pipelineName, name);
  }

  public void updateOrCreate(Execution graph) throws IOException {
    this.storage.updateOrCreate(graph);
  }

  public void delete(String pipelineName, String name) throws IOException {
    this.storage.delete(pipelineName, name);
  }

  private void countStatus(Execution execution, ExecutionOverview overview) {
    List<ExecutionGraph> graphs = ExecutionUtils.getGraphs(execution);
    if (execution.getStatus().equals(ExecutionStatus.FINISHED)) {
      overview.setSuccessfulExecutionNumber(overview.getSuccessfulExecutionNumber() + 1);
      graphs.forEach(
          graph ->
              graph
                  .getNodes()
                  .forEach(
                      (k, v) -> {
                        if (overview.getLastExecution() == null
                            || execution.getCreatedTime().after(overview.getLastExecution())) {
                          overview.setLastSuccessfulExecution(execution.getCreatedTime());
                        }
                        overview.addSuccessfulExecutionTime(v.getUsedTime());
                        overview.addTotalExecutionTime(v.getUsedTime());
                      }));
    } else if (execution.getStatus().equals(ExecutionStatus.FAILED)) {
      overview.setFailedExecutionNumber(overview.getFailedExecutionNumber() + 1);
      graphs.forEach(
          graph ->
              graph
                  .getNodes()
                  .forEach(
                      (k, v) -> {
                        if (overview.getLastFailedExecution() == null
                            || execution
                                .getCreatedTime()
                                .after(overview.getLastFailedExecution())) {
                          overview.setLastFailedExecution(execution.getCreatedTime());
                        }
                        if (v.getUsedTime() != null) {
                          overview.addTotalExecutionTime(v.getUsedTime());
                        }
                      }));
    } else if (execution.getStatus().equals(ExecutionStatus.RUNNING)) {
      overview.setRunningExecutionNumber(overview.getRunningExecutionNumber() + 1);
      graphs.forEach(
          graph ->
              graph
                  .getNodes()
                  .forEach(
                      (k, v) -> {
                        if (v.getUsedTime() != null) {
                          overview.addTotalExecutionTime(v.getUsedTime());
                        }
                      }));
    } else {
      overview.setCreatedExecutionNumber(overview.getCreatedExecutionNumber() + 1);
    }

    if (overview.getLastExecution() == null
        || execution.getCreatedTime().after(overview.getLastExecution())) {
      overview.setLastExecution(execution.getCreatedTime());
    }
    overview.setTotalExecutionNumber(overview.getTotalExecutionNumber() + 1);
  }
}
