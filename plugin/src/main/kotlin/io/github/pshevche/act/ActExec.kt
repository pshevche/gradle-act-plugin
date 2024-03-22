package io.github.pshevche.act

import io.github.pshevche.act.internal.ActRunner
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Execute arbitrary GitHub workflows using [nektos/act](https://github.com/nektos/act).
 */
@Suppress("unused")
abstract class ActExec : DefaultTask() {

    /**
     * Path to workflow file(s) (default ".github/workflows").
     */
    @get:Input
    @get:Optional
    abstract val workflows: Property<File>

    /**
     * Additional args to pass to the `act` command.
     */
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
