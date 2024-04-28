package io.github.pshevche.act.internal

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ActRunner : ActConfigBuilder() {

    fun exec() {
        val actFuture = supplyAsync {
            ProcessBuilder()
                .command(cmd())
                .start()
                .forwardOutput()
                .waitFor()
        }

        try {
            val result = if (timeout != null) {
                actFuture.get(timeout!!.toMillis(), TimeUnit.MILLISECONDS)
            } else {
                actFuture.get()
            }

            if (result != 0) {
                throw ActPluginException("Failed to invoke act command with exit code $result")
            }
        } catch (e: TimeoutException) {
            throw ActPluginException("Failed to complete act command within the configured timeout", e)
        }
    }

    private fun cmd(): List<String> = mutableListOf<String>().apply {
        add("act")

        if (eventType == null) {
            add("--detect-event")
        } else {
            add(eventType!!)
        }
        eventPayload?.let {
            add("--eventpath")
            add(it.path)
        }

        add("--workflows")
        add(workflow!!.path)

        job?.let {
            add("--job")
            add(it)
        }

        envFile?.let {
            add("--env-file")
            add(it.path)
        }
        addAll(envValues.flatMap { listOf("--env", "${it.key}=${it.value}") })

        inputsFile?.let {
            add("--input-file")
            add(it.path)
        }
        addAll(inputValues.flatMap { listOf("--input", "${it.key}=${it.value}") })

        secretsFile?.let {
            add("--secret-file")
            add(it.path)
        }
        addAll(secretValues.flatMap { listOf("--secret", "${it.key}=${it.value}") })

        variablesFile?.let {
            add("--var-file")
            add(it.path)
        }
        addAll(variableValues.flatMap { listOf("--var", "${it.key}=${it.value}") })

        addAll(additionalArgs)
    }

    private fun Process.forwardOutput(): Process {
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line: String? = reader.readLine()
            while (line != null) {
                println(line)
                line = reader.readLine()
            }
        }

        return this
    }
}
