package io.github.pshevche.act.internal.spec;

import javax.annotation.Nullable;
import java.nio.file.Path;

public record ActTestSpecEvent(@Nullable String type, @Nullable Path payload) {
}
