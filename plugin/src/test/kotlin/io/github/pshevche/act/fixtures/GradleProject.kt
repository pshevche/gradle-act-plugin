package io.github.pshevche.act.fixtures

import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.test.TestCase
import org.gradle.internal.impldep.com.google.common.io.Files
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

class GradleProject(val workspaceDir: File) : BeforeTestListener {

    private val buildFile by lazy { workspaceDir.resolve("build.gradle") }
    private val settingsFile by lazy { workspaceDir.resolve("settings.gradle") }

    override suspend fun beforeTest(testCase: TestCase) {
        cleanWorkspace()
        settingsFile.writeText("")
        buildFile.writeText(
            """
            plugins {
                id('io.github.pshevche.act')
            }
            """
        )
    }

    private fun cleanWorkspace() {
        workspaceDir.listFiles()?.forEach { it.deleteRecursively() }
    }

    fun addWorkflows(workspaceDir: String, vararg workflowNames: String) {
        workflowNames.forEach { workflowName ->
            val workflowFile = this.workspaceDir.resolve(workspaceDir).resolve("$workflowName.yml")
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

    fun build(vararg args: String): BuildResult = runner(*args).build()

    fun buildAndFail(vararg args: String): BuildResult = runner(*args).buildAndFail()

    private fun runner(vararg args: String): GradleRunner =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments(args.toList())
            .withProjectDir(workspaceDir)
}
