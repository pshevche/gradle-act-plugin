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

package io.github.pshevche.act.internal.reporting

import io.github.pshevche.act.fixtures.assertEventsPayload
import io.github.pshevche.act.internal.TestDescriptor.JobDescriptor
import io.github.pshevche.act.internal.TestDescriptor.SpecDescriptor
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempfile
import org.opentest4j.reporting.events.core.Result.Status.FAILED
import org.opentest4j.reporting.events.core.Result.Status.SKIPPED
import org.opentest4j.reporting.events.core.Result.Status.SUCCESSFUL
import java.io.File

class XmlActTestReporterTest : FreeSpec({

    fun writeReport(action: XmlActTestReporter.() -> Unit): File {
        val reportFile = tempfile()
        XmlActTestReporter(reportFile.toPath()).use {
            it.action()
        }
        return reportFile
    }

    "reports a spec with no jobs" {
        val spec = SpecDescriptor("spec")
        val report =
            writeReport {
                reportTestExecutionStarted()
                reportSpecStarted(spec)
                reportSpecFinishedSuccessfully(spec)
                reportTestExecutionFinished()
            }

        assertEventsPayload(report) {
            spec(1, "spec", SUCCESSFUL)
        }
    }

    "reports a spec with successful jobs" {
        val spec1 = SpecDescriptor("spec1")
        val job1 = JobDescriptor(spec1, "job1")
        val spec2 = SpecDescriptor("spec2")
        val job2 = JobDescriptor(spec2, "job2")

        val report =
            writeReport {
                reportTestExecutionStarted()
                reportSpecStarted(spec1)
                reportJobStarted(job1)
                reportJobFinishedOrSkipped(job1, "job1 output")
                reportSpecFinishedSuccessfully(spec1)

                reportSpecStarted(spec2)
                reportJobStarted(job2)
                reportSuccessfulJobStep(job2)
                reportJobFinishedOrSkipped(job2, "job2 output")
                reportSpecFinishedSuccessfully(spec2)
                reportTestExecutionFinished()
            }

        assertEventsPayload(report) {
            spec(1, "spec1", SUCCESSFUL) {
                job(2, "job1", SKIPPED, "job1 output")
            }
            spec(3, "spec2", SUCCESSFUL) {
                job(4, "job2", SUCCESSFUL, "job2 output")
            }
        }
    }

    "reports a spec with failing jobs" {
        val spec = SpecDescriptor("spec")
        val job1 = JobDescriptor(spec, "job1")
        val job2 = JobDescriptor(spec, "job2")
        val report =
            writeReport {
                reportTestExecutionStarted()
                reportSpecStarted(spec)
                reportJobStarted(job1)
                reportJobFinishedWithFailure(job1, "job1 goes boom!")

                reportJobStarted(job2)
                reportJobFinishedWithFailure(job2, "job2 goes boom, too!")
                reportSpecFinishedWithFailure(spec)
                reportTestExecutionFinished()
            }

        assertEventsPayload(report) {
            spec(1, "spec", FAILED) {
                job(2, "job1", FAILED, "job1 goes boom!")
                job(3, "job2", FAILED, "job2 goes boom, too!")
            }
        }
    }
})
