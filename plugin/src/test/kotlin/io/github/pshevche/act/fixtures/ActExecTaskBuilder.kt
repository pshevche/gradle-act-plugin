package io.github.pshevche.act.fixtures

import io.github.pshevche.act.internal.ActConfigBuilder
import kotlin.io.path.absolutePathString

class ActExecTaskBuilder : ActConfigBuilder() {

    fun toTaskConfig(): String {
        val taskConfigBuilder = StringBuilder()

        if (workflows != null) {
            taskConfigBuilder.append("workflows = file('${workflows!!.absolutePathString()}')")
        }

        return taskConfigBuilder.toString()
    }
}
