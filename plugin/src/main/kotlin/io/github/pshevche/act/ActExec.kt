package io.github.pshevche.act

import io.github.pshevche.act.internal.ActRunner
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class ActExec : DefaultTask() {

    @get:Input
    @get:Optional
    abstract val workflows: Property<File>

    init {
        workflows.convention(project.layout.projectDirectory.dir(".github/workflows").asFile)
    }

    @TaskAction
    fun exec() {
        val runner = ActRunner()
        runner.workflows(workflows.get())
        runner.exec()
    }
}
