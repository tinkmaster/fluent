package tech.tinkmaster.fluent.common.entity.execution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class ExecutionGraph {
  @NonNull private List<Integer> sources;
  @NonNull private Map<Integer, ExecutionGraphNode> nodes;
  @Default private Integer currentNode = null;
  @Default private ExecutionStatus status = ExecutionStatus.CREATED;
  @Default private Map<Integer, String> results = new HashMap<>();
}
