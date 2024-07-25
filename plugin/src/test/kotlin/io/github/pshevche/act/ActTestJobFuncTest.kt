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
import org.opentest4j.reporting.events.core.Result.Status.FAILED
import org.opentest4j.reporting.events.core.Result.Status.SUCCESSFUL

class ActTestJobFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    "reports failures for individual jobs" {
        project.addWorkflow("workflow_with_failing_and_passing_jobs")
        project.addSpec(
            "failing_and_passing_jobs.act.yml",
            """
            name: workflow with failing and passing jobs
            workflow: workflow_with_failing_and_passing_jobs.yml
            """.trimIndent(),
        )

        project.testAndFail()

        assertEvents(project.xmlReport()) {
            spec(name = "workflow with failing and passing jobs", result = FAILED) {
                job(name = "successful_job", result = SUCCESSFUL)
                job(name = "failing_job", result = FAILED)
            }
        }
    }

    "supports selecting single jobs to run" {
        project.addWorkflow("workflow_with_failing_and_passing_jobs")
        project.addSpec(
            "failing_job.act.yml",
            """
            name: failing job only
            workflow: workflow_with_failing_and_passing_jobs.yml
            job: failing_job
            """.trimIndent(),
        )
        project.addSpec(
            "passing_job.act.yml",
            """
            name: passing job only
            workflow: workflow_with_failing_and_passing_jobs.yml
            job: successful_job
            """.trimIndent(),
        )

        project.testAndFail()

        assertEvents(project.xmlReport()) {
            spec(name = "failing job only", result = FAILED) {
                job(name = "failing_job", result = FAILED)
            }
            spec(name = "passing job only", result = SUCCESSFUL) {
                job(name = "successful_job", result = SUCCESSFUL)
            }
        }
    }
})
