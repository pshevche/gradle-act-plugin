package io.github.pshevche.act.fixtures

import io.github.pshevche.act.internal.ActConfigBuilder

class ActExecTaskBuilder : ActConfigBuilder() {

    fun toTaskConfig(): String {
        val taskConfigBuilder = StringBuilder()

        if (workflows != null) {
            taskConfigBuilder.append("workflows = file('${workflows!!.path}')\n")
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

        if (timeout != null) {
            taskConfigBuilder.append("timeout = java.time.Duration.ofMillis(${timeout!!.toMillis()})\n")
        }

        return taskConfigBuilder.toString()
    }
}
