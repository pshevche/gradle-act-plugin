package io.github.pshevche.act

import org.gradle.api.Plugin
import org.gradle.api.Project

class ActPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("greeting") { task ->
            task.doLast {
                println("Hello from plugin 'io.github.pshevche.act'")
            }
        }
    }
}
