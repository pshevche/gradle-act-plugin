package io.github.pshevche.act

import io.github.pshevche.act.fixtures.BuildResultAssertions.outputShouldContainEither
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldFail
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldHaveFailedJobs
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldHaveSuccessfulJobs
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldSucceed
import io.github.pshevche.act.fixtures.BuildRunner
import io.github.pshevche.act.fixtures.Workspace
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import java.time.Duration

class ActExecFuncTest : FreeSpec({

    timeout = 30 * 1000

    val workspace = extension(Workspace(tempdir()))
    val runner = BuildRunner(workspace)

    "runs workflows in the default location" {
        workspace.addWorkflows(".github/workflows/", "hello_world", "goodbye_world")
        workspace.execTask("actAll")

        val result = runner.build("actAll")

        result.shouldSucceed(":actAll")
        result.shouldHaveSuccessfulJobs("print_greeting", "print_farewell")
    }

    "supports overriding the location of workflow files" {
        workspace.addWorkflows("custom-workflows/", "hello_world")
        workspace.execTask("actCustom") {
            workflows(workspace.dir.resolve("custom-workflows"))
        }

        val result = runner.build("actCustom")

        result.shouldSucceed(":actCustom")
        result.shouldHaveSuccessfulJobs("print_greeting")
    }

    "supports running a single workflow" {
        workspace.addWorkflows("custom-workflows/", "hello_world", "goodbye_world")
        workspace.execTask("actSingle") {
            workflows(workspace.dir.resolve("custom-workflows/hello_world.yml"))
        }

        val result = runner.build("actSingle")

        result.shouldSucceed(":actSingle")
        result.shouldHaveSuccessfulJobs("print_greeting")
    }

    "is never up-to-date" {
        workspace.addWorkflows(".github/workflows/", "hello_world")
        workspace.execTask("actAll")

        var result = runner.build("actAll")
        result.shouldSucceed(":actAll")
        result.shouldHaveSuccessfulJobs("print_greeting")

        result = runner.build("actAll")
        result.shouldSucceed(":actAll")
        result.shouldHaveSuccessfulJobs("print_greeting")
    }

    "allows passing arbitrary additional arguments to act" {
        workspace.addWorkflows("workflows/", "hello_world", "goodbye_world")
        workspace.execTask("actList") {
            workflows(workspace.dir.resolve("workflows/hello_world.yml"))
            additionalArgs("--list")
        }

        val result = runner.build("actList")

        result.shouldSucceed(":actList")
        result.output shouldContain "Workflow file"
        result.output shouldContain "hello_world.yml"
        result.output shouldNotContain "goodbye_world.yml"
    }

    "failing workflow will cause the task to fail" {
        workspace.addWorkflows(".github/workflows/", "failing_job")
        workspace.execTask("actFailing")

        val result = runner.buildAndFail("actFailing")

        result.shouldFail(":actFailing")
        result.shouldHaveFailedJobs("failing_job")
    }

    "respects task timeouts" {
        workspace.addWorkflows(".github/workflows/", "hello_world")
        workspace.execTask("actTimeout") {
            timeout(Duration.ofMillis(100))
        }

        val result = runner.buildAndFail("actTimeout")

        result.shouldFail(":actTimeout")
        // Sometimes our check kicks in before Gradle cancels the task
        result.outputShouldContainEither(
            "Timeout has been exceeded",
            "Failed to complete act command within the configured timeout"
        )
        result.output shouldNotContain "Job succeeded"
    }

    "works with configuration cache" {
        workspace.addWorkflows(".github/workflows/", "hello_world")
        workspace.execTask("actDefault")

        var result = runner.build("actDefault", "--configuration-cache")

        result.shouldSucceed(":actDefault")
        result.output shouldContain "Configuration cache entry stored"

        result = runner.build("actDefault", "--configuration-cache")

        result.shouldSucceed(":actDefault")
        result.output shouldContain "Reusing configuration cache"
    }
})
