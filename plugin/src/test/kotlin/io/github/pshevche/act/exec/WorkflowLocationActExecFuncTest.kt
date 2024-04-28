package io.github.pshevche.act.exec

import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldHaveSuccessfulJobs
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldSucceed
import io.github.pshevche.act.fixtures.GradleProject
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir

class WorkflowLocationActExecFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    "runs custom workflow file" {
        project.addWorkflows("custom-workflows/", "hello_world", "goodbye_world")
        project.execTask("actSingle") {
            workflow(project.workspaceDir.resolve("custom-workflows/hello_world.yml"))
        }

        val result = project.build("actSingle")

        result.shouldSucceed(":actSingle")
        result.shouldHaveSuccessfulJobs("print_greeting")
    }
})
