package io.github.pshevche.act.internal.reporting

import io.github.pshevche.act.internal.TestDescriptor.JobDescriptor
import io.github.pshevche.act.internal.TestDescriptor.SpecDescriptor
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.equals.shouldBeEqual
import java.io.File

class HtmlActTestReporterTest : FreeSpec({

    fun writeReport(action: HtmlActTestReporter.() -> Unit): File {
        val reportFile = tempfile()
        HtmlActTestReporter(reportFile.toPath()).use {
            it.action()
        }
        return reportFile
    }

    "smoke test" {
        val spec1 = SpecDescriptor("spec1")
        val job1 = JobDescriptor(spec1, "job1")
        val spec2 = SpecDescriptor("spec2")
        val job2 = JobDescriptor(spec2, "job2")
        val job3 = JobDescriptor(spec2, "job3")

        val report =
            writeReport {
                reportTestExecutionStarted()
                reportSpecStarted(spec1)
                reportJobStarted(job1)
                reportJobFinishedOrSkipped(job1, "output1")
                reportSpecFinishedSuccessfully(spec1)

                reportSpecStarted(spec2)
                reportJobStarted(job2)
                reportJobFinishedWithFailure(job2, "output2")
                reportJobStarted(job3)
                reportSuccessfulJobStep(job3)
                reportJobFinishedOrSkipped(job3, "output3")
                reportSpecFinishedWithFailure(spec2)
                reportTestExecutionFinished()
            }

        val reportHTML = report.readText()
        // job1 is skipped
        "card-header skipped".toRegex().findAll(reportHTML).count() shouldBeEqual 1
        // spec1 and job3 passed
        "card-header successful".toRegex().findAll(reportHTML).count() shouldBeEqual 2
        // spec2 and job2 failed
        "card-header failed".toRegex().findAll(reportHTML).count() shouldBeEqual 2
    }
})
