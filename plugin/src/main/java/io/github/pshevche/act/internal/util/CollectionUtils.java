package io.github.pshevche.act.internal.util;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CollectionUtils {

    private CollectionUtils() {
    }

    public static <K, V> Map<K, V> nullToEmpty(@Nullable Map<K, V> map) {
        return map == null ? Collections.emptyMap() : map;
    }

    public static <V> List<V> nullToEmpty(@Nullable List<V> list) {
        return list == null ? Collections.emptyList() : list;
    }
}
