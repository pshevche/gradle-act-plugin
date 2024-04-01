package io.github.pshevche.act.fixtures

import io.github.pshevche.act.internal.ActConfigBuilder

class ActExecTaskBuilder : ActConfigBuilder() {

    fun toTaskConfig(): String {
        val taskConfigBuilder = StringBuilder()

        workflows?.let {
            taskConfigBuilder.append("workflows = file('${it.path}')\n")
        }

        if (additionalArgs.isNotEmpty()) {
            taskConfigBuilder.append(
                "additionalArgs = [${
                    additionalArgs.joinToString(
                        separator = ", ",
                        transform = { "'$it'" }
                    )
                }]\n"
            )
        }

        timeout?.let {
            taskConfigBuilder.append("timeout = java.time.Duration.ofMillis(${it.toMillis()})\n")
        }

        envFile?.let {
            taskConfigBuilder.append("env.file.set(file('${it.path}'))\n")
        }

        if (envValues.isNotEmpty()) {
            val valuesAsString = envValues.map { e -> "\"${e.key}\": \"${e.value}\"" }.joinToString(", ")
            taskConfigBuilder.append("env { values.set([$valuesAsString]) }\n")
        }

        return taskConfigBuilder.toString()
    }
}
