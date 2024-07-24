package io.github.pshevche.act

import io.github.pshevche.act.fixtures.GradleProject
import io.github.pshevche.act.fixtures.assertEvents
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldContain
import org.opentest4j.reporting.events.core.Result.Status.SUCCESSFUL

class ActTestMatrixFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    beforeTest {
        project.addWorkflow("print_matrix_values")
    }

    "runs workflow with all matrix values by default" {
        project.addSpec(
            "matrix_values.act.yml", """
            name: default values
            workflow: print_matrix_values.yml
        """.trimIndent()
        )

        val result = project.test("-Dact.forwardOutput=true")

        result.output shouldContain "Hello, Bruce!"
        result.output shouldContain "Hello, Falco!"
        result.output shouldContain "Hallo, Bruce!"
        result.output shouldContain "Hallo, Falco!"

        assertEvents(project.xmlReport()) {
            spec(name = "default values", result = SUCCESSFUL) {
                job(name = "print_greeting-1", result = SUCCESSFUL)
                job(name = "print_greeting-2", result = SUCCESSFUL)
                job(name = "print_greeting-3", result = SUCCESSFUL)
                job(name = "print_greeting-4", result = SUCCESSFUL)
            }
        }
    }

    "supports restricting matrix values to run with" {
        project.addSpec(
            "matrix_values.act.yml", """
            name: default values
            workflow: print_matrix_values.yml
            matrix:
                greeting: Hello
                name: Bruce
        """.trimIndent()
        )

        val result = project.test("-Dact.forwardOutput=true")

        result.output shouldContain "Hello, Bruce!"
        assertEvents(project.xmlReport()) {
            spec(name = "default values", result = SUCCESSFUL) {
                job(name = "print_greeting", result = SUCCESSFUL)
            }
        }
    }
})
