package tech.tinkmaster.fluent.common.util.http.http;

import java.util.Map;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class FluentHttpResponse {

  FluentHttpResponseStatus status;
  Map<String, String> headers;
  String body;
}
