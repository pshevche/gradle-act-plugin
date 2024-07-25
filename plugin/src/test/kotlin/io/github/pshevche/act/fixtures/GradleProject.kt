/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.pshevche.act.fixtures

import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.test.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.nio.file.Files

class GradleProject(private val workspaceDir: File) : BeforeTestListener {
    val buildFile by lazy { workspaceDir.resolve("build.gradle") }
    val settingsFile by lazy { workspaceDir.resolve("settings.gradle") }
    val workflowsRoot by lazy { workspaceDir.resolve(".github/workflows") }
    val specsRoot by lazy { workspaceDir.resolve(".github/act") }

    override suspend fun beforeTest(testCase: TestCase) {
        cleanWorkspace()
        withContext(Dispatchers.IO) {
            Files.createDirectories(workflowsRoot.toPath())
            Files.createDirectories(specsRoot.toPath())
        }
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
            
            """.trimIndent(),
        )
    }

    fun addWorkflow(
        workflowName: String,
        targetLocation: File = workflowsRoot.resolve("$workflowName.yml"),
    ) {
        Files.createDirectories(targetLocation.toPath().parent)
        copyWorkflowContent(targetLocation, workflowName)
    }

    fun addSpecResource(resourceName: String) {
        val resourceFile = specsRoot.resolve(resourceName)
        Files.createDirectories(resourceFile.toPath().parent)
        copyResourceContent(resourceFile, resourceName)
    }

    fun addSpec(
        filePath: String,
        content: String,
    ) {
        addSpec(specsRoot.resolve(filePath), content)
    }

    fun addSpec(
        specFile: File,
        content: String,
    ) {
        Files.createDirectories(specFile.toPath().parent)
        specFile.writeText(content)
    }

    fun run(vararg args: String) = runner(*args).build()

    fun runAndFail(vararg args: String) = runner(*args).buildAndFail()

    fun test(vararg args: String): BuildResult = run("actTest", *args)

    fun testAndFail(vararg args: String): BuildResult = runAndFail("actTest", *args)

    fun xmlReport(taskName: String = "actTest") = file("build/reports/act/$taskName/test.xml")

    private fun copyWorkflowContent(
        workflowFile: File,
        workflowName: String,
    ) {
        copyResourceContent(workflowFile, "workflows/$workflowName.yml")
    }

    private fun copyResourceContent(
        targetFile: File,
        resourcePath: String,
    ) {
        val resourceContent =
            this.javaClass.getResourceAsStream("/$resourcePath")!!
                .bufferedReader().use { reader ->
                    reader.readText()
                }

        targetFile.bufferedWriter().use { writer ->
            writer.write(resourceContent)
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

    private fun file(path: String): File = workspaceDir.toPath().resolve(path).toFile()
}
