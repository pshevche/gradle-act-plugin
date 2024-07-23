package io.github.pshevche.act;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Gradle plugin for validating GitHub actions using <a href="https://github.com/nektos/act">nektos/act</a>.
 */
@SuppressWarnings("unused")
public class ActPlugin implements Plugin<Project> {
    public void apply(Project project) {
        var actExtension = project.getExtensions().create("act", ActExtension.class);
        project.getTasks().register("actTest", ActTest.class, task -> {
            task.getWorkflowsRoot().set(actExtension.getWorkflowsRoot());
            task.getSpecsRoot().set(actExtension.getSpecsRoot());
        });
    }
}
