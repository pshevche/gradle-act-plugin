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
