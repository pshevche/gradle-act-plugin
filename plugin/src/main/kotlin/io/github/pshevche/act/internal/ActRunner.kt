package io.github.pshevche.act.internal

import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.io.path.absolutePathString

class ActRunner : ActConfigBuilder() {

    fun exec() {
        val result = ProcessBuilder()
            .command(cmd())
            .start()
            .forwardOutput()
            .waitFor()

        if (result != 0) {
            throw ActPluginException("Failed to invoke act: exit code $result")
        }
    }

    private fun cmd(): List<String> = mutableListOf<String>().apply {
        add("act")
        add("--workflows")
        add(workflows!!.absolutePathString())
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
