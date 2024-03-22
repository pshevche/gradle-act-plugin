package io.github.pshevche.act

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class ActExec : DefaultTask() {

    @TaskAction
    fun exec() {
        logger.lifecycle("Hello, World!")
    }
}
