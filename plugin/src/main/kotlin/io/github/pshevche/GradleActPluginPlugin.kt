package io.github.pshevche

import org.gradle.api.Plugin
import org.gradle.api.Project

class GradleActPluginPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("greeting") { task ->
            task.doLast {
                println("Hello from plugin 'io.github.pshevche.greeting'")
            }
        }
    }
}
