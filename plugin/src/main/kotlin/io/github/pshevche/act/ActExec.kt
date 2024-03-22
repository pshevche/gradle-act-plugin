package io.github.pshevche.act

import io.github.pshevche.act.internal.ActRunner
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class ActExec : DefaultTask() {

    @get:InputDirectory
    @get:Optional
    val workflows: DirectoryProperty = project.objects.directoryProperty()

    init {
        workflows.convention(project.layout.projectDirectory.dir(".github/workflows"))
    }

    @TaskAction
    fun exec() {
        val runner = ActRunner()
        runner.workflows(workflows.get().asFile.toPath())
        runner.exec()
    }
}
