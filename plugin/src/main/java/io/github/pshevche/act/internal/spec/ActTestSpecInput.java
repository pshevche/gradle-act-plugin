package io.github.pshevche.act.internal.spec;

import javax.annotation.Nullable;
import java.util.Map;

public record ActTestSpecInput(@Nullable String file, @Nullable Map<String, Object> values) {
}
