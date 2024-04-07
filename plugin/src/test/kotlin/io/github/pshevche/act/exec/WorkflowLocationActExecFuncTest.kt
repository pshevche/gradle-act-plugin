package io.github.pshevche.act.exec

import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldHaveSuccessfulJobs
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldSucceed
import io.github.pshevche.act.fixtures.GradleProject
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir

class WorkflowLocationActExecFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    "runs workflows in the default location" {
        project.addWorkflows(".github/workflows/", "hello_world", "goodbye_world")
        project.execTask("actAll")

        val result = project.build("actAll")

        result.shouldSucceed(":actAll")
        result.shouldHaveSuccessfulJobs("print_greeting", "print_farewell")
    }

    "runs workflows in a custom directory" {
        project.addWorkflows("custom-workflows/", "hello_world")
        project.execTask("actCustom") {
            workflows(project.workspaceDir.resolve("custom-workflows"))
        }

        val result = project.build("actCustom")

        result.shouldSucceed(":actCustom")
        result.shouldHaveSuccessfulJobs("print_greeting")
    }

    "runs custom workflow file" {
        project.addWorkflows("custom-workflows/", "hello_world", "goodbye_world")
        project.execTask("actSingle") {
            workflows(project.workspaceDir.resolve("custom-workflows/hello_world.yml"))
        }

        val result = project.build("actSingle")

        result.shouldSucceed(":actSingle")
        result.shouldHaveSuccessfulJobs("print_greeting")
    }
})
