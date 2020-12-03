package tech.tinkmaster.fluent.common.entity.datastructure;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;
import tech.tinkmaster.fluent.common.ApiVersions;
import tech.tinkmaster.fluent.common.entity.ObjectMetadata;
import tech.tinkmaster.fluent.common.entity.ResourceKind;

@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
@JsonPropertyOrder({"apiVersion", "kind", "metadata", "spec"})
public class DataStructureDefinition {
  String apiVersion = ApiVersions.V1;
  String kind = ResourceKind.DATA_STRUCTURE_DEFINITION;

  ObjectMetadata metadata;
  DataStructureDefinitionSpec spec;

  @Value
  @With
  @lombok.Builder(toBuilder = true, builderClassName = "Builder")
  @JsonPropertyOrder({"fields"})
  public static class DataStructureDefinitionSpec {
    /**
     * If the field type is data structure reference, only ds in the same namespace is accessible.
     */
    @NonNull List<Field> fields;
  }

  //  @Id
  public String getId() {
    return String.format(
        "/%s/%s/%s/%s/%s",
        this.apiVersion,
        this.kind,
        "namespaces",
        this.metadata.getNamespace(),
        this.metadata.getName());
  }
}
