package io.github.pshevche.act

import io.github.pshevche.act.extensions.BuildRunner
import io.github.pshevche.act.extensions.Workspace
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldContain

class ActPluginFuncTest : FreeSpec({

    val workspace = extension(Workspace(tempdir()))
    val runner = BuildRunner(workspace)

    "can run task" {
        val result = runner.build("greeting")
        result.output shouldContain "Hello from plugin 'io.github.pshevche.act'"
    }
})
