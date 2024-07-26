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

package io.github.pshevche.act

import io.github.pshevche.act.fixtures.GradleProject
import io.github.pshevche.act.fixtures.assertEvents
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.engine.spec.tempdir
import org.gradle.util.GradleVersion
import org.opentest4j.reporting.events.core.Result

class ActTestGradleCompatibilityFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    fun gradleVersionsUnderTest() = listOf(
        "7.4",
        "7.4.2",
        "7.5.1",
        "7.6.4",
        "8.0.2",
        "8.1.1",
        "8.2.1",
        "8.3",
        "8.4",
        "8.5",
        "8.6",
        "8.7",
        "8.8",
        "8.9"
    ).map { GradleVersion.version(it) }

    withData(
        nameFn = { "is compatible with Gradle ${it.version}" },
        gradleVersionsUnderTest()
    ) { gradleVersion ->
        project.addWorkflow("always_passing_workflow")
        project.addSpec(
            "gradle_compatibility.act.yml",
            """
                name: Gradle compatibility spec
                workflow: always_passing_workflow.yml
            """.trimIndent()
        )

        project.test(gradleVersion, "actTest", "--stacktrace")

        assertEvents(project.xmlReport()) {
            spec(name = "Gradle compatibility spec", result = Result.Status.SUCCESSFUL) {
                job(name = "successful_job", result = Result.Status.SUCCESSFUL)
            }
        }
    }
})
