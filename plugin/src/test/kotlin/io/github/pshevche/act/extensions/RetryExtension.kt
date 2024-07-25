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

package io.github.pshevche.act.extensions

import io.kotest.assertions.retry
import io.kotest.assertions.retryConfig
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.time.Duration.Companion.minutes

object RetryExtension : TestCaseExtension {

    val config = retryConfig {
        maxRetry = 2
        timeout = 10.minutes
        exceptionClass = Exception::class
    }

    override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
        return execute(
            testCase.copy(test = {
                retry(config) {
                    testCase.test(this)
                }
            })
        )
    }
}
