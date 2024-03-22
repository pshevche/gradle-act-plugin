package io.github.pshevche.act.fixtures

import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.test.TestCase
import org.gradle.internal.impldep.com.google.common.io.Files
import java.io.File

class Workspace(val dir: File) : BeforeTestListener {

    private val buildFile by lazy { dir.resolve("build.gradle") }
    private val settingsFile by lazy { dir.resolve("settings.gradle") }

    override suspend fun beforeTest(testCase: TestCase) {
        settingsFile.writeText("")
        buildFile.writeText(
            """
            plugins {
                id('io.github.pshevche.act')
            }
            """
        )
    }

    fun addWorkflows(workspaceDir: String, vararg workflowNames: String) {
        workflowNames.forEach { workflowName ->
            val workflowFile = dir.resolve(workspaceDir).resolve("$workflowName.yml")
            Files.createParentDirs(workflowFile)
            copyWorkflowContent(workflowFile, workflowName)
        }
    }

    private fun copyWorkflowContent(workflowFile: File, workflowName: String) {
        val workflowContent = this.javaClass.getResourceAsStream("/workflows/$workflowName.yml")!!
            .bufferedReader().use { reader ->
                reader.readText()
            }

        workflowFile.bufferedWriter().use { writer ->
            writer.write(workflowContent)
        }
    }

    fun execTask(name: String, config: ActExecTaskBuilder.() -> Unit = { }) {
        val taskConfig = ActExecTaskBuilder()
            .apply(config)
            .toTaskConfig()
        if (taskConfig.isBlank()) {
            buildFile.appendText("tasks.register('$name', io.github.pshevche.act.ActExec)")
        } else {
            buildFile.appendText(
                """
                tasks.register('$name', io.github.pshevche.act.ActExec) {
                    $taskConfig    
                }
                """
            )
        }
    }
}
