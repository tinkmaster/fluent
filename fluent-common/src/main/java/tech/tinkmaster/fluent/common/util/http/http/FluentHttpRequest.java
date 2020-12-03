package tech.tinkmaster.fluent.common.util.http.http;

import java.net.URI;
import java.util.Map;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class FluentHttpRequest {
  URI uri;
  FluentHttpMethod method;
  Map<String, String> httpHeader;
  byte[] requestBody;
}
