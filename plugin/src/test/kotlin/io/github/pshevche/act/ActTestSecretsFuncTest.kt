package io.github.pshevche.act

import io.github.pshevche.act.fixtures.GradleProject
import io.github.pshevche.act.fixtures.assertEvents
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldContain
import org.opentest4j.reporting.events.core.Result.Status.SUCCESSFUL

class ActTestSecretsFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    beforeTest {
        project.addWorkflow("print_secrets")
    }

    "supports setting secrets values directly" {
        project.addSpec(
            "secrets_values.act.yml", """
            name: secrets values
            workflow: print_secrets.yml
            secrets:
                values:
                    GREETING: Hello
                    NAME: Bruce
            # otherwise, the secrets will be obfuscated
            additionalArgs:
                - --insecure-secrets
        """.trimIndent()
        )

        val result = project.test("-Dact.forwardOutput=true")

        result.output shouldContain "Hello, Bruce!"
        assertEvents(project.xmlReport()) {
            spec(name = "secrets values", result = SUCCESSFUL) {
                job(name = "print_greeting", result = SUCCESSFUL)
            }
        }
    }

    "supports setting workflow inputs from file" {
        project.addSpecResource("inputs/greeting.secrets")
        project.addSpec(
            "secrets_files.act.yml", """
            name: secrets file
            workflow: print_secrets.yml
            secrets:
                file: inputs/greeting.secrets
            # otherwise, the secrets will be obfuscated
            additionalArgs:
                - --insecure-secrets
        """.trimIndent()
        )

        val result = project.test("-Dact.forwardOutput=true")

        result.output shouldContain "Hallo, Falco!"
        assertEvents(project.xmlReport()) {
            spec(name = "secrets file", result = SUCCESSFUL) {
                job(name = "print_greeting", result = SUCCESSFUL)
            }
        }
    }
})
