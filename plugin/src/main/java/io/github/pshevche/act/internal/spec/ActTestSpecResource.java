package io.github.pshevche.act.internal.spec;

import javax.annotation.Nullable;

public record ActTestSpecResource(
        boolean enabled,
        @Nullable String storage,
        @Nullable String host,
        @Nullable Integer port
) {
}
