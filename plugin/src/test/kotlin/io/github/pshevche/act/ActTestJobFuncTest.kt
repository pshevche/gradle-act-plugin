package io.github.pshevche.act

import io.github.pshevche.act.fixtures.GradleProject
import io.github.pshevche.act.fixtures.assertEvents
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import org.opentest4j.reporting.events.core.Result.Status.FAILED
import org.opentest4j.reporting.events.core.Result.Status.SUCCESSFUL

class ActTestJobFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    "reports failures for individual jobs" {
        project.addWorkflow("workflow_with_failing_and_passing_jobs")
        project.addSpec(
            "failing_and_passing_jobs.act.yml", """
            name: workflow with failing and passing jobs
            workflow: workflow_with_failing_and_passing_jobs.yml
        """.trimIndent()
        )

        project.testAndFail()

        assertEvents(project.xmlReport()) {
            spec(name = "workflow with failing and passing jobs", result = FAILED) {
                job(name = "successful_job", result = SUCCESSFUL)
                job(name = "failing_job", result = FAILED)
            }
        }
    }

    "supports selecting single jobs to run" {
        project.addWorkflow("workflow_with_failing_and_passing_jobs")
        project.addSpec(
            "failing_job.act.yml", """
            name: failing job only
            workflow: workflow_with_failing_and_passing_jobs.yml
            job: failing_job
        """.trimIndent()
        )
        project.addSpec(
            "passing_job.act.yml", """
            name: passing job only
            workflow: workflow_with_failing_and_passing_jobs.yml
            job: successful_job
        """.trimIndent()
        )

        project.testAndFail()

        assertEvents(project.xmlReport()) {
            spec(name = "failing job only", result = FAILED) {
                job(name = "failing_job", result = FAILED)
            }
            spec(name = "passing job only", result = SUCCESSFUL) {
                job(name = "successful_job", result = SUCCESSFUL)
            }
        }
    }
})
