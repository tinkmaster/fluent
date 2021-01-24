package tech.tinkmaster.fluent.common.entity.variable;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Environment {
  String name;
  Map<String, String> variables;
}
