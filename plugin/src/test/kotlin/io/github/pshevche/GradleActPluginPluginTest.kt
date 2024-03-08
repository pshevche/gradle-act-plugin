package io.github.pshevche

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldNotBe
import org.gradle.testfixtures.ProjectBuilder

class GradleActPluginPluginTest : FreeSpec({

    "plugin registers task" {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("io.github.pshevche.greeting")
        project.tasks.findByName("greeting") shouldNotBe null
    }
})
