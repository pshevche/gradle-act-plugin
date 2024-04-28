package io.github.pshevche.act.exec

import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldFail
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldHaveFailedJobs
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldHaveSuccessfulJobs
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldSucceed
import io.github.pshevche.act.fixtures.GradleProject
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.string.shouldContain

class ArtifactServerActExecFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    fun artifactContent(artifactServerDir: String = "build/act/artifact", artifactPath: String) =
        project.workspaceDir
            .resolve("$artifactServerDir/1")
            .resolve(artifactPath)
            .readText()
            .trim()

    "artifact server can be configured with default settings" {
        project.addWorkflows("workflows", "workflow_with_artifacts")
        project.execTask("actArtifacts") {
            workflow("workflows/workflow_with_artifacts.yml")
            artifactServer {
                enabled(true)
            }
            envValues("ACTIONS_RUNTIME_TOKEN" to "foo")
        }

        val result = project.build("actArtifacts")

        result.shouldSucceed(":actArtifacts")
        result.shouldHaveSuccessfulJobs("upload_artifact_with_greetings")

        artifactContent(artifactPath = "greetings_artifact/english.txt") shouldBeEqual "Hello, World!"
        artifactContent(artifactPath = "greetings_artifact/deutsch.txt") shouldBeEqual "Hallo, Welt!"
    }

    "does not store artifacts if server is not enabled" {
        project.addWorkflows("workflows", "workflow_with_artifacts")
        project.execTask("actArtifacts") {
            workflow("workflows/workflow_with_artifacts.yml")
            envValues("ACTIONS_RUNTIME_TOKEN" to "foo")
        }

        val result = project.buildAndFail("actArtifacts")

        result.shouldFail(":actArtifacts")
        result.shouldHaveFailedJobs("upload_artifact_with_greetings")
        // indicates that the artifact server has not been started
        result.output shouldContain "Unable to get ACTIONS_RUNTIME_URL"
    }

    "artifacts location can be configured" {
        project.addWorkflows("workflows", "workflow_with_artifacts")
        project.execTask("actArtifacts") {
            workflow("workflows/workflow_with_artifacts.yml")
            artifactServer {
                enabled(true)
                path(project.workspaceDir.resolve("build/custom_artifact").toPath())
            }
            envValues("ACTIONS_RUNTIME_TOKEN" to "foo")
        }

        val result = project.build("actArtifacts")

        result.shouldSucceed(":actArtifacts")
        result.shouldHaveSuccessfulJobs("upload_artifact_with_greetings")

        artifactContent("build/custom_artifact", "greetings_artifact/english.txt") shouldBeEqual "Hello, World!"
        artifactContent("build/custom_artifact", "greetings_artifact/deutsch.txt") shouldBeEqual "Hallo, Welt!"
    }

    "artifacts server address bindings can be configured" {
        project.addWorkflows("workflows", "workflow_with_artifacts")
        project.execTask("actArtifacts") {
            workflow("workflows/workflow_with_artifacts.yml")
            artifactServer {
                enabled(true)
                port(34568)
            }
            envValues("ACTIONS_RUNTIME_TOKEN" to "foo")
        }

        val result = project.build("actArtifacts")

        result.shouldSucceed(":actArtifacts")
        result.shouldHaveSuccessfulJobs("upload_artifact_with_greetings")
        result.output shouldContain "Artifact Url: http://192.168.0.54:34568"

        artifactContent(artifactPath = "greetings_artifact/english.txt") shouldBeEqual "Hello, World!"
        artifactContent(artifactPath = "greetings_artifact/deutsch.txt") shouldBeEqual "Hallo, Welt!"
    }
})
