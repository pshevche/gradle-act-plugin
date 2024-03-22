package io.github.pshevche.act

import io.github.pshevche.act.extensions.BuildRunner
import io.github.pshevche.act.extensions.Workspace
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldContain

class ActExecFuncTest : FreeSpec({

    val workspace = extension(Workspace(tempdir()))
    val runner = BuildRunner(workspace)

    "can run task" {
        workspace.execTask("actExec")
        val result = runner.build("actExec")
        result.output shouldContain "Hello, World!"
    }
})
