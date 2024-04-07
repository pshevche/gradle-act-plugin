package io.github.pshevche.act.exec

import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldHaveSuccessfulJobs
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldSucceed
import io.github.pshevche.act.fixtures.GradleProject
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldContain

class WorkflowInputsActExecFuncTest : FreeSpec({

    timeout = 30 * 1000

    val project = extension(GradleProject(tempdir()))

    "allows settings workflow inputs as values" {
        project.addWorkflows(".github/workflows/", "hello_inputs")
        project.execTask("actInputValues") {
            inputValues(
                "GREETING" to "Hallo",
                "NAME" to "Welt"
            )
        }

        val result = project.build("actInputValues")

        result.shouldSucceed(":actInputValues")
        result.shouldHaveSuccessfulJobs("print_greeting_with_inputs")
        result.output shouldContain "Hallo, Welt!"
    }

    "sets workflow inputs from default file" {
        project.addWorkflows(".github/workflows/", "hello_inputs")
        project.workspaceDir.resolve(".input").apply {
            createNewFile()
            appendText("GREETING=Hallo\n")
            appendText("NAME=Welt\n")
        }
        project.execTask("actInputFile")

        val result = project.build("actInputFile")

        result.shouldSucceed(":actInputFile")
        result.shouldHaveSuccessfulJobs("print_greeting_with_inputs")
        result.output shouldContain "Hallo, Welt!"
    }

    "sets workflow inputs from custom file" {
        project.addWorkflows(".github/workflows/", "hello_inputs")
        val customInputs = project.workspaceDir.resolve(".customInput")
        customInputs.apply {
            createNewFile()
            appendText("GREETING=Hallo\n")
            appendText("NAME=Welt\n")
        }
        project.execTask("actInputFile") {
            inputsFile = customInputs
        }

        val result = project.build("actInputFile")

        result.shouldSucceed(":actInputFile")
        result.shouldHaveSuccessfulJobs("print_greeting_with_inputs")
        result.output shouldContain "Hallo, Welt!"
    }

    "sets workflow inputs both from file and as values" {
        project.addWorkflows(".github/workflows/", "hello_inputs")
        val customInputs = project.workspaceDir.resolve(".customInput")
        customInputs.apply {
            createNewFile()
            appendText("GREETING=Hallo\n")
        }
        project.execTask("actInputFileAndValues") {
            inputsFile = customInputs
            inputValues("NAME" to "Welt")
        }

        val result = project.build("actInputFileAndValues")

        result.shouldSucceed(":actInputFileAndValues")
        result.shouldHaveSuccessfulJobs("print_greeting_with_inputs")
        result.output shouldContain "Hallo, Welt!"
    }
})
