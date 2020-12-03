package tech.tinkmaster.fluent.common.util.http.http;

public class FluentHttpResponseStatus {
  public static final FluentHttpResponseStatus OK = new FluentHttpResponseStatus(200, "OK");
  public static final FluentHttpResponseStatus CREATED =
      new FluentHttpResponseStatus(201, "Created");
  public static final FluentHttpResponseStatus ACCEPTED =
      new FluentHttpResponseStatus(202, "Accepted");
  public static final FluentHttpResponseStatus MOVED_PERMANENTLY =
      new FluentHttpResponseStatus(301, "Moved Permanently");
  public static final FluentHttpResponseStatus FOUND = new FluentHttpResponseStatus(302, "Found");
  public static final FluentHttpResponseStatus BAD_REQUEST =
      new FluentHttpResponseStatus(400, "Bad Request");
  public static final FluentHttpResponseStatus UNAUTHORIZED =
      new FluentHttpResponseStatus(401, "Unauthorized");
  public static final FluentHttpResponseStatus FORBIDDEN =
      new FluentHttpResponseStatus(403, "Forbidden");
  public static final FluentHttpResponseStatus NOT_FOUND =
      new FluentHttpResponseStatus(404, "Not Found");
  public static final FluentHttpResponseStatus INTERNAL_SERVER_ERROR =
      new FluentHttpResponseStatus(500, "Internal Server Error");
  public static final FluentHttpResponseStatus BAD_GATEWAY =
      new FluentHttpResponseStatus(502, "Bad Gateway");
  public static final FluentHttpResponseStatus SERVICE_UNAVAILABLE =
      new FluentHttpResponseStatus(503, "Service Unavailable");
  public static final FluentHttpResponseStatus GATEWAY_TIMEOUT =
      new FluentHttpResponseStatus(504, "Gateway Timeout");

  public int code;
  public String reason;

  FluentHttpResponseStatus(int code, String reason) {
    this.code = code;
    this.reason = reason;
  }

  public static FluentHttpResponseStatus valueOf(int code) {
    switch (code) {
      case 200:
        return OK;
      case 201:
        return CREATED;
      case 202:
        return ACCEPTED;
      case 301:
        return MOVED_PERMANENTLY;
      case 302:
        return FOUND;
      case 400:
        return BAD_REQUEST;
      case 401:
        return UNAUTHORIZED;
      case 403:
        return FORBIDDEN;
      case 404:
        return NOT_FOUND;
      case 500:
        return INTERNAL_SERVER_ERROR;
      case 502:
        return BAD_GATEWAY;
      case 503:
        return SERVICE_UNAVAILABLE;
      case 504:
        return GATEWAY_TIMEOUT;
      default:
        return new FluentHttpResponseStatus(code, "Not Identified.");
    }
  }
}
