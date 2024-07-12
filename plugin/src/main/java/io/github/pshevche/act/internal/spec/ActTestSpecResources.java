package io.github.pshevche.act.internal.spec;

import javax.annotation.Nullable;

public record ActTestSpecResources(
        @Nullable ActTestSpecResource artifactServer,
        @Nullable ActTestSpecResource cacheServer
) {
}
