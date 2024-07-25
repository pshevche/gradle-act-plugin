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
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.string.shouldContain
import org.opentest4j.reporting.events.core.Result.Status.FAILED
import org.opentest4j.reporting.events.core.Result.Status.SUCCESSFUL
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

class ActTestArtifactServerResourceFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    beforeTest {
        project.addWorkflow("save_file_in_artifact_server")
    }

    "artifact server is disabled by default" {
        project.addSpec(
            "default_artifact_server.act.yml",
            """
            name: default artifact server
            workflow: save_file_in_artifact_server.yml
            env:
                values:
                    # required to execute upload-artifact action
                    ACTIONS_RUNTIME_TOKEN: irrelevant
            """.trimIndent(),
        )

        project.testAndFail()

        assertEvents(project.xmlReport()) {
            spec(name = "default artifact server", result = FAILED) {
                job(name = "store_file_in_artifact_server", result = FAILED)
            }
        }
    }

    "persists workflow artifacts in configured directory" {
        val artifactServerStorage = project.buildFile.toPath().parent.resolve("artifacts")
        project.addSpec(
            "configured_artifact_server.act.yml",
            """
            name: configured artifact server
            workflow: save_file_in_artifact_server.yml
            resources:
                artifactServer:
                    enabled: true
                    storage: ${artifactServerStorage.absolutePathString()}
            env:
                values:
                    # required to execute upload-artifact action
                    ACTIONS_RUNTIME_TOKEN: irrelevant
            """.trimIndent(),
        )

        project.test()

        assertEvents(project.xmlReport()) {
            spec(name = "configured artifact server", result = SUCCESSFUL) {
                job(name = "store_file_in_artifact_server", result = SUCCESSFUL)
            }
        }

        artifactServerStorage.resolve("1/greeting/greeting.zip").exists() shouldBeEqual true
    }

    "artifact server can be explicitly disabled" {
        val artifactServerStorage = project.buildFile.toPath().parent.resolve("artifacts")
        project.addSpec(
            "disabled_artifact_server.act.yml",
            """
            name: disabled artifact server
            workflow: save_file_in_artifact_server.yml
            resources:
                artifactServer:
                    enabled: false
                    storage: ${artifactServerStorage.absolutePathString()}
            env:
                values:
                    # required to execute upload-artifact action
                    ACTIONS_RUNTIME_TOKEN: irrelevant
            """.trimIndent(),
        )

        project.testAndFail()

        assertEvents(project.xmlReport()) {
            spec(name = "disabled artifact server", result = FAILED) {
                job(name = "store_file_in_artifact_server", result = FAILED)
            }
        }

        artifactServerStorage.resolve("1/greeting/greeting.zip").exists() shouldBeEqual false
    }

    "supports advanced server configuration" {
        val artifactServerStorage = project.buildFile.toPath().parent.resolve("artifacts")
        project.addSpec(
            "configured_artifact_server.act.yml",
            """
            name: configured artifact server
            workflow: save_file_in_artifact_server.yml
            resources:
                artifactServer:
                    enabled: true
                    storage: ${artifactServerStorage.absolutePathString()}
                    host: 192.168.0.55
                    port: 12345
            env:
                values:
                    # required to execute upload-artifact action
                    ACTIONS_RUNTIME_TOKEN: irrelevant
            """.trimIndent(),
        )

        // I can't figure out how to override these settings to something that will work
        val result = project.testAndFail("-Dact.forwardOutput=true")
        result.output shouldContain "Start server on http://192.168.0.55:12345"
    }
})
