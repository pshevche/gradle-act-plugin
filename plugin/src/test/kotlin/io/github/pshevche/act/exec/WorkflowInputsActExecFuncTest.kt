package io.github.pshevche.act.exec

import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldHaveSuccessfulJobs
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldSucceed
import io.github.pshevche.act.fixtures.GradleProject
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldContain

class WorkflowInputsActExecFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    "allows settings workflow inputs as values" {
        project.addWorkflows(".github/workflows/", "workflow_with_inputs")
        project.execTask("actInputValues") {
            workflow(".github/workflows/workflow_with_inputs.yml")
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

    "sets workflow inputs from custom file" {
        project.addWorkflows(".github/workflows/", "workflow_with_inputs")
        val customInputs = project.workspaceDir.resolve(".customInput")
        customInputs.apply {
            createNewFile()
            appendText("GREETING=Hallo\n")
            appendText("NAME=Welt\n")
        }
        project.execTask("actInputFile") {
            workflow(".github/workflows/workflow_with_inputs.yml")
            inputsFile(customInputs)
        }

        val result = project.build("actInputFile")

        result.shouldSucceed(":actInputFile")
        result.shouldHaveSuccessfulJobs("print_greeting_with_inputs")
        result.output shouldContain "Hallo, Welt!"
    }

    "sets workflow inputs both from file and as values" {
        project.addWorkflows(".github/workflows/", "workflow_with_inputs")
        val customInputs = project.workspaceDir.resolve(".customInput")
        customInputs.apply {
            createNewFile()
            appendText("GREETING=Hallo\n")
        }
        project.execTask("actInputFileAndValues") {
            workflow(".github/workflows/workflow_with_inputs.yml")
            inputsFile(customInputs)
            inputValues("NAME" to "Welt")
        }

        val result = project.build("actInputFileAndValues")

        result.shouldSucceed(":actInputFileAndValues")
        result.shouldHaveSuccessfulJobs("print_greeting_with_inputs")
        result.output shouldContain "Hallo, Welt!"
    }
})
