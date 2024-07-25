package io.github.pshevche.act.fixtures

import io.kotest.matchers.equals.shouldBeEqual
import org.opentest4j.reporting.events.core.Result.Status
import java.io.File

fun assertEventsPayload(
    report: File,
    specs: ActTestReportSpecsBuilder.() -> Unit,
) = parseSpecs(
    report,
    ParseOption.INCLUDE_TEST_ID,
    ParseOption.INCLUDE_TEST_OUTPUT,
) shouldBeEqual ActTestReportSpecsBuilder().apply(specs).build()

fun assertEventsWithOutput(
    report: File,
    specs: ActTestReportSpecsBuilder.() -> Unit,
) = parseSpecs(
    report,
    ParseOption.INCLUDE_TEST_OUTPUT,
) shouldBeEqual ActTestReportSpecsBuilder().apply(specs).build()

fun assertEvents(
    report: File,
    specs: ActTestReportSpecsBuilder.() -> Unit,
) = parseSpecs(report) shouldBeEqual ActTestReportSpecsBuilder().apply(specs).build()

class ActTestReportSpecsBuilder {
    private val specs = mutableListOf<ActTestReportSpec>()

    fun spec(
        id: Long? = null,
        name: String,
        result: Status,
        jobs: ActTestReportJobsBuilder.() -> Unit = {},
    ) = specs.add(
        ActTestReportSpec(id, name, result, ActTestReportJobsBuilder(name).apply(jobs).build()),
    )

    fun build(): List<ActTestReportSpec> = specs
}

class ActTestReportJobsBuilder(private val specName: String) {
    private val jobs = mutableSetOf<ActTestReportJob>()

    fun job(
        id: Long? = null,
        name: String,
        result: Status,
        output: String? = null,
    ) = jobs.add(ActTestReportJob(id, name, specName, result, output))

    fun build(): Set<ActTestReportJob> = jobs
}

data class ActTestReportSpec(val id: Long?, val name: String, val result: Status, val jobs: Set<ActTestReportJob>)

data class ActTestReportJob(
    val id: Long?,
    val name: String,
    val parent: String,
    val result: Status,
    val output: String?,
)
