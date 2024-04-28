package io.github.pshevche.act.internal

import java.io.File
import java.nio.file.Path
import java.time.Duration

@Suppress("TooManyFunctions")
abstract class ActConfigBuilder {

    var workflow: File? = null
    var job: String? = null
    var additionalArgs: List<String> = listOf()
    var timeout: Duration? = null
    var envFile: File? = null
    var envValues: Map<String, String> = emptyMap()
    var secretsFile: File? = null
    var secretValues: Map<String, String> = emptyMap()
    var variablesFile: File? = null
    var variableValues: Map<String, String> = emptyMap()
    var inputsFile: File? = null
    var inputValues: Map<String, String> = emptyMap()
    var eventType: String? = null
    var eventPayload: File? = null
    var artifactServer: ArtifactServerBuilder = ArtifactServerBuilder()

    fun workflow(workflowPath: String) = workflow(File(workflowPath))

    fun workflow(workflow: File) = apply {
        this.workflow = workflow
    }

    fun job(job: String?) = apply {
        this.job = job
    }

    fun additionalArgs(vararg args: String) = additionalArgs(args.toList())

    fun additionalArgs(args: List<String>) = apply {
        this.additionalArgs = args
    }

    fun timeout(timeout: Duration?) = apply {
        this.timeout = timeout
    }

    fun envFile(file: File?) = apply {
        this.envFile = file
    }

    fun envValues(values: Map<String, String>) = apply {
        this.envValues = values
    }

    fun envValues(vararg values: Pair<String, String>) = apply {
        this.envValues = mapOf(*values)
    }

    fun secretsFile(file: File?) = apply {
        this.secretsFile = file
    }

    fun secretValues(values: Map<String, String>) = apply {
        this.secretValues = values
    }

    fun secretValues(vararg values: Pair<String, String>) = apply {
        this.secretValues = mapOf(*values)
    }

    fun variablesFile(file: File?) = apply {
        this.variablesFile = file
    }

    fun variableValues(values: Map<String, String>) = apply {
        this.variableValues = values
    }

    fun variableValues(vararg values: Pair<String, String>) = apply {
        this.variableValues = mapOf(*values)
    }

    fun inputsFile(file: File?) = apply {
        this.inputsFile = file
    }

    fun inputValues(values: Map<String, String>) = apply {
        this.inputValues = values
    }

    fun inputValues(vararg values: Pair<String, String>) = apply {
        this.inputValues = mapOf(*values)
    }

    fun eventType(type: String?) = apply {
        this.eventType = type
    }

    fun eventPayload(eventPayload: File?) = apply {
        this.eventPayload = eventPayload
    }

    fun artifactServer(config: ArtifactServerBuilder.() -> Unit) = apply {
        artifactServer.apply(config)
    }
}

class ArtifactServerBuilder {
    var enabled: Boolean = false
    var host: String? = null
    var port: Int? = null
    var path: Path? = null

    fun enabled(enabled: Boolean) = apply {
        this.enabled = enabled
    }

    fun host(host: String) = apply {
        this.host = host
    }

    fun port(port: Int) = apply {
        this.port = port
    }

    fun path(path: Path) = apply {
        this.path = path
    }
}
