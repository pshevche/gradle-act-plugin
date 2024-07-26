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
import io.kotest.matchers.string.shouldContain
import org.opentest4j.reporting.events.core.Result.Status.SKIPPED
import org.opentest4j.reporting.events.core.Result.Status.SUCCESSFUL

class ActTestEventFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    beforeTest {
        project.addWorkflow("print_issue_or_pr_title")
    }

    "uses the first event type lexicographically from workflow if no event type is set" {
        project.addSpec(
            "default_event.act.yml",
            """
            name: picks first event type if unspecified
            workflow: print_issue_or_pr_title.yml
            """.trimIndent(),
        )

        project.test()

        assertEvents(project.xmlReport()) {
            spec(name = "picks first event type if unspecified", result = SUCCESSFUL) {
                job(name = "print_issue_title", result = SUCCESSFUL)
                job(name = "print_pr_title", result = SKIPPED)
            }
        }
    }

    "allows configuring the event type to trigger the workflow with" {
        project.addSpec(
            "custom_event.act.yml",
            """
            name: picks provided event type
            workflow: print_issue_or_pr_title.yml
            event:
                type: pull_request
            """.trimIndent(),
        )

        project.test()

        assertEvents(project.xmlReport()) {
            spec(name = "picks provided event type", result = SUCCESSFUL) {
                job(name = "print_issue_title", result = SKIPPED)
                job(name = "print_pr_title", result = SUCCESSFUL)
            }
        }
    }

    "allows providing the payload for the default event type" {
        project.addSpecResource("events/issue_payload.json")
        project.addSpec(
            "default_event_custom_payload.act.yml",
            """
            name: uses provided payload for default event
            workflow: print_issue_or_pr_title.yml
            event:
                payload: events/issue_payload.json
            """.trimIndent(),
        )

        val result = project.test("-Dact.forwardOutput=true")

        result.output shouldContain "Issue Title: Example issue payload"
        assertEvents(project.xmlReport()) {
            spec(name = "uses provided payload for default event", result = SUCCESSFUL) {
                job(name = "print_issue_title", result = SUCCESSFUL)
                job(name = "print_pr_title", result = SKIPPED)
            }
        }
    }

    "allows configuring both the event type and its payload" {
        project.addSpecResource("events/pull_request_payload.json")
        project.addSpec(
            "custom_event_custom_payload.act.yml",
            """
            name: uses provided event type and payload
            workflow: print_issue_or_pr_title.yml
            event:
                type: pull_request
                payload: events/pull_request_payload.json
            """.trimIndent(),
        )

        val result = project.test("-Dact.forwardOutput=true")

        result.output shouldContain "PR Title: Example PR payload"
        assertEvents(project.xmlReport()) {
            spec(name = "uses provided event type and payload", result = SUCCESSFUL) {
                job(name = "print_issue_title", result = SKIPPED)
                job(name = "print_pr_title", result = SUCCESSFUL)
            }
        }
    }
})
