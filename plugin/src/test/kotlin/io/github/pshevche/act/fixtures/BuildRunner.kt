package io.github.pshevche.act.fixtures

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

class BuildRunner(private val workspace: Workspace) {

    fun build(vararg args: String): BuildResult = runner(*args).build()

    fun buildAndFail(vararg args: String): BuildResult = runner(*args).buildAndFail()

    private fun runner(vararg args: String): GradleRunner =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments(args.toList())
            .withProjectDir(workspace.dir)
}
