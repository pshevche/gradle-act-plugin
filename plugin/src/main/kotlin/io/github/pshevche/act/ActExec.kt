package io.github.pshevche.act

import io.github.pshevche.act.internal.ActRunner
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class ActExec : DefaultTask() {

    @get:Input
    @get:Optional
    abstract val workflows: Property<File>

    @get:Input
    @get:Optional
    val additionalArgs: ListProperty<String> = project.objects.listProperty(String::class.java)

    init {
        workflows.convention(project.layout.projectDirectory.dir(".github/workflows").asFile)
        additionalArgs.convention(emptyList())
    }

    @TaskAction
    fun exec() {
        val runner = ActRunner()
        runner.workflows(workflows.get())
            .additionalArgs(additionalArgs.get())
        runner.exec()
    }
}
