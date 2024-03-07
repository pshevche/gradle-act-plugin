package io.github.pshevche;

import org.gradle.api.Project;
import org.gradle.api.Plugin;

/**
 * A simple 'hello world' plugin.
 */
public class GradleActPluginPlugin implements Plugin<Project> {
    public void apply(Project project) {
        // Register a task
        project.getTasks().register("greeting", task -> {
            task.doLast(s -> System.out.println("Hello from plugin 'io.github.pshevche.greeting'"));
        });
    }
}
