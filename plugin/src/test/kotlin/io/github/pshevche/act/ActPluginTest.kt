package io.github.pshevche.act

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldNotBe
import org.gradle.testfixtures.ProjectBuilder

class ActPluginTest : FreeSpec({

    "plugin registers task" {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("io.github.pshevche.act")
        project.tasks.findByName("greeting") shouldNotBe null
    }
})
