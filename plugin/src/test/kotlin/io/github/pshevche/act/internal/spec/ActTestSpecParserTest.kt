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
        workflow: Path = workflowsRoot.resolve("workflow.yml").toPath(),
        job: String? = null,
        event: ActTestSpecEvent? = null,
        env: ActTestSpecInput? = null,
        inputs: ActTestSpecInput? = null,
        secrets: ActTestSpecInput? = null,
        variables: ActTestSpecInput? = null,
        matrix: Map<String, Any> = emptyMap(),
        resources: ActTestSpecResources? = null,
        additionalArgs: List<String> = emptyList(),
        description: String? = null
    ) = ActTestSpec(
        workflow,
        job,
        event,
        env,
        inputs,
        secrets,
        variables,
        matrix,
        resources,
        additionalArgs,
        description
    )

    "fails if workflow path is absent" {
        specFile.writeText("description: something")
        parseWithFailures("Spec file must have a single 'workflow' property specifying the path to the workflow file relative to the workspace root")
    }

    "fails if workflow does not exist in the workflows root" {
        specFile.writeText("workflow: non-existent.yml")
        parseWithFailures("The workflow file 'non-existent.yml' does not exist in the workflows root directory")
    }

    "handles optional parameters" {
        specFile.writeText("workflow: workflow.yml")
        parser.parse(specFile) shouldBeEqual actSpec()
    }

    "captures job ID" {
        specFile.writeText(
            """
            workflow: workflow.yml
            job: awesome-job
        """.trimIndent()
        )
        parser.parse(specFile) shouldBeEqual actSpec(
            job = "awesome-job"
        )
    }

    "handles event type" {
        specFile.writeText(
            """
            workflow: workflow.yml
            event: 
                type: pull_request
        """.trimIndent()
        )
        parser.parse(specFile) shouldBeEqual actSpec(
            event = ActTestSpecEvent("pull_request", null)
        )
    }

    "handles valid event payload file" {
        val payloadFile = specsRoot.resolve("event_payload.json").apply {
            this.createNewFile()
        }
        specFile.writeText(
            """
            workflow: workflow.yml
            event: 
                payload: event_payload.json
        """.trimIndent()
        )
        parser.parse(specFile) shouldBeEqual actSpec(
            event = ActTestSpecEvent(null, payloadFile.toPath())
        )
    }

    "requires event payload to be located in the specs root" {
        specFile.writeText(
            """
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
            workflow: workflow.yml
            event: 
                type: pull_request
                payload: event_payload.json
        """.trimIndent()
        )
        parser.parse(specFile) shouldBeEqual actSpec(
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
                workflow: workflow.yml
                $inputType:
                    values:
                        key1: value1
                        key2: 4
            """.trimIndent()
        )

        val expectedValues = mapOf("key1" to "value1", "key2" to 4)
        val expectedSpec = when (inputType) {
            "env" -> actSpec(env = ActTestSpecInput(null, expectedValues))
            "inputs" -> actSpec(inputs = ActTestSpecInput(null, expectedValues))
            "secrets" -> actSpec(secrets = ActTestSpecInput(null, expectedValues))
            "variables" -> actSpec(variables = ActTestSpecInput(null, expectedValues))
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
                workflow: workflow.yml
                $inputType:
                    file: values.json
            """.trimIndent()
        )

        val expectedSpec = when (inputType) {
            "env" -> actSpec(env = ActTestSpecInput(eventFile.toPath(), emptyMap()))
            "inputs" -> actSpec(inputs = ActTestSpecInput(eventFile.toPath(), emptyMap()))
            "secrets" -> actSpec(secrets = ActTestSpecInput(eventFile.toPath(), emptyMap()))
            "variables" -> actSpec(variables = ActTestSpecInput(eventFile.toPath(), emptyMap()))
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
            "env" -> actSpec(env = ActTestSpecInput(eventFile.toPath(), expectedValues))
            "inputs" -> actSpec(inputs = ActTestSpecInput(eventFile.toPath(), expectedValues))
            "secrets" -> actSpec(secrets = ActTestSpecInput(eventFile.toPath(), expectedValues))
            "variables" -> actSpec(variables = ActTestSpecInput(eventFile.toPath(), expectedValues))
            else -> throw IllegalArgumentException("Unexpected workflow input type")
        }
        parser.parse(specFile) shouldBeEqual expectedSpec
    }

    "can configure workflow resources" - {
        withData(
            nameFn = { "$it with default settings" },
            "artifactServer",
            "cacheServer"
        ) { resourceType ->
            specFile.writeText(
                """
                workflow: workflow.yml
                resources:
                    $resourceType:
                        enabled: true
            """.trimIndent()
            )

            val expectedSpec = when (resourceType) {
                "artifactServer" -> actSpec(
                    resources = ActTestSpecResources(
                        ActTestSpecResource(
                            true,
                            null,
                            null,
                            null
                        ), null
                    )
                )

                "cacheServer" -> actSpec(
                    resources = ActTestSpecResources(
                        null,
                        ActTestSpecResource(true, null, null, null)
                    )
                )

                else -> throw IllegalArgumentException("Unexpected workflow input type")
            }
            parser.parse(specFile) shouldBeEqual expectedSpec
        }

        withData(
            nameFn = { "$it with advanced settings" },
            "artifactServer",
            "cacheServer"
        ) { resourceType ->
            specFile.writeText(
                """
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
            workflow: workflow.yml
            additionalArgs:
            - --json
            - --platform
            - ubuntu-18.04=nektos/act-environments-ubuntu:18.04
        """.trimIndent()
        )
        parser.parse(specFile) shouldBeEqual actSpec(
            additionalArgs = listOf(
                "--json",
                "--platform",
                "ubuntu-18.04=nektos/act-environments-ubuntu:18.04"
            )
        )
    }

    "allows specifying spec description" {
        specFile.writeText(
            """
            workflow: workflow.yml
            description: |
                My first ever workflow spec file!
        """.trimIndent()
        )
        parser.parse(specFile) shouldBeEqual actSpec(description = "My first ever workflow spec file!")
    }

})


