package io.github.pshevche.act.fixtures

import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.test.TestCase
import org.gradle.internal.impldep.com.google.common.io.Files
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

class GradleProject(private val workspaceDir: File) : BeforeTestListener {

    private val buildFile by lazy { workspaceDir.resolve("build.gradle") }
    val settingsFile by lazy { workspaceDir.resolve("settings.gradle") }
    private val workflowsRoot by lazy { workspaceDir.resolve(".github/workflows") }
    private val specsRoot by lazy { workspaceDir.resolve(".github/act") }

    override suspend fun beforeTest(testCase: TestCase) {
        cleanWorkspace()
        settingsFile.writeText("")
        buildFile.writeText(
            """
            plugins {
                id('base')
                id('io.github.pshevche.act')
            }
            
            act {
                workflowsRoot = file('${workflowsRoot.toPath()}')
                specsRoot = file('${specsRoot.toPath()}')
            }
            
            """.trimIndent()
        )
    }

    fun addWorkflow(workflowName: String, targetLocation: String = "$workflowName.yml") {
        val workflowFile = workflowsRoot.resolve(targetLocation)
        Files.createParentDirs(workflowFile)
        copyWorkflowContent(workflowFile, workflowName)
    }

    fun addSpec(filePath: String, content: String) {
        val specFile = specsRoot.resolve(filePath)
        Files.createParentDirs(specFile)
        specFile.writeText(content)
    }

    fun run(vararg args: String) = runner(*args).build()

    fun test(vararg args: String): BuildResult = run("actTest", *args)

    fun testAndFail(vararg args: String): BuildResult = runner("actTest", *args).buildAndFail()

    fun xmlReport() = file("build/reports/act/test.xml")

    fun htmlReport() = file("build/reports/act/test.html")

    private fun copyWorkflowContent(workflowFile: File, workflowName: String) {
        val workflowContent = this.javaClass.getResourceAsStream("/workflows/$workflowName.yml")!!
            .bufferedReader().use { reader ->
                reader.readText()
            }

        workflowFile.bufferedWriter().use { writer ->
            writer.write(workflowContent)
        }
    }

    private fun cleanWorkspace() {
        workspaceDir.listFiles()?.forEach { it.deleteRecursively() }
    }

    private fun runner(vararg args: String): GradleRunner =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments(args.toList())
            .withProjectDir(workspaceDir)
            .withDebug(true)

    private fun file(path: String): File =
        workspaceDir.toPath().resolve(path).toFile()
}
