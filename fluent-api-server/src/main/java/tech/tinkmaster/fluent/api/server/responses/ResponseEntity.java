package tech.tinkmaster.fluent.api.server.responses;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
@JsonPropertyOrder({"code", "message", "data"})
public class ResponseEntity {
  int code;
  String message;
  Object data;

  public static ResponseEntity ok(Object data) {
    return ResponseEntity.builder().code(200).message("OK").data(data).build();
  }

  public static ResponseEntity notFound(Object data) {
    return ResponseEntity.builder().code(404).message("Not found").data(data).build();
  }
}
