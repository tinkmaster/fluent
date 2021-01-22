package tech.tinkmaster.fluent.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import tech.tinkmaster.fluent.common.FluentObjectMappers;

public class JsonUtils {
  private static final ObjectMapper MAPPER = FluentObjectMappers.getNewConfiguredJsonMapper();

  public static JsonNode isJsonFormation(String content) {
    try {
      return MAPPER.readTree(content);
    } catch (IOException e) {
      // ignored
    } catch (Throwable t) {
      return null;
    }
    return null;
  }
}
