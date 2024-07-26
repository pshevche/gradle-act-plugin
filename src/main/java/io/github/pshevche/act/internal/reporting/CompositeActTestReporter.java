/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.pshevche.act.internal.reporting;

import io.github.pshevche.act.internal.TestDescriptor.JobDescriptor;
import io.github.pshevche.act.internal.TestDescriptor.SpecDescriptor;

import java.util.List;

public class CompositeActTestReporter extends AbstractActTestReporter {

    private final List<ActTestReporter> delegates;

    public CompositeActTestReporter(List<ActTestReporter> delegates) {
        this.delegates = delegates;
    }

    @Override
    public void reportTestExecutionStarted() {
        delegates.forEach(ActTestReporter::reportTestExecutionStarted);
    }

    @Override
    public void reportTestExecutionFinished() {
        delegates.forEach(ActTestReporter::reportTestExecutionFinished);
    }

    @Override
    public void reportSpecStarted(SpecDescriptor spec) {
        delegates.forEach(d -> d.reportSpecStarted(spec));
    }

    @Override
    public void reportSpecFinishedSuccessfully(SpecDescriptor spec) {
        delegates.forEach(d -> d.reportSpecFinishedSuccessfully(spec));
    }

    @Override
    public void reportSpecFinishedWithFailure(SpecDescriptor spec) {
        delegates.forEach(d -> d.reportSpecFinishedWithFailure(spec));
    }

    @Override
    public void reportJobStarted(JobDescriptor job) {
        delegates.forEach(d -> d.reportJobStarted(job));
    }

    @Override
    public void reportSuccessfulJobStep(JobDescriptor job) {
        delegates.forEach(d -> d.reportSuccessfulJobStep(job));
    }

    @Override
    public void reportJobFinishedOrSkipped(JobDescriptor job, String output) {
        delegates.forEach(d -> d.reportJobFinishedOrSkipped(job, output));
    }

    @Override
    public void reportJobFinishedWithFailure(JobDescriptor job, String output) {
        delegates.forEach(d -> d.reportJobFinishedWithFailure(job, output));
    }

    @Override
    public void close() throws Exception {
        for (var delegate : delegates) {
            delegate.close();
        }

    }
}
