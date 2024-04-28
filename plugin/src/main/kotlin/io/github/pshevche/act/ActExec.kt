package io.github.pshevche.act

import io.github.pshevche.act.internal.ActRunner
import io.github.pshevche.act.internal.DefaultActEventSpec
import io.github.pshevche.act.internal.DefaultActInputSpec
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

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
    @get:InputFile
    val workflow: RegularFileProperty = objects.fileProperty()

    /**
     * Job ID to execute (run all jobs by default).
     */
    @get:Input
    @get:Optional
    val job: Property<String> = objects.property(String::class.java)

    /**
     * Env to make available to actions.
     * Can be specified in a separate file (default .env) or as a map of values.
     */
    @get:Nested
    @get:Optional
    val env: ActInputSpec = DefaultActInputSpec(objects)

    /**
     * Env to make available to actions.
     * Can be specified in a separate file (default .env) or as a map of values.
     */
    fun env(action: Action<ActInputSpec>) = action.execute(env)

    /**
     * Inputs to make available to actions.
     * Can be specified in a separate file (default .input) or as a map of values.
     */
    @get:Nested
    @get:Optional
    val actionInputs: ActInputSpec = DefaultActInputSpec(objects)

    /**
     * Inputs to make available to actions.
     * Can be specified in a separate file (default .input) or as a map of values.
     */
    fun actionInputs(action: Action<ActInputSpec>) = action.execute(actionInputs)

    /**
     * Secrets to make available to actions.
     * Can be specified in a separate file (default .secrets) or as a map of values.
     */
    @get:Nested
    @get:Optional
    val secrets: ActInputSpec = DefaultActInputSpec(objects)

    /**
     * Secrets to make available to actions.
     * Can be specified in a separate file (default .env) or as a map of values.
     */
    fun secrets(action: Action<ActInputSpec>) = action.execute(secrets)

    /**
     * Variables to make available to actions.
     * Can be specified in a separate file (default .vars) or as a map of values.
     */
    @get:Nested
    @get:Optional
    val variables: ActInputSpec = DefaultActInputSpec(objects)

    /**
     * Variables to make available to actions.
     * Can be specified in a separate file (default .vars) or as a map of values.
     */
    fun variables(action: Action<ActInputSpec>) = action.execute(variables)

    /**
     * Event type and payload to trigger the workflow with.
     */
    @get:Nested
    @get:Optional
    val event: ActEventSpec = DefaultActEventSpec(objects)

    /**
     * Event type and payload to trigger the workflow with.
     */
    fun event(action: Action<ActEventSpec>) = action.execute(event)

    /**
     * Additional args to pass to the `act` command.
     */
    @get:Input
    @get:Optional
    val additionalArgs: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

    @TaskAction
    fun exec() {
        val runner = ActRunner()
        runner.workflow(workflow.get().asFile)
            .job(job.orNull)
            .envFile(env.file.asFile.orNull)
            .envValues(env.values.get())
            .inputsFile(actionInputs.file.asFile.orNull)
            .inputValues(actionInputs.values.get())
            .secretsFile(secrets.file.asFile.orNull)
            .secretValues(secrets.values.get())
            .variablesFile(variables.file.asFile.orNull)
            .variableValues(variables.values.get())
            .additionalArgs(additionalArgs.get())
            .eventType(event.type.orNull)
            .eventPayload(event.payload.asFile.orNull)
            .timeout(timeout.orNull)
        runner.exec()
    }
}
