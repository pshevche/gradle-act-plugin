package io.github.pshevche.act.extensions

import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.test.TestCase
import java.io.File

class Workspace(val dir: File) : BeforeTestListener {

    val buildFile by lazy { dir.resolve("build.gradle") }
    val settingsFile by lazy { dir.resolve("settings.gradle") }

    override suspend fun beforeTest(testCase: TestCase) {
        settingsFile.writeText("")
        buildFile.writeText(
            """
            plugins {
                id('io.github.pshevche.act')
            }
            """.trimIndent()
        )
    }
}
