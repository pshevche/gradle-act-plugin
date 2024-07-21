package io.github.pshevche.act.internal.reporting;

import io.github.pshevche.act.internal.TestDescriptor;

abstract class AbstractActTestReporter implements ActTestReporter {

    @Override
    public void reportTestExecutionStarted() {

    }

    @Override
    public void reportTestExecutionFinished() {

    }

    @Override
    public void reportSpecStarted(TestDescriptor.SpecDescriptor spec) {

    }

    @Override
    public void reportSpecFinishedSuccessfully(TestDescriptor.SpecDescriptor spec) {

    }

    @Override
    public void reportSpecFinishedWithFailure(TestDescriptor.SpecDescriptor spec) {

    }

    @Override
    public void reportJobStarted(TestDescriptor.JobDescriptor job) {

    }

    @Override
    public void reportJobFinishedSuccessfully(TestDescriptor.JobDescriptor job, String output) {

    }

    @Override
    public void reportJobFinishedWithFailure(TestDescriptor.JobDescriptor job, String output) {

    }
}
