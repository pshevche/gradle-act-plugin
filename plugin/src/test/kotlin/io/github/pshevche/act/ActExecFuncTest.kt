package io.github.pshevche.act

import io.github.pshevche.act.fixtures.BuildRunner
import io.github.pshevche.act.fixtures.Workspace
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

class ActExecFuncTest : FreeSpec({

    val workspace = extension(Workspace(tempdir()))
    val runner = BuildRunner(workspace)

    fun succeeded(result: BuildResult, taskPath: String, expectedJobs: List<String>) {
        result.output shouldContain "BUILD SUCCESSFUL"
        result.task(taskPath)?.outcome shouldBe TaskOutcome.SUCCESS
        expectedJobs shouldContainExactlyInAnyOrder result.output.lines()
            .filter { it.contains("\uD83C\uDFC1  Job succeeded") }
            .map { it.substring(it.indexOf("/") + 1, it.indexOf("]")).trim() }
    }

    "runs workflows in the default location" {
        workspace.addWorkflows(".github/workflows/", "hello_world", "goodbye_world")
        workspace.execTask("actHelloWorld")

        val result = runner.build("actHelloWorld")
        succeeded(result, ":actHelloWorld", listOf("print_greeting", "print_farewell"))
    }

    "supports overriding the location of workflow files" {
        workspace.addWorkflows("custom-workflows/", "hello_world")
        workspace.execTask("actHelloWorld") {
            workflows(workspace.dir.resolve("custom-workflows"))
        }

        val result = runner.build("actHelloWorld")
        succeeded(result, ":actHelloWorld", listOf("print_greeting"))
    }

    "supports running a single workflow" {
        workspace.addWorkflows("custom-workflows/", "hello_world", "goodbye_world")
        workspace.execTask("actHelloWorld") {
            workflows(workspace.dir.resolve("custom-workflows/hello_world.yml"))
        }

        val result = runner.build("actHelloWorld")
        succeeded(result, ":actHelloWorld", listOf("print_greeting"))
    }
})
