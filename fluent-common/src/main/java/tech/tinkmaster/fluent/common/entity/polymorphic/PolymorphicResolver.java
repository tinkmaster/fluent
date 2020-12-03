package tech.tinkmaster.fluent.common.entity.polymorphic;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import java.util.HashMap;
import java.util.Map;
import tech.tinkmaster.fluent.common.entity.polymorphic.AbstractPolymorphic.UnknownKind;
import tech.tinkmaster.fluent.common.util.MoreMap;

public abstract class PolymorphicResolver extends TypeIdResolverBase {
  private JavaType superType;
  private Map<String, Class> subtypes = new HashMap<>();
  private Map<Class, String> reversedSubtypes = new HashMap<>();

  @Override
  public void init(JavaType baseType) {
    superType = baseType;
  }

  @Override
  public String idFromValue(Object o) {
    Class clz = o.getClass();
    return reversedSubtypes.getOrDefault(clz, UnknownKind.KIND);
  }

  @Override
  public String idFromValueAndType(Object o, Class<?> aClass) {
    return reversedSubtypes.get(aClass);
  }

  @Override
  public JavaType typeFromId(DatabindContext context, String id) {
    Class<?> subType = subtypes.getOrDefault(id, UnknownKind.class);
    return context.constructSpecializedType(superType, subType);
  }

  @Override
  public JsonTypeInfo.Id getMechanism() {
    return JsonTypeInfo.Id.NAME;
  }

  protected void bind(String kind, Class clazz) {
    subtypes.put(kind, clazz);
    reversedSubtypes = MoreMap.inverse(subtypes);
  }

  protected abstract void bindSubtypes();
}
