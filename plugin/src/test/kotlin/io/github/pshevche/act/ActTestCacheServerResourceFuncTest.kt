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
            "configured_cache_server.act.yml", """
            name: configured cache server
            workflow: save_file_in_cache.yml
            resources:
                cacheServer:
                    enabled: true
                    storage: ${cacheServerStorage.absolutePathString()}
        """.trimIndent()
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
        project.addSpec(
            "configured_cache_server.act.yml", """
            name: configured cache server
            workflow: save_file_in_cache.yml
            resources:
                cacheServer:
                    enabled: true
                    storage: ${cacheServerStorage.absolutePathString()}
                    host: 192.168.0.55
                    port: 12345
        """.trimIndent()
        )

        val result = project.testAndFail("-Dact.forwardOutput=true")
        result.output shouldContain "Resource Url: http://192.168.0.55:12345"
    }
})
