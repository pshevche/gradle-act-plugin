package io.github.pshevche.act.exec

import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldHaveSuccessfulJobs
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldSucceed
import io.github.pshevche.act.fixtures.GradleProject
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir

class JobFilterActExecFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    "runs specified job from the workflow" {
        project.addWorkflows(".github/workflows/", "multiple_jobs")
        project.execTask("actAll") {
            workflow(".github/workflows/multiple_jobs.yml")
            job("print_farewell")
        }

        val result = project.build("actAll")

        result.shouldSucceed(":actAll")
        result.shouldHaveSuccessfulJobs("print_farewell")
    }

    "runs all jobs by default" {
        project.addWorkflows(".github/workflows/", "multiple_jobs")
        project.execTask("actAll") {
            workflow(".github/workflows/multiple_jobs.yml")
        }

        val result = project.build("actAll")

        result.shouldSucceed(":actAll")
        result.shouldHaveSuccessfulJobs("print_greeting", "print_farewell")
    }
})
