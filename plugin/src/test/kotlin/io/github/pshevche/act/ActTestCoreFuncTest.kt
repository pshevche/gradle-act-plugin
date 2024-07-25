package io.github.pshevche.act

import io.github.pshevche.act.fixtures.GradleProject
import io.github.pshevche.act.fixtures.assertEvents
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
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
            "always_failing.act.yml",
            """
            name: always failing workflow
            workflow: always_failing_workflow.yml
            """.trimIndent(),
        )

        val result = project.testAndFail()
        result.task(":actTest")?.outcome shouldBe TaskOutcome.FAILED
    }

    "is up-to-date" {
        project.addWorkflow("always_passing_workflow")
        project.addSpec(
            "always_passing.act.yml",
            """
            name: always passing workflow
            workflow: always_passing_workflow.yml
            """.trimIndent(),
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
            """.trimIndent(),
        )

        project.addWorkflow("always_passing_workflow")
        project.addSpec(
            "always_passing.act.yml",
            """
            name: always passing workflow
            workflow: always_passing_workflow.yml
            """.trimIndent(),
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
            "passing.act.yml",
            """
            name: always passing workflow
            workflow: always_passing_workflow.yml
            """.trimIndent(),
        )
        project.addSpec(
            "failing.act.yml",
            """
            name: always failing workflow
            workflow: always_failing_workflow.yml
            """.trimIndent(),
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

    "discovers specs and workflow in nested directories" {
        project.addWorkflow("always_passing_workflow")
        project.addWorkflow(
            "always_failing_workflow",
            project.workflowsRoot.resolve("nested/always_failing_workflow.yml"),
        )
        project.addSpec(
            "passing.act.yml",
            """
            name: always passing workflow
            workflow: always_passing_workflow.yml
            """.trimIndent(),
        )
        project.addSpec(
            "nested/failing.act.yml",
            """
            name: always failing workflow
            workflow: nested/always_failing_workflow.yml
            """.trimIndent(),
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

    "supports both YML and YAML spec extensions" {
        project.addWorkflow("always_passing_workflow")
        project.addSpec(
            "passing_yml.act.yml",
            """
            name: YML spec
            workflow: always_passing_workflow.yml
            """.trimIndent(),
        )
        project.addSpec(
            "passing_yml.act.yaml",
            """
            name: YAML spec
            workflow: always_passing_workflow.yml
            """.trimIndent(),
        )

        project.test()

        assertEvents(project.xmlReport()) {
            spec(name = "YAML spec", result = SUCCESSFUL) {
                job(name = "successful_job", result = SUCCESSFUL)
            }
            spec(name = "YML spec", result = SUCCESSFUL) {
                job(name = "successful_job", result = SUCCESSFUL)
            }
        }
    }

    withData(
        nameFn = { "output forwarding can be enabled via a system property ($it)" },
        false,
        true,
    ) { enabled ->
        project.addWorkflow("always_passing_workflow")
        project.addSpec(
            "passing.act.yml",
            """
            name: always passing workflow
            workflow: always_passing_workflow.yml
            """.trimIndent(),
        )

        val args = if (enabled) listOf("-Dact.forwardOutput=true") else listOf()
        val result = project.test(*args.toTypedArray())

        result.output.contains("Hello, World!") shouldBe enabled
    }

    withData(
        nameFn = { "output forwarding can be enabled via task configuration ($it)" },
        false,
        true,
    ) { enabled ->
        project.addWorkflow("always_passing_workflow")
        project.addSpec(
            "passing.act.yml",
            """
            name: always passing workflow
            workflow: always_passing_workflow.yml
            """.trimIndent(),
        )
        project.buildFile.appendText(
            """
            tasks.actTest {
                forwardActOutput = $enabled
            }
            """.trimIndent(),
        )

        val result = project.test()

        result.output.contains("Hello, World!") shouldBe enabled
    }

    "does not register a task if extension is not configured" {
        project.buildFile.writeText(
            """
            plugins {
                id('base')
                id('io.github.pshevche.act')
            }
            """.trimIndent(),
        )
        val result = project.testAndFail()
        result.output shouldContain "property 'specsRoot' doesn't have a configured value"
    }

    "missing extension configuration does not break the build" {
        project.buildFile.writeText(
            """
            plugins {
                id('base')
                id('io.github.pshevche.act')
            }
            """.trimIndent(),
        )
        project.run("help")
    }

    "supports defining custom test tasks" {
        val customSuccessfulWorkflowsRoot = tempdir("successfulWorkflows")
        val customSuccessfulSpecsRoot = tempdir("successfulSpecs")
        val customFailingWorkflowsRoot = tempdir("failingWorkflows")
        val customFailingSpecsRoot = tempdir("failingSpecs")

        project.buildFile.writeText(
            """
            plugins {
                id('base')
                id('io.github.pshevche.act')
            }
            
            tasks.register('customSuccessfulActTest', io.github.pshevche.act.ActTest) {
                workflowsRoot = file('${customSuccessfulWorkflowsRoot.absolutePath}')
                specsRoot = file('${customSuccessfulSpecsRoot.absolutePath}')
            }
            
            tasks.register('customFailingActTest', io.github.pshevche.act.ActTest) {
                workflowsRoot = file('${customFailingWorkflowsRoot.absolutePath}')
                specsRoot = file('${customFailingSpecsRoot.absolutePath}')
            }
            """.trimIndent(),
        )

        project.addWorkflow(
            "always_passing_workflow",
            customSuccessfulWorkflowsRoot.resolve("always_passing_workflow.yaml"),
        )
        project.addSpec(
            customSuccessfulSpecsRoot.resolve("custom.act.yaml"),
            """
            name: successful spec in custom root
            workflow: always_passing_workflow.yaml
            """.trimIndent(),
        )
        project.addWorkflow(
            "always_failing_workflow",
            customFailingWorkflowsRoot.resolve("always_failing_workflow.yaml"),
        )
        project.addSpec(
            customFailingSpecsRoot.resolve("custom.act.yaml"),
            """
            name: failing spec in custom root
            workflow: always_failing_workflow.yaml
            """.trimIndent(),
        )

        project.runAndFail("customSuccessfulActTest", "customFailingActTest")

        assertEvents(project.xmlReport("customSuccessfulActTest")) {
            spec(name = "successful spec in custom root", result = SUCCESSFUL) {
                job(name = "successful_job", result = SUCCESSFUL)
            }
        }
        assertEvents(project.xmlReport("customFailingActTest")) {
            spec(name = "failing spec in custom root", result = FAILED) {
                job(name = "failing_job", result = FAILED)
            }
        }
    }

    "supports task timeouts" {
        project.addWorkflow("always_passing_workflow")
        project.addSpec(
            "always_passing.act.yml",
            """
            name: always passing workflow
            workflow: always_passing_workflow.yml
            """.trimIndent(),
        )

        project.buildFile.appendText(
            """
            tasks.actTest {
                timeout = java.time.Duration.ofMillis(10)
            }
            """.trimIndent(),
        )

        val result = project.testAndFail()

        result.output shouldContain "Timeout has been exceeded"
    }

    /**
     * Only works if run via Gradle, not via Kotest IntelliJ plugin.
     */
    "works with configuration cache" {
        project.addWorkflow("always_passing_workflow")
        project.addSpec(
            "always_passing.act.yml",
            """
            name: always passing workflow
            workflow: always_passing_workflow.yml
            """.trimIndent(),
        )

        project.test("--configuration-cache")

        assertEvents(project.xmlReport()) {
            spec(name = "always passing workflow", result = SUCCESSFUL) {
                job(name = "successful_job", result = SUCCESSFUL)
            }
        }
    }
})
