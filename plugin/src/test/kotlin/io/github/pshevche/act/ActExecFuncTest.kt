package io.github.pshevche.act

import io.github.pshevche.act.fixtures.BuildRunner
import io.github.pshevche.act.fixtures.Workspace
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
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
        expectedJobs.forEach { job ->
            result.output shouldContain "/$job] \uD83C\uDFC1  Job succeeded"
        }
    }

    "can execute a simple workflow in the default location" {
        workspace.addWorkflow(".github/workflows/", "hello_world")
        workspace.execTask("actHelloWorld")

        val result = runner.build("actHelloWorld", "--stacktrace")
        succeeded(result, ":actHelloWorld", listOf("print_greeting"))
    }
})
