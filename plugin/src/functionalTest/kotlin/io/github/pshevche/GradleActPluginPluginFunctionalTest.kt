package io.github.pshevche

import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.GradleRunner

class GradleActPluginPluginFunctionalTest : FreeSpec({

    val projectDir = tempdir()
    val buildFile by lazy { projectDir.resolve("build.gradle") }
    val settingsFile by lazy { projectDir.resolve("settings.gradle") }

    "can run task" {
        settingsFile.writeText("")
        buildFile.writeText("""
            plugins {
                id('io.github.pshevche.greeting')
            }
        """.trimIndent())

        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("greeting")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        result.output shouldContain "Hello from plugin 'io.github.pshevche.greeting'"
    }
})
