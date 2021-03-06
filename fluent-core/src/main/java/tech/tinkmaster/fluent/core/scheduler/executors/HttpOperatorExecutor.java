package tech.tinkmaster.fluent.core.scheduler.executors;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tinkmaster.fluent.common.entity.execution.Execution;
import tech.tinkmaster.fluent.common.entity.operator.Operator;
import tech.tinkmaster.fluent.common.util.http.http.FluentHttpMethod;
import tech.tinkmaster.fluent.common.util.http.http.FluentHttpPortal;
import tech.tinkmaster.fluent.common.util.http.http.FluentHttpRequest;
import tech.tinkmaster.fluent.common.util.http.http.FluentHttpResponse;

public class HttpOperatorExecutor implements OperatorExecutor {
  private static final Logger LOG = LoggerFactory.getLogger(HttpOperatorExecutor.class);
  private Operator operator;
  private Execution graph;

  private FluentHttpRequest request;

  public HttpOperatorExecutor(Execution graph, Operator operator) {
    this.operator = operator;
    this.graph = graph;

    Map<String, String> parameters = operator.getParams();
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    if (parameters.get("headers") != null) {
      Arrays.asList(parameters.get("headers").split("\n"))
          .forEach(
              s -> {
                if (s != null && s.trim().length() != 0) {
                  String[] arr = s.split(":");
                  if (arr.length == 2) {
                    headers.put(arr[0].trim(), arr[1].trim());
                  }
                }
              });
    }

    byte[] body = parameters.get("body") == null ? null : parameters.get("body").getBytes();
    try {
      this.request =
          FluentHttpRequest.builder()
              .method(FluentHttpMethod.valueOf(parameters.get("method")))
              .uri(new URI(this.generateUrl(parameters.get("uri"), parameters.get("path"))))
              .requestBody(body)
              .httpHeader(headers)
              .build();
    } catch (URISyntaxException e) {
      throw new RuntimeException("Unable to deserialize http uri in http operator executor.");
    }
  }

  @Override
  public Object execute() throws IOException {
    try {
      FluentHttpResponse response = FluentHttpPortal.sendRequest(this.request);
      return response;
    } catch (IOException e) {
      // collect error information
      LOG.error("error", e);
      throw e;
    }
  }

  @Override
  public Class returnType() {
    return FluentHttpResponse.class;
  }

  @Override
  public Operator getOperator() {
    return this.operator;
  }

  public String generateUrl(String s1, String s2) {
    if (s1.endsWith("/")) {
      s1 = s1.substring(0, s1.length() - 1);
    }
    if (!s2.startsWith("/")) {
      s2 = "/" + s2;
    }
    return s1 + s2;
  }
}
