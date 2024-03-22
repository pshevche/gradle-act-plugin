package io.github.pshevche.act.internal

import java.io.File

abstract class ActConfigBuilder {

    var workflows: File? = null
    var additionalArgs: List<String> = listOf()

    fun workflows(workflows: File): ActConfigBuilder {
        this.workflows = workflows
        return this
    }

    fun additionalArgs(vararg args: String): ActConfigBuilder {
        return additionalArgs(args.toList())
    }

    fun additionalArgs(args: List<String>): ActConfigBuilder {
        this.additionalArgs = args
        return this
    }
}
