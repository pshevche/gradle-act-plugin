package io.github.pshevche.act.exec

import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldSucceed
import io.github.pshevche.act.fixtures.GradleProject
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain

class AdditionalArgsActExecFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    "allows passing arbitrary additional arguments to act" {
        project.addWorkflows("workflows/", "hello_world", "goodbye_world")
        project.execTask("actList") {
            workflow(project.workspaceDir.resolve("workflows/hello_world.yml"))
            additionalArgs("--list")
        }

        val result = project.build("actList")

        result.shouldSucceed(":actList")
        result.output shouldContain "Workflow file"
        result.output shouldContain "hello_world.yml"
        result.output shouldNotContain "goodbye_world.yml"
    }
})
