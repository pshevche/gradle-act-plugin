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
import org.opentest4j.reporting.events.core.Result.Status.SUCCESSFUL
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

class ActTestCacheServerResourceFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    beforeTest {
        project.addWorkflow("save_file_in_cache")
    }

    "persists cache entries in configured directory" {
        val cacheServerStorage = project.buildFile.toPath().parent.resolve("cache")
        project.addSpec(
            "configured_cache_server.act.yml",
            """
            name: configured cache server
            workflow: save_file_in_cache.yml
            resources:
                cacheServer:
                    enabled: true
                    storage: ${cacheServerStorage.absolutePathString()}
            """.trimIndent(),
        )

        project.test()

        assertEvents(project.xmlReport()) {
            spec(name = "configured cache server", result = SUCCESSFUL) {
                job(name = "store_file_in_cache", result = SUCCESSFUL)
            }
        }
        cacheServerStorage.resolve("cache/01/1").exists() shouldBeEqual true
    }

    "supports advanced cache configuration" {
        val cacheServerStorage = project.buildFile.toPath().parent.resolve("cache")
        // custom configuration is invalid and can sometimes cause act to hang
        project.buildFile.appendText(
            """
            tasks.actTest {
                timeout = java.time.Duration.ofSeconds(10)
            }
            """.trimIndent(),
        )
        project.addSpec(
            "configured_cache_server.act.yml",
            """
            name: configured cache server
            workflow: save_file_in_cache.yml
            resources:
                cacheServer:
                    enabled: true
                    storage: ${cacheServerStorage.absolutePathString()}
                    host: 192.168.0.55
                    port: 34568
            """.trimIndent(),
        )

        val result = project.testAndFail("-Dact.forwardOutput=true")
        result.output shouldContain "Resource Url: http://192.168.0.55:34568"
    }
})
