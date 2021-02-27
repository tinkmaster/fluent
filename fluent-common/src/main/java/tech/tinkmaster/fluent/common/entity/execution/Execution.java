package tech.tinkmaster.fluent.common.entity.execution;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.Date;
import java.util.Map;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class Execution {
  String name;
  String pipelineName;
  ExecutionStages stages;
  ExecutionStatus status;
  Date createdTime;
  String environment;
  Map<String, String> parameters;
}
