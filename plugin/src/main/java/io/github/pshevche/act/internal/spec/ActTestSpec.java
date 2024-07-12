package io.github.pshevche.act.internal.spec;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public record ActTestSpec(
        String workflow,
        @Nullable String job,
        @Nullable ActTestSpecInput event,
        @Nullable ActTestSpecInput env,
        @Nullable ActTestSpecInput inputs,
        @Nullable ActTestSpecInput secrets,
        @Nullable ActTestSpecInput variables,
        @Nullable Map<String, Object> matrix,
        @Nullable List<String> additionalArgs
) {
}
