package io.github.pshevche.act.exec

import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldHaveSuccessfulJobs
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldSucceed
import io.github.pshevche.act.fixtures.GradleProject
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldContain

class WorkflowEnvActExecFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    "allows configuring workflow env as values" {
        project.addWorkflows(".github/workflows/", "workflow_with_env")
        project.execTask("actEnvValues") {
            workflow(".github/workflows/workflow_with_env.yml")
            envValues(
                "GREETING" to "Hallo",
                "NAME" to "Welt"
            )
        }

        val result = project.build("actEnvValues")

        result.shouldSucceed(":actEnvValues")
        result.shouldHaveSuccessfulJobs("print_greeting_with_env")
        result.output shouldContain "Hallo, Welt!"
    }

    "configure workflow env from custom file" {
        project.addWorkflows(".github/workflows/", "workflow_with_env")
        val customEnv = project.workspaceDir.resolve(".customEnv")
        customEnv.apply {
            createNewFile()
            appendText("GREETING=Hallo\n")
            appendText("NAME=Welt\n")
        }
        project.execTask("actEnvFile") {
            workflow(".github/workflows/workflow_with_env.yml")
            envFile(customEnv)
        }

        val result = project.build("actEnvFile")

        result.shouldSucceed(":actEnvFile")
        result.shouldHaveSuccessfulJobs("print_greeting_with_env")
        result.output shouldContain "Hallo, Welt!"
    }

    "configures workflow env both from file and as values" {
        project.addWorkflows(".github/workflows/", "workflow_with_env")
        val customEnv = project.workspaceDir.resolve(".customEnv")
        customEnv.apply {
            createNewFile()
            appendText("GREETING=Hallo\n")
        }
        project.execTask("actEnvFileAndValues") {
            workflow(".github/workflows/workflow_with_env.yml")
            envFile(customEnv)
            envValues("NAME" to "Welt")
        }

        val result = project.build("actEnvFileAndValues")

        result.shouldSucceed(":actEnvFileAndValues")
        result.shouldHaveSuccessfulJobs("print_greeting_with_env")
        result.output shouldContain "Hallo, Welt!"
    }
})
