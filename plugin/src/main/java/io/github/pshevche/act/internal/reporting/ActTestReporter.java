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
