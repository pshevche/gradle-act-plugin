package io.github.pshevche.act;

import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.api.Project;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A simple unit test for the 'io.github.pshevche.greeting' plugin.
 */
class ActPluginTest {
    @Test void pluginRegistersATask() {
        // Create a test project and apply the plugin
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("io.github.pshevche.greeting");

        // Verify the result
        assertNotNull(project.getTasks().findByName("greeting"));
    }
}
