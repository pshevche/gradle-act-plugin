package io.github.pshevche.act.internal

import java.nio.file.Path

abstract class ActConfigBuilder {

    var workflows: Path? = null

    fun workflows(workflows: Path): ActConfigBuilder {
        this.workflows = workflows
        return this
    }
}
