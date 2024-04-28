package io.github.pshevche.act.exec

import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldHaveSuccessfulJobs
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldSucceed
import io.github.pshevche.act.fixtures.GradleProject
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldContain

class WorkflowSecretsActExecFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    "allows configuring secrets values" {
        project.addWorkflows(".github/workflows/", "workflow_with_secrets")
        project.execTask("actSecretValues") {
            workflow(".github/workflows/workflow_with_secrets.yml")
            secretValues(
                "GREETING" to "Hallo",
                "NAME" to "Welt"
            )
            additionalArgs("--insecure-secrets")
        }

        val result = project.build("actSecretValues")

        result.shouldSucceed(":actSecretValues")
        result.shouldHaveSuccessfulJobs("print_greeting_with_secrets")
        result.output shouldContain "Hallo, Welt!"
    }

    "configures secrets from custom file" {
        project.addWorkflows(".github/workflows/", "workflow_with_secrets")
        val customSecrets = project.workspaceDir.resolve(".customSecrets")
        customSecrets.apply {
            createNewFile()
            appendText("GREETING=Hallo\n")
            appendText("NAME=Welt\n")
        }
        project.execTask("actSecretsFile") {
            workflow(".github/workflows/workflow_with_secrets.yml")
            secretsFile(customSecrets)
            additionalArgs("--insecure-secrets")
        }

        val result = project.build("actSecretsFile")

        result.shouldSucceed(":actSecretsFile")
        result.shouldHaveSuccessfulJobs("print_greeting_with_secrets")
        result.output shouldContain "Hallo, Welt!"
    }

    "hides secrets values by default" {
        project.addWorkflows(".github/workflows/", "workflow_with_secrets")
        val customSecrets = project.workspaceDir.resolve(".secrets").apply {
            createNewFile()
            appendText("GREETING=Hallo\n")
            appendText("NAME=Welt\n")
        }
        project.execTask("actSecretsFile") {
            workflow(".github/workflows/workflow_with_secrets.yml")
            secretsFile(customSecrets)
        }

        val result = project.build("actSecretsFile")

        result.shouldSucceed(":actSecretsFile")
        result.shouldHaveSuccessfulJobs("print_greeting_with_secrets")
        result.output shouldContain "***, ***!"
    }
})
