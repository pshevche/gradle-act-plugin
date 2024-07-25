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
        logger.lifecycle(line);
    }

    @Override
    public void onError(TestDescriptor.SpecDescriptor spec, String line) {
        logger.error(line);
    }
}
