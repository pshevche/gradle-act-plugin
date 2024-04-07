package io.github.pshevche.act.exec

import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldHaveSuccessfulJobs
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldSucceed
import io.github.pshevche.act.fixtures.GradleProject
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldContain

class ConfigurationVarsActExecFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    "allows setting configuration vars values" {
        project.addWorkflows(".github/workflows/", "hello_vars")
        project.execTask("actVarValues") {
            variableValues(
                "GREETING" to "Hallo",
                "NAME" to "Welt"
            )
        }

        val result = project.build("actVarValues")

        result.shouldSucceed(":actVarValues")
        result.shouldHaveSuccessfulJobs("print_greeting_with_vars")
        result.output shouldContain "Hallo, Welt!"
    }

    "sets configuration vars from default file" {
        project.addWorkflows(".github/workflows/", "hello_vars")
        project.workspaceDir.resolve(".vars").apply {
            createNewFile()
            appendText("GREETING=Hallo\n")
            appendText("NAME=Welt\n")
        }
        project.execTask("actVarFile")

        val result = project.build("actVarFile")

        result.shouldSucceed(":actVarFile")
        result.shouldHaveSuccessfulJobs("print_greeting_with_vars")
        result.output shouldContain "Hallo, Welt!"
    }

    "sets configuration vars from custom file" {
        project.addWorkflows(".github/workflows/", "hello_vars")
        val customVars = project.workspaceDir.resolve(".customEnv")
        customVars.apply {
            createNewFile()
            appendText("GREETING=Hallo\n")
            appendText("NAME=Welt\n")
        }
        project.execTask("actVarFile") {
            variablesFile = customVars
        }

        val result = project.build("actVarFile")

        result.shouldSucceed(":actVarFile")
        result.shouldHaveSuccessfulJobs("print_greeting_with_vars")
        result.output shouldContain "Hallo, Welt!"
    }

    "sets configuration vars both from file and as values" {
        project.addWorkflows(".github/workflows/", "hello_vars")
        val customVars = project.workspaceDir.resolve(".customEnv")
        customVars.apply {
            createNewFile()
            appendText("GREETING=Hallo\n")
        }
        project.execTask("actVarFileAndValues") {
            variablesFile = customVars
            variableValues("NAME" to "Welt")
        }

        val result = project.build("actVarFileAndValues")

        result.shouldSucceed(":actVarFileAndValues")
        result.shouldHaveSuccessfulJobs("print_greeting_with_vars")
        result.output shouldContain "Hallo, Welt!"
    }
})
