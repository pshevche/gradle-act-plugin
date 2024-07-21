package io.github.pshevche.act.internal.spec

import io.github.pshevche.act.internal.ActException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.engine.spec.tempdir
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.string.shouldContain
import java.nio.file.Path
import java.nio.file.Paths

class ActTestSpecParserTest : FreeSpec({

    val workflowsRoot = tempdir("workflows").apply {
        this.resolve("workflow.yml").createNewFile()
    }
    val specsRoot = tempdir("specs")
    val parser = ActTestSpecParser(workflowsRoot.toPath(), specsRoot.toPath())

    val specFile = tempfile("spec", ".yml")

    fun parseWithFailures(vararg expectedErrors: String) {
        val error = shouldThrow<ActException> {
            parser.parse(specFile)
        }

        expectedErrors.forEach {
            error.message shouldContain it
        }
    }

    fun actSpec(
        name: String? = null,
        workflow: Path = workflowsRoot.resolve("workflow.yml").toPath(),
        job: String? = null,
        event: ActTestSpecEvent? = null,
        env: ActTestSpecInput? = null,
        inputs: ActTestSpecInput? = null,
        secrets: ActTestSpecInput? = null,
        variables: ActTestSpecInput? = null,
        matrix: Map<String, Any> = emptyMap(),
        resources: ActTestSpecResources? = null,
        additionalArgs: List<String> = emptyList()
    ) = ActTestSpec(
        name,
        workflow,
        job,
        event,
        env,
        inputs,
        secrets,
        variables,
        matrix,
        resources,
        additionalArgs
    )

    "fails if spec name is absent" {
        specFile.writeText("workflow: workflow.yml")
        parseWithFailures("Spec file must contain a 'name' property")
    }

    "fails if workflow path is absent" {
        specFile.writeText(
            """
            name: spec without workflow
        """.trimIndent()
        )
        parseWithFailures("Spec file must have a single 'workflow' property specifying the path to the workflow file relative to the workspace root")
    }

    "fails if workflow does not exist in the workflows root" {
        specFile.writeText(
            """
            name: spec with non-existing workflow file
            workflow: non_existent.yml
        """.trimIndent()
        )
        parseWithFailures("The workflow file 'non_existent.yml' does not exist in the workflows root directory")
    }

    "handles optional parameters" {
        specFile.writeText(
            """
            name: barebone spec
            workflow: workflow.yml
        """.trimIndent()
        )
        parser.parse(specFile) shouldBeEqual actSpec("barebone spec")
    }

    "captures job ID" {
        specFile.writeText(
            """
            name: spec running a single job
            workflow: workflow.yml
            job: awesome-job
        """.trimIndent()
        )
        parser.parse(specFile) shouldBeEqual actSpec(
            name = "spec running a single job",
            job = "awesome-job"
        )
    }

    "handles event type" {
        specFile.writeText(
            """
            name: spec specifying an event type
            workflow: workflow.yml
            event: 
                type: pull_request
        """.trimIndent()
        )
        parser.parse(specFile) shouldBeEqual actSpec(
            name = "spec specifying an event type",
            event = ActTestSpecEvent("pull_request", null)
        )
    }

    "handles valid event payload file" {
        val payloadFile = specsRoot.resolve("event_payload.json").apply {
            this.createNewFile()
        }
        specFile.writeText(
            """
            name: spec providing an event payload
            workflow: workflow.yml
            event: 
                payload: event_payload.json
        """.trimIndent()
        )
        parser.parse(specFile) shouldBeEqual actSpec(
            name = "spec providing an event payload",
            event = ActTestSpecEvent(null, payloadFile.toPath())
        )
    }

    "requires event payload to be located in the specs root" {
        specFile.writeText(
            """
            name: spec specifying a non-existing payload file
            workflow: workflow.yml
            event: 
                payload: non_existent.json
        """.trimIndent()
        )
        parseWithFailures("The event payload file 'non_existent.json' does not exist in the specs root directory")
    }

    "allows specifying both the event type and payload file" {
        val payloadFile = specsRoot.resolve("event_payload.json").apply {
            this.createNewFile()
        }
        specFile.writeText(
            """
            name: spec specifying both an event type and payload
            workflow: workflow.yml
            event: 
                type: pull_request
                payload: event_payload.json
        """.trimIndent()
        )
        parser.parse(specFile) shouldBeEqual actSpec(
            name = "spec specifying both an event type and payload",
            event = ActTestSpecEvent("pull_request", payloadFile.toPath())
        )
    }

    withData(
        nameFn = { "values of workflow $it can be specified as key-value pairs" },
        "env",
        "inputs",
        "secrets",
        "variables"
    ) { inputType ->
        specFile.writeText(
            """
                name: spec with inputs
                workflow: workflow.yml
                $inputType:
                    values:
                        key1: value1
                        key2: 4
            """.trimIndent()
        )

        val expectedValues = mapOf("key1" to "value1", "key2" to 4)
        val expectedSpec = when (inputType) {
            "env" -> actSpec(name = "spec with inputs", env = ActTestSpecInput(null, expectedValues))
            "inputs" -> actSpec(name = "spec with inputs", inputs = ActTestSpecInput(null, expectedValues))
            "secrets" -> actSpec(name = "spec with inputs", secrets = ActTestSpecInput(null, expectedValues))
            "variables" -> actSpec(name = "spec with inputs", variables = ActTestSpecInput(null, expectedValues))
            else -> throw IllegalArgumentException("Unexpected workflow input type")
        }
        parser.parse(specFile) shouldBeEqual expectedSpec
    }

    withData(
        nameFn = { "values of workflow $it can be specified via a file" },
        "env",
        "inputs",
        "secrets",
        "variables"
    ) { inputType ->
        val eventFile = specsRoot.resolve("values.json").apply {
            this.createNewFile()
        }
        specFile.writeText(
            """
                name: spec with inputs
                workflow: workflow.yml
                $inputType:
                    file: values.json
            """.trimIndent()
        )

        val expectedSpec = when (inputType) {
            "env" -> actSpec(name = "spec with inputs", env = ActTestSpecInput(eventFile.toPath(), emptyMap()))
            "inputs" -> actSpec(name = "spec with inputs", inputs = ActTestSpecInput(eventFile.toPath(), emptyMap()))
            "secrets" -> actSpec(name = "spec with inputs", secrets = ActTestSpecInput(eventFile.toPath(), emptyMap()))
            "variables" -> actSpec(
                name = "spec with inputs",
                variables = ActTestSpecInput(eventFile.toPath(), emptyMap())
            )

            else -> throw IllegalArgumentException("Unexpected workflow input type")
        }
        parser.parse(specFile) shouldBeEqual expectedSpec
    }

    withData(
        nameFn = { "values of workflow $it can be specified both via a file and as key-value pairs" },
        "env",
        "inputs",
        "secrets",
        "variables"
    ) { inputType ->
        val eventFile = specsRoot.resolve("values.json").apply {
            this.createNewFile()
        }
        specFile.writeText(
            """
                name: spec with inputs
                workflow: workflow.yml
                $inputType:
                    file: values.json
                    values:
                        key1: value1
                        key2: 4
            """.trimIndent()
        )

        val expectedValues = mapOf("key1" to "value1", "key2" to 4)
        val expectedSpec = when (inputType) {
            "env" -> actSpec(name = "spec with inputs", env = ActTestSpecInput(eventFile.toPath(), expectedValues))
            "inputs" -> actSpec(
                name = "spec with inputs",
                inputs = ActTestSpecInput(eventFile.toPath(), expectedValues)
            )

            "secrets" -> actSpec(
                name = "spec with inputs",
                secrets = ActTestSpecInput(eventFile.toPath(), expectedValues)
            )

            "variables" -> actSpec(
                name = "spec with inputs",
                variables = ActTestSpecInput(eventFile.toPath(), expectedValues)
            )

            else -> throw IllegalArgumentException("Unexpected workflow input type")
        }
        parser.parse(specFile) shouldBeEqual expectedSpec
    }

    "can configure workflow resources" - {
        withData(
            nameFn = { "$it requires a storage directory" },
            "artifactServer",
            "cacheServer"
        ) { resourceType ->
            specFile.writeText(
                """
                name: spec with inputs
                workflow: workflow.yml
                resources:
                    $resourceType:
                        enabled: true
            """.trimIndent()
            )
            parseWithFailures("The directory for storing ${if (resourceType == "artifactServer") "artifact server" else "cache server"} data must be provided if resource is enabled")
        }

        withData(
            nameFn = { "$it with advanced settings" },
            "artifactServer",
            "cacheServer"
        ) { resourceType ->
            specFile.writeText(
                """
                name: spec with inputs
                workflow: workflow.yml
                resources:
                    $resourceType:
                        enabled: true
                        storage: storage/$resourceType
                        host: 192.168.0.54
                        port: 34567
            """.trimIndent()
            )

            val expectedSpec = when (resourceType) {
                "artifactServer" -> actSpec(
                    name = "spec with inputs",
                    resources = ActTestSpecResources(
                        ActTestSpecResource(
                            true,
                            Paths.get("storage/artifactServer"),
                            "192.168.0.54",
                            34567
                        ), null
                    )
                )

                "cacheServer" -> actSpec(
                    name = "spec with inputs",
                    resources = ActTestSpecResources(
                        null,
                        ActTestSpecResource(true, Paths.get("storage/cacheServer"), "192.168.0.54", 34567)
                    )
                )

                else -> throw IllegalArgumentException("Unexpected workflow input type")
            }
            parser.parse(specFile) shouldBeEqual expectedSpec
        }
    }

    "allows specifying additional act params" {
        specFile.writeText(
            """
            name: spec with additional args
            workflow: workflow.yml
            additionalArgs:
            - --json
            - --platform
            - ubuntu-18.04=nektos/act-environments-ubuntu:18.04
        """.trimIndent()
        )
        parser.parse(specFile) shouldBeEqual actSpec(
            name = "spec with additional args",
            additionalArgs = listOf(
                "--json",
                "--platform",
                "ubuntu-18.04=nektos/act-environments-ubuntu:18.04"
            )
        )
    }

    "captures all violation errors" {
        specFile.writeText(
            """
            workflow: non_existent.yml
            event: 
                payload: non_existent.json
        """.trimIndent()
        )
        parseWithFailures(
            "The workflow file 'non_existent.yml' does not exist in the workflows root directory",
            "The event payload file 'non_existent.json' does not exist in the specs root directory"
        )
    }

})
