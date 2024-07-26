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

import org.opentest4j.reporting.events.core.Result.Status
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.events.XMLEvent

fun parseSpecs(
    report: File,
    vararg options: ParseOption,
): List<ActTestReportSpec> {
    val specs = SpecsParser()
    val reader = createXmlReader(report)
    val eventStack = Stack<XMLEvent>()
    while (reader.hasNext()) {
        val event = reader.nextEvent()
        when {
            isContainerStarted(event) -> specs.onSpecOrJobStarted(event, options)
            isContainerResult(event) -> specs.onSpecOrJobResult(event, eventStack.peek())
            isResultReason(event) -> specs.onJobResultReason(reader.elementText, eventStack.peekTwice())
            isContainerFinished(event) -> specs.onSpecOrJobFinished(event)
        }
        if (event.isStartElement) {
            eventStack.push(event)
        } else if (event.isEndElement) {
            eventStack.pop()
        }
    }

    return specs.toSpecs()
}

enum class ParseOption {
    INCLUDE_TEST_ID,
    INCLUDE_TEST_OUTPUT,
}

private fun isContainerFinished(event: XMLEvent) =
    event.isStartElement && event.asStartElement().name.localPart == "finished"

private fun isResultReason(event: XMLEvent) = event.isStartElement && event.asStartElement().name.localPart == "reason"

private fun isContainerResult(event: XMLEvent) =
    event.isStartElement && event.asStartElement().name.localPart == "result"

private fun isContainerStarted(event: XMLEvent) =
    event.isStartElement && event.asStartElement().name.localPart == "started"

private fun createXmlReader(report: File): XMLEventReader =
    XMLInputFactory.newInstance().createXMLEventReader(FileInputStream(report))

private fun getAttribute(
    event: XMLEvent,
    name: String,
) = event.asStartElement().getAttributeByName(QName(name)).value

private fun <E> Stack<E>.peekTwice(): E =
    this.let {
        val top = this.pop()
        val topParent = this.peek()
        this.push(top)
        return topParent
    }

private class SpecsParser {
    private val specParsers: MutableList<SpecParser> = mutableListOf()

    fun toSpecs(): List<ActTestReportSpec> = specParsers.map { it.toSpec() }

    fun onSpecOrJobStarted(
        event: XMLEvent,
        options: Array<out ParseOption>,
    ) {
        val containerId = getAttribute(event, "id").toLong()
        val containerName = getAttribute(event, "name")
        if (hasInProgressSpec()) {
            currentSpec().onJobStarted(containerId, containerName)
        } else {
            specParsers.add(SpecParser(containerId, containerName, options))
        }
    }

    private fun hasInProgressSpec() = specParsers.isNotEmpty() && currentSpec().isRunning()

    fun onSpecOrJobResult(
        event: XMLEvent,
        containerFinishedEvent: XMLEvent,
    ) {
        val status = Status.valueOf(getAttribute(event, "status"))
        val containerId = getAttribute(containerFinishedEvent, "id").toLong()
        currentSpec().onSpecOrJobResult(containerId, status)
    }

    fun onJobResultReason(
        output: String,
        containerFinishedEvent: XMLEvent,
    ) {
        currentSpec().onJobResultReason(getAttribute(containerFinishedEvent, "id").toLong(), output)
    }

    fun onSpecOrJobFinished(event: XMLEvent) {
        val jobId = getAttribute(event, "id").toLong()
        currentSpec().finishJob(jobId)
    }

    private fun currentSpec() = specParsers.last()
}

private class SpecParser(private val id: Long, private val name: String, private val options: Array<out ParseOption>) {
    private var jobParsers: MutableList<JobParser> = mutableListOf()
    private var status: Status? = null
    private var runningJobs: MutableMap<Long, JobParser> = mutableMapOf()

    fun toSpec(): ActTestReportSpec =
        ActTestReportSpec(
            getId(),
            name,
            status!!,
            jobParsers.map { it.toJob() }.toSet(),
        )

    private fun getId() =
        if (options.contains(ParseOption.INCLUDE_TEST_ID)) {
            id
        } else {
            null
        }

    fun onJobStarted(
        jobId: Long,
        jobName: String,
    ) {
        runningJobs.putIfAbsent(jobId, JobParser(jobId, jobName, this.name, options))
    }

    fun onSpecOrJobResult(
        containerId: Long,
        status: Status,
    ) {
        val maybeJob = runningJobs.get(containerId)
        if (maybeJob == null) {
            this.status = status
        } else {
            maybeJob.onResult(status)
        }
    }

    fun onJobResultReason(
        jobId: Long,
        output: String,
    ) {
        runningJobs.getValue(jobId).onResultReason(output)
    }

    fun finishJob(jobId: Long) {
        runningJobs.get(jobId)?.let {
            jobParsers.add(it)
        }
    }

    fun isRunning() = status == null
}

private class JobParser(
    private val id: Long?,
    private val name: String,
    private val specName: String,
    private val options: Array<out ParseOption>,
) {
    private var status: Status? = null
    private var output: String? = null

    fun onResult(status: Status) {
        this.status = status
    }

    fun onResultReason(output: String) {
        this.output = output
    }

    fun toJob(): ActTestReportJob = ActTestReportJob(getId(), name, specName, status!!, getOutput())

    private fun getOutput() =
        if (options.contains(ParseOption.INCLUDE_TEST_OUTPUT)) {
            output!!
        } else {
            null
        }

    private fun getId() =
        if (options.contains(ParseOption.INCLUDE_TEST_ID)) {
            id
        } else {
            null
        }
}
