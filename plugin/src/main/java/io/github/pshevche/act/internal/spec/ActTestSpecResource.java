package io.github.pshevche.act.internal.spec;

import javax.annotation.Nullable;
import java.nio.file.Path;

public record ActTestSpecResource(
        boolean enabled,
        @Nullable Path storage,
        @Nullable String host,
        @Nullable Integer port
) {
}
