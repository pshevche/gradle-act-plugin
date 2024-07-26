/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.pshevche.act;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Gradle plugin for validating GitHub actions using <a href="https://github.com/nektos/act">nektos/act</a>.
 */
@SuppressWarnings("unused")
public class ActPlugin implements Plugin<Project> {

    /**
     * Plugin application will register a default task {@code actTest} of type {@link ActTest},
     * which will execute all specification files defined in {@link ActExtension#getSpecsRoot()}.
     * <p>
     * The configuration of the default task should be done using the registered project extension of type {@link ActExtension}.
     */
    public void apply(Project project) {
        var actExtension = project.getExtensions().create("act", ActExtension.class);
        project.getTasks().register("actTest", ActTest.class, task -> {
            task.setGroup("Verification");
            task.setDescription("Executes GitHub workflows using 'nektos/act' according to the provided specification files");
            task.getWorkflowsRoot().set(actExtension.getWorkflowsRoot());
            task.getSpecsRoot().set(actExtension.getSpecsRoot());
        });
    }
}
