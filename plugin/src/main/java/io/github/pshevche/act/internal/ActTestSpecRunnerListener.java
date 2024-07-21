package io.github.pshevche.act.internal;

import io.github.pshevche.act.internal.TestDescriptor.SpecDescriptor;

public interface ActTestSpecRunnerListener {

    void onOutput(SpecDescriptor spec, String line);

    void onError(SpecDescriptor spec, String line);
}
