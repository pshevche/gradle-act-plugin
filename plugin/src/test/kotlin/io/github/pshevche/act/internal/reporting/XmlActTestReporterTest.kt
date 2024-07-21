package io.github.pshevche.act.internal.reporting

import io.github.pshevche.act.fixtures.assertEventsPayload
import io.github.pshevche.act.internal.TestDescriptor.JobDescriptor
import io.github.pshevche.act.internal.TestDescriptor.SpecDescriptor
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempfile
import org.opentest4j.reporting.events.core.Result.Status.FAILED
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
        val report = writeReport {
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

        val report = writeReport {
            reportTestExecutionStarted()
            reportSpecStarted(spec1)
            reportJobStarted(job1)
            reportJobFinishedSuccessfully(job1, "job1 output")
            reportSpecFinishedSuccessfully(spec1)

            reportSpecStarted(spec2)
            reportJobStarted(job2)
            reportJobFinishedSuccessfully(job2, "job2 output")
            reportSpecFinishedSuccessfully(spec2)
            reportTestExecutionFinished()
        }

        assertEventsPayload(report) {
            spec(1, "spec1", SUCCESSFUL) {
                job(2, "job1", SUCCESSFUL, "job1 output")
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
        val report = writeReport {
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
