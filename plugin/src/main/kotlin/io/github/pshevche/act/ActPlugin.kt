package io.github.pshevche.act

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle plugin for running and validating GitHub actions using [nektos/act](https://github.com/nektos/act).
 */
class ActPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // do nothing, as the plugin only exposes custom task types
    }
}
