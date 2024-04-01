package io.github.pshevche.act.internal

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ActRunner : ActConfigBuilder() {

    fun exec() {
        println("PSHEVCHE: ${cmd()}")
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

        add("--workflows")
        add(workflows!!.path)

        envFile?.let {
            add("--env-file")
            add(it.path)
        }

        addAll(envValues.flatMap { listOf("--env", "${it.key}=${it.value}") })

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
