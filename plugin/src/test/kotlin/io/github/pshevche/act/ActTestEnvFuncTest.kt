package io.github.pshevche.act

import io.github.pshevche.act.fixtures.GradleProject
import io.github.pshevche.act.fixtures.assertEvents
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldContain
import org.opentest4j.reporting.events.core.Result.Status.SUCCESSFUL

class ActTestEnvFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    beforeTest {
        project.addWorkflow("print_env_variables")
    }

    "supports setting environment variable values directly" {
        project.addSpec(
            "env_values.act.yml", """
            name: env values
            workflow: print_env_variables.yml
            env:
                values:
                    GREETING: Hello
                    NAME: Jeff
        """.trimIndent()
        )

        val result = project.test("-Dact.forwardOutput=true")

        result.output shouldContain "Hello, Jeff!"
        assertEvents(project.xmlReport()) {
            spec(name = "env values", result = SUCCESSFUL) {
                job(name = "print_greeting", result = SUCCESSFUL)
            }
        }
    }

    "supports setting environment variables from file" {
        project.addSpecResource("inputs/example_env.env")
        project.addSpec(
            "env_file.act.yml", """
            name: env file
            workflow: print_env_variables.yml
            env:
                file: inputs/example_env.env
        """.trimIndent()
        )

        val result = project.test("-Dact.forwardOutput=true")

        result.output shouldContain "Hallo, Falco!"
        assertEvents(project.xmlReport()) {
            spec(name = "env file", result = SUCCESSFUL) {
                job(name = "print_greeting", result = SUCCESSFUL)
            }
        }
    }
})
