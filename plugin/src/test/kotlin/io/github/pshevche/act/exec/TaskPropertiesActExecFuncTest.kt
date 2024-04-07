package io.github.pshevche.act.exec

import io.github.pshevche.act.fixtures.BuildResultAssertions.outputShouldContainEither
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldFail
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldHaveFailedJobs
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldHaveSuccessfulJobs
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldSucceed
import io.github.pshevche.act.fixtures.GradleProject
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import java.time.Duration

class TaskPropertiesActExecFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    "failing workflow will cause the task to fail" {
        project.addWorkflows(".github/workflows/", "single_failing_job")
        project.execTask("actFailing")

        val result = project.buildAndFail("actFailing")

        result.shouldFail(":actFailing")
        result.shouldHaveFailedJobs("failing_job")
    }

    "respects the task timeout" {
        project.addWorkflows(".github/workflows/", "hello_world")
        project.execTask("actTimeout") {
            timeout(Duration.ofMillis(100))
        }

        val result = project.buildAndFail("actTimeout")

        result.shouldFail(":actTimeout")
        // Sometimes our check kicks in before Gradle cancels the task
        result.outputShouldContainEither(
            "Timeout has been exceeded",
            "Failed to complete act command within the configured timeout"
        )
        result.output shouldNotContain "Job succeeded"
    }

    "has the expected cache behavior" - {
        "is never up-to-date" {
            project.addWorkflows(".github/workflows/", "hello_world")
            project.execTask("actAll")

            var result = project.build("actAll")
            result.shouldSucceed(":actAll")
            result.shouldHaveSuccessfulJobs("print_greeting")

            result = project.build("actAll")
            result.shouldSucceed(":actAll")
            result.shouldHaveSuccessfulJobs("print_greeting")
        }

        "works with configuration cache" {
            project.addWorkflows(".github/workflows/", "hello_world")
            project.execTask("actDefault")

            var result = project.build("actDefault", "--configuration-cache")

            result.shouldSucceed(":actDefault")
            result.output shouldContain "Configuration cache entry stored"

            result = project.build("actDefault", "--configuration-cache")

            result.shouldSucceed(":actDefault")
            result.output shouldContain "Reusing configuration cache"
        }
    }
})
