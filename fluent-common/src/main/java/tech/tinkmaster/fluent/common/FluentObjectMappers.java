package tech.tinkmaster.fluent.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class FluentObjectMappers {

  /**
   * Return a new instance of ObjectYamlMapper configured with fluent settings. No matter how you
   * set the Feature.MINIMIZE_QUOTES or JsonInclude.Include, if the field has value null, it will
   * ends up having null value in java object instance.
   *
   * @return a configured ObjectMapper
   */
  public static ObjectMapper getNewConfiguredYamlMapper() {
    YAMLFactory yamlFactory = new YAMLFactory();
    yamlFactory.disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID);
    yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
    yamlFactory.disable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
    ObjectMapper mapper = new ObjectMapper(yamlFactory);
    configureMapper(mapper);
    return mapper;
  }

  public static ObjectMapper getNewConfiguredJsonMapper() {
    ObjectMapper mapper = new ObjectMapper();
    configureMapper(mapper);
    return mapper;
  }

  /**
   * Configure the existing mapper with fluent persistent settings.
   *
   * @param mapper the mapper to configure
   */
  private static void configureMapper(ObjectMapper mapper) {
    mapper.registerModule(new JavaTimeModule()); // To support Instant
    mapper.registerModule(new Jdk8Module()); // To support Optional

    mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, false);
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
    mapper.configure(MapperFeature.AUTO_DETECT_CREATORS, true);
    mapper.configure(MapperFeature.AUTO_DETECT_GETTERS, true);
    mapper.configure(MapperFeature.AUTO_DETECT_SETTERS, true);
    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.setAnnotationIntrospector(new ApiAnnotationInspector());
  }

  static class ApiAnnotationInspector extends JacksonAnnotationIntrospector {

    @Override
    public boolean hasIgnoreMarker(AnnotatedMember m) {
      return super.hasIgnoreMarker(m);
    }
  }
}
