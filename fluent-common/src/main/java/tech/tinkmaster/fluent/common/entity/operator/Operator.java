package tech.tinkmaster.fluent.common.entity.operator;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Value;
import lombok.With;

// stateless
@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class Operator {
  String name;
  OperatorType type;
  Map<String, String> params;

  public Operator deepClone() {
    return new Builder().name(name).type(type).params(new HashMap<>(params)).build();
  }
}
