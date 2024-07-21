package io.github.pshevche.act

import io.github.pshevche.act.fixtures.GradleProject
import io.github.pshevche.act.fixtures.assertEvents
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.TaskOutcome
import org.opentest4j.reporting.events.core.Result.Status.FAILED
import org.opentest4j.reporting.events.core.Result.Status.SUCCESSFUL

class ActTestCoreFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    "warns if no spec files are defined" {
        val result = project.test()
        result.output shouldContain "Act plugin has not detected any specs to execute"
    }

    "spec failures cause task failures" {
        project.addWorkflow("always_failing_workflow")
        project.addSpec(
            "always_failing.act.yml", """
            name: always failing workflow
            workflow: always_failing_workflow.yml
        """.trimIndent()
        )

        val result = project.testAndFail()
        result.task(":actTest")?.outcome shouldBe TaskOutcome.FAILED
    }

    "is up-to-date" {
        project.addWorkflow("always_passing_workflow")
        project.addSpec(
            "always_passing.act.yml", """
                name: always passing workflow
                workflow: always_passing_workflow.yml
            """.trimIndent()
        )

        var result = project.test()
        result.task(":actTest")?.outcome shouldBe TaskOutcome.SUCCESS

        result = project.test()
        result.task(":actTest")?.outcome shouldBe TaskOutcome.UP_TO_DATE
    }

    "is cacheable" {
        project.settingsFile.appendText(
            """
            buildCache {
                local {
                    directory = file("${'$'}{rootDir}/build-cache")
                }
            }
            """.trimIndent()
        )

        project.addWorkflow("always_passing_workflow")
        project.addSpec(
            "always_passing.act.yml", """
                name: always passing workflow
                workflow: always_passing_workflow.yml
            """.trimIndent()
        )

        var result = project.run("clean", "actTest", "--build-cache")
        result.task(":actTest")?.outcome shouldBe TaskOutcome.SUCCESS

        result = project.run("clean", "actTest", "--build-cache")
        result.task(":actTest")?.outcome shouldBe TaskOutcome.FROM_CACHE

        project.addWorkflow("always_failing_workflow")
        result = project.run("clean", "actTest", "--build-cache")
        result.task(":actTest")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    "generates expected XML reports" {
        project.addWorkflow("always_passing_workflow")
        project.addWorkflow("always_failing_workflow")
        project.addSpec(
            "passing.act.yml", """
            name: always passing workflow
            workflow: always_passing_workflow.yml
        """.trimIndent()
        )
        project.addSpec(
            "failing.act.yml", """
            name: always failing workflow
            workflow: always_failing_workflow.yml
        """.trimIndent()
        )

        project.testAndFail()

        assertEvents(project.xmlReport()) {
            spec(name = "always passing workflow", result = SUCCESSFUL) {
                job(name = "successful_job", result = SUCCESSFUL)
            }
            spec(name = "always failing workflow", result = FAILED) {
                job(name = "failing_job", result = FAILED)
            }
        }
    }

    "discovers specs and workflow in nested directories" {
        project.addWorkflow("always_passing_workflow")
        project.addWorkflow("always_failing_workflow", "nested/always_failing_workflow.yml")
        project.addSpec(
            "passing.act.yml", """
            name: always passing workflow
            workflow: always_passing_workflow.yml
        """.trimIndent()
        )
        project.addSpec(
            "nested/failing.act.yml", """
            name: always failing workflow
            workflow: nested/always_failing_workflow.yml
        """.trimIndent()
        )

        project.testAndFail()

        assertEvents(project.xmlReport()) {
            spec(name = "always failing workflow", result = FAILED) {
                job(name = "failing_job", result = FAILED)
            }
            spec(name = "always passing workflow", result = SUCCESSFUL) {
                job(name = "successful_job", result = SUCCESSFUL)
            }
        }
    }
})
