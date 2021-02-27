package tech.tinkmaster.fluent.common.entity.execution;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class ExecutionGraph {
  @NonNull private List<Integer> sources;
  @NonNull private Map<Integer, ExecutionGraphNode> nodes;
  @Default private Integer currentNode = null;
  @Default private ExecutionStatus status = ExecutionStatus.CREATED;
  @Default private Map<String, String> results = new HashMap<>();
}
