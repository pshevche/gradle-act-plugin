package io.github.pshevche.act.internal.spec;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public record ActTestSpec(
        String name,
        Path workflow,
        @Nullable String job,
        @Nullable ActTestSpecEvent event,
        @Nullable ActTestSpecInput env,
        @Nullable ActTestSpecInput inputs,
        @Nullable ActTestSpecInput secrets,
        @Nullable ActTestSpecInput variables,
        Map<String, Object> matrix,
        @Nullable ActTestSpecResources resources,
        List<String> additionalArgs
) {
}
