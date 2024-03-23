package io.github.pshevche.act.fixtures

import io.kotest.assertions.failure
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

object BuildResultAssertions {

    fun BuildResult.shouldSucceed(vararg tasks: String) {
        this.output shouldContain "BUILD SUCCESSFUL"
        tasks.toList().forEach {
            this.task(it)?.outcome shouldBe TaskOutcome.SUCCESS
        }
    }

    fun BuildResult.shouldFail(vararg tasks: String) {
        this.output shouldContain "BUILD FAILED"
        tasks.toList().forEach {
            this.task(it)?.outcome shouldBe TaskOutcome.FAILED
        }
    }

    fun BuildResult.shouldHaveSuccessfulJobs(vararg jobs: String) {
        jobs.toList() shouldContainExactlyInAnyOrder this.output.lines()
            .filter { it.contains("\uD83C\uDFC1  Job succeeded") }
            .map { it.substring(it.indexOf("/") + 1, it.indexOf("]")).trim() }
    }

    fun BuildResult.shouldHaveFailedJobs(vararg jobs: String) {
        jobs.toList() shouldContainExactlyInAnyOrder this.output.lines()
            .filter { it.contains("\uD83C\uDFC1  Job failed") }
            .map { it.substring(it.indexOf("/") + 1, it.indexOf("]")).trim() }
    }

    fun BuildResult.outputShouldContainEither(a: String, b: String) {
        if (!this.output.contains(a) && !this.output.contains(b)) {
            throw failure("Output contains neither '$a' nor '$b'")
        }
    }
}
