package io.github.pshevche.act

import io.kotest.core.config.AbstractProjectConfig
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ActFuncTestConfig : AbstractProjectConfig() {
    override val timeout: Duration
        get() = 30.seconds
}