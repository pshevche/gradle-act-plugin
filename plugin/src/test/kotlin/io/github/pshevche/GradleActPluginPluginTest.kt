package io.github.pshevche

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

class GradleActPluginPluginTest {
    @Test fun `plugin registers task`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("io.github.pshevche.greeting")
        assertNotNull(project.tasks.findByName("greeting"))
    }
}
