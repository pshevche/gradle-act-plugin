package io.github.pshevche.act.internal.reporting;

import io.github.pshevche.act.internal.TestDescriptor;

public interface ActTestReporter extends AutoCloseable {

    void reportTestExecutionStarted();

    void reportTestExecutionFinished();

    void reportSpecStarted(TestDescriptor.SpecDescriptor spec);

    void reportSpecFinishedSuccessfully(TestDescriptor.SpecDescriptor spec);

    void reportSpecFinishedWithFailure(TestDescriptor.SpecDescriptor spec);

    void reportJobStarted(TestDescriptor.JobDescriptor job);

    void reportSuccessfulJobStep(TestDescriptor.JobDescriptor job);

    void reportJobFinishedOrSkipped(TestDescriptor.JobDescriptor job, String output);

    void reportJobFinishedWithFailure(TestDescriptor.JobDescriptor job, String output);
}
