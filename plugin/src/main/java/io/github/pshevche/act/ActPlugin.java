package io.github.pshevche.act;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Gradle plugin for validating GitHub actions using <a href="https://github.com/nektos/act">nektos/act</a>.
 */
@SuppressWarnings("unused")
public class ActPlugin implements Plugin<Project> {
    public void apply(Project project) {
        // Register a task
        project.getTasks().register("greeting", task -> {
            task.doLast(s -> System.out.println("Hello from plugin 'io.github.pshevche.greeting'"));
        });
    }
}
