package tech.tinkmaster.fluent.common.util;

import java.util.Map;
import java.util.stream.Collectors;

public class MoreMap {

  /**
   * Inverse the map
   *
   * @param map map to inverse
   * @param <K> Key type
   * @param <V> Value type
   * @return reversed immutable map
   */
  public static <K, V> Map<V, K> inverse(Map<K, V> map) {
    return map.entrySet()
        .stream()
        .collect(Collectors.toMap(kvEntry -> kvEntry.getValue(), kvEntry -> kvEntry.getKey()));
  }
}
