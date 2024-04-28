package io.github.pshevche.act.exec

import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldHaveSuccessfulJobs
import io.github.pshevche.act.fixtures.BuildResultAssertions.shouldSucceed
import io.github.pshevche.act.fixtures.GradleProject
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldContain

class CustomEventActExecFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    "uses first event type by default" {
        project.addWorkflows(".github/workflows/", "print_event_and_payload")
        project.execTask("act") {
            workflow(".github/workflows/print_event_and_payload.yml")
        }

        val result = project.build("act")

        result.shouldSucceed(":act")
        result.shouldHaveSuccessfulJobs("print_event_and_payload")
        result.output shouldContain "event = issue_comment"
    }

    "allows overriding the event type to trigger the workflow with" {
        project.addWorkflows(".github/workflows/", "print_event_and_payload")
        project.execTask("act") {
            workflow(".github/workflows/print_event_and_payload.yml")
            eventType("pull_request")
        }

        val result = project.build("act")

        result.shouldSucceed(":act")
        result.shouldHaveSuccessfulJobs("print_event_and_payload")
        result.output shouldContain "event = pull_request"
    }

    "allows specifying the event payload via a file" {
        project.addWorkflows(".github/workflows/", "print_event_and_payload")
        val eventPayload = project.workspaceDir.resolve("pr_payload.json").apply {
            createNewFile()
            writeText("{ \"author\": \"pshevche\" }")
        }
        project.execTask("act") {
            workflow(".github/workflows/print_event_and_payload.yml")
            eventType("pull_request")
            eventPayload(eventPayload)
        }

        val result = project.build("act")

        result.shouldSucceed(":act")
        result.shouldHaveSuccessfulJobs("print_event_and_payload")
        result.output shouldContain "event = pull_request"
        result.output shouldContain "author: pshevche"
    }
})
