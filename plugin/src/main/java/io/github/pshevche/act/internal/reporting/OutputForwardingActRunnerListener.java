package io.github.pshevche.act.internal.reporting;

import io.github.pshevche.act.internal.ActTestSpecRunnerListener;
import io.github.pshevche.act.internal.TestDescriptor;
import org.gradle.api.logging.Logger;

public class OutputForwardingActRunnerListener implements ActTestSpecRunnerListener {

    private final Logger logger;

    public OutputForwardingActRunnerListener(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void onOutput(TestDescriptor.SpecDescriptor spec, String line) {
        logger.info(line);
    }

    @Override
    public void onError(TestDescriptor.SpecDescriptor spec, String line) {
        logger.error(line);
    }
}
