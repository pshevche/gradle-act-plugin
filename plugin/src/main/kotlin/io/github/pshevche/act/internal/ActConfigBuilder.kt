package io.github.pshevche.act.internal

import java.io.File

abstract class ActConfigBuilder {

    var workflows: File? = null

    fun workflows(workflows: File): ActConfigBuilder {
        this.workflows = workflows
        return this
    }
}
