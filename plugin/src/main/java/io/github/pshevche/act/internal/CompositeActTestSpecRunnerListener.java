package io.github.pshevche.act.internal;

import java.util.List;

class CompositeActTestSpecRunnerListener implements ActTestSpecRunnerListener {
    private final List<ActTestSpecRunnerListener> delegates;

    CompositeActTestSpecRunnerListener(List<ActTestSpecRunnerListener> delegates) {
        this.delegates = delegates;
    }

    @Override
    public void onOutput(TestDescriptor.SpecDescriptor spec, String line) {
        delegates.forEach(d -> d.onOutput(spec, line));
    }

    @Override
    public void onError(TestDescriptor.SpecDescriptor spec, String line) {
        delegates.forEach(d -> d.onError(spec, line));
    }
}
