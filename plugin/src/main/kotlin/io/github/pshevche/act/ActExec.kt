package io.github.pshevche.act

import io.github.pshevche.act.internal.ActRunner
import io.github.pshevche.act.internal.DefaultGithubActionInput
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Execute arbitrary GitHub workflows using [nektos/act](https://github.com/nektos/act).
 */
@Suppress("unused")
open class ActExec : DefaultTask() {

    private val projectDir = project.layout.projectDirectory
    private val objects = project.objects

    /**
     * Path to workflow file(s) (default ".github/workflows").
     */
    @get:Input
    @get:Optional
    val workflows: Property<File> =
        objects.property(File::class.java).convention(projectDir.dir(".github/workflows").asFile)

    /**
     * Env to make available to actions.
     * Can be specified in a separate file (default .env) or as a map of values.
     */
    @get:Nested
    @get:Optional
    val env: GithubActionInput = DefaultGithubActionInput(objects, projectDir.file(".env").asFile)

    /**
     * Env to make available to actions.
     * Can be specified in a separate file (default .env) or as a map of values.
     */
    fun env(action: Action<GithubActionInput>) = action.execute(env)

    /**
     * Secrets to make available to actions.
     * Can be specified in a separate file (default .secrets) or as a map of values.
     */
    @get:Nested
    @get:Optional
    val secrets: GithubActionInput = DefaultGithubActionInput(objects, projectDir.file(".secrets").asFile)

    /**
     * Secrets to make available to actions.
     * Can be specified in a separate file (default .env) or as a map of values.
     */
    fun secrets(action: Action<GithubActionInput>) = action.execute(secrets)

    /**
     * Additional args to pass to the `act` command.
     */
    @get:Input
    @get:Optional
    val additionalArgs: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

    @TaskAction
    fun exec() {
        val runner = ActRunner()
        runner.workflows(workflows.get())
            .envFile(env.file.orNull)
            .envValues(env.values.get())
            .secretsFile(secrets.file.orNull)
            .secretValues(secrets.values.get())
            .additionalArgs(additionalArgs.get())
            .timeout(timeout.orNull)
        runner.exec()
    }
}
