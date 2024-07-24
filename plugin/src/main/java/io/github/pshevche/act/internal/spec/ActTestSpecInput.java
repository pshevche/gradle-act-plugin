package io.github.pshevche.act.internal.spec;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Map;

public record ActTestSpecInput(@Nullable Path file, Map<String, Object> values) {
}