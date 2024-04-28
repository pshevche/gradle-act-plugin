package io.github.pshevche.act.fixtures

import io.github.pshevche.act.internal.ActConfigBuilder

class ActExecTaskBuilder : ActConfigBuilder() {

    fun toTaskConfig(): String {
        val taskConfigBuilder = StringBuilder()

        workflow?.let {
            taskConfigBuilder.append("workflow = file('${it.path}')\n")
        }

        job?.let {
            taskConfigBuilder.append("job = '$it'\n")
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

        secretsFile?.let {
            taskConfigBuilder.append("secrets.file.set(file('${it.path}'))\n")
        }
        if (secretValues.isNotEmpty()) {
            val valuesAsString = secretValues.map { e -> "\"${e.key}\": \"${e.value}\"" }.joinToString(", ")
            taskConfigBuilder.append("secrets { values.set([$valuesAsString]) }\n")
        }

        variablesFile?.let {
            taskConfigBuilder.append("variables.file.set(file('${it.path}'))\n")
        }
        if (variableValues.isNotEmpty()) {
            val valuesAsString = variableValues.map { e -> "\"${e.key}\": \"${e.value}\"" }.joinToString(", ")
            taskConfigBuilder.append("variables { values.set([$valuesAsString]) }\n")
        }

        inputsFile?.let {
            taskConfigBuilder.append("actionInputs.file.set(file('${it.path}'))\n")
        }
        if (inputValues.isNotEmpty()) {
            val valuesAsString = inputValues.map { e -> "\"${e.key}\": \"${e.value}\"" }.joinToString(", ")
            taskConfigBuilder.append("actionInputs { values.set([$valuesAsString]) }\n")
        }

        eventType?.let {
            taskConfigBuilder.append("event.type.set('$it')\n")
        }
        eventPayload?.let {
            taskConfigBuilder.append("event { payload.set(file('${it.path}')) }\n")
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

        return taskConfigBuilder.toString()
    }
}
