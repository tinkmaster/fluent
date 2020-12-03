package tech.tinkmaster.fluent.common.util.http.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import okhttp3.*;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class FluentHttpPortal {

  private static FluentHttpPortal portal = FluentHttpPortal.builder().build();

  OkHttpClient client = new OkHttpClient.Builder().build();;

  public static FluentHttpResponse sendRequest(FluentHttpRequest request) throws IOException {
    Request.Builder builder = new Request.Builder();
    RequestBody requestBody =
        request.getRequestBody() == null || request.getMethod() == FluentHttpMethod.GET
            ? null
            : RequestBody.create(request.getRequestBody());
    builder.method(request.getMethod().name(), requestBody);
    request.getHttpHeader().forEach(builder::header);
    builder.url(request.getUri().toURL());

    Call call = portal.client.newCall(builder.build());

    Response response = call.execute();

    Map<String, String> headers = new HashMap<>();
    response
        .headers()
        .iterator()
        .forEachRemaining(v -> headers.put(v.component1(), v.component2()));

    return FluentHttpResponse.builder()
        .status(FluentHttpResponseStatus.valueOf(response.code()))
        .body(response.body() == null ? null : new String(response.body().bytes()))
        .headers(headers)
        .build();
  }
}
