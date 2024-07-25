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
        return execute(testCase.copy(test = {
            retry(config) {
                testCase.test(this)
            }
        }))
    }
}