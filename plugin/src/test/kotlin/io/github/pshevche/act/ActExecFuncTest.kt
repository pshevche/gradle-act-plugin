package io.github.pshevche.act

import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldRunJobs
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldSucceed
import io.github.pshevche.act.fixtures.BuildRunner
import io.github.pshevche.act.fixtures.Workspace
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir

class ActExecFuncTest : FreeSpec({

    val workspace = extension(Workspace(tempdir()))
    val runner = BuildRunner(workspace)

    "runs workflows in the default location" {
        workspace.addWorkflows(".github/workflows/", "hello_world", "goodbye_world")
        workspace.execTask("actAll")

        val result = runner.build("actAll")

        result.shouldSucceed(":actAll")
        result.shouldRunJobs("print_greeting", "print_farewell")
    }

    "supports overriding the location of workflow files" {
        workspace.addWorkflows("custom-workflows/", "hello_world")
        workspace.execTask("actCustom") {
            workflows(workspace.dir.resolve("custom-workflows"))
        }

        val result = runner.build("actCustom")

        result.shouldSucceed(":actCustom")
        result.shouldRunJobs("print_greeting")
    }

    "supports running a single workflow" {
        workspace.addWorkflows("custom-workflows/", "hello_world", "goodbye_world")
        workspace.execTask("actSingle") {
            workflows(workspace.dir.resolve("custom-workflows/hello_world.yml"))
        }

        val result = runner.build("actSingle")

        result.shouldSucceed(":actSingle")
        result.shouldRunJobs("print_greeting")
    }

    "is never up-to-date" {
        workspace.addWorkflows(".github/workflows/", "hello_world")
        workspace.execTask("actAll")

        var result = runner.build("actAll")
        result.shouldSucceed(":actAll")
        result.shouldRunJobs("print_greeting")

        result = runner.build("actAll")
        result.shouldSucceed(":actAll")
        result.shouldRunJobs("print_greeting")
    }
})