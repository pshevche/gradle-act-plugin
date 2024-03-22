package io.github.pshevche.act.fixtures

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

    fun BuildResult.shouldRunJobs(vararg jobs: String) {
        jobs.toList() shouldContainExactlyInAnyOrder this.output.lines()
            .filter { it.contains("\uD83C\uDFC1  Job succeeded") }
            .map { it.substring(it.indexOf("/") + 1, it.indexOf("]")).trim() }
    }
}
