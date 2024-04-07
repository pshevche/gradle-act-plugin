package io.github.pshevche.act.internal

import java.io.File
import java.time.Duration

abstract class ActConfigBuilder {

    var workflows: File? = null
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

    fun workflows(workflows: File): ActConfigBuilder {
        this.workflows = workflows
        return this
    }

    fun job(job: String?): ActConfigBuilder {
        this.job = job
        return this
    }

    fun additionalArgs(vararg args: String): ActConfigBuilder {
        return additionalArgs(args.toList())
    }

    fun additionalArgs(args: List<String>): ActConfigBuilder {
        this.additionalArgs = args
        return this
    }

    fun timeout(timeout: Duration?): ActConfigBuilder {
        this.timeout = timeout
        return this
    }

    fun envFile(file: File?): ActConfigBuilder {
        this.envFile = file
        return this
    }

    fun envValues(values: Map<String, String>): ActConfigBuilder {
        this.envValues = values
        return this
    }

    fun envValues(vararg values: Pair<String, String>): ActConfigBuilder {
        this.envValues = mapOf(*values)
        return this
    }

    fun secretsFile(file: File?): ActConfigBuilder {
        this.secretsFile = file
        return this
    }

    fun secretValues(values: Map<String, String>): ActConfigBuilder {
        this.secretValues = values
        return this
    }

    fun secretValues(vararg values: Pair<String, String>): ActConfigBuilder {
        this.secretValues = mapOf(*values)
        return this
    }

    fun variablesFile(file: File?): ActConfigBuilder {
        this.variablesFile = file
        return this
    }

    fun variableValues(values: Map<String, String>): ActConfigBuilder {
        this.variableValues = values
        return this
    }

    fun variableValues(vararg values: Pair<String, String>): ActConfigBuilder {
        this.variableValues = mapOf(*values)
        return this
    }

    fun inputsFile(file: File?): ActConfigBuilder {
        this.inputsFile = file
        return this
    }

    fun inputValues(values: Map<String, String>): ActConfigBuilder {
        this.inputValues = values
        return this
    }

    fun inputValues(vararg values: Pair<String, String>): ActConfigBuilder {
        this.inputValues = mapOf(*values)
        return this
    }

    fun eventType(type: String?): ActConfigBuilder {
        this.eventType = type
        return this
    }

    fun eventPayload(eventPayload: File?): ActConfigBuilder {
        this.eventPayload = eventPayload
        return this
    }
}
