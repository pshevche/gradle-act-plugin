package io.github.pshevche.act

import org.gradle.api.Action
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import java.nio.file.Path

/**
 * A generic specification for different types of act GitHub action servers like artifact or cache servers.
 */
interface ActServerSpec {

    /**
     * Defines whether the server is enabled or not.
     */
    @get:Input
    val enabled: Property<Boolean>

    /**
     * Defines the address to which the server binds.
     */
    @get:Nested
    val address: ActServerAddress

    /**
     * Defines the address to which the server binds.
     */
    fun address(action: Action<ActServerAddress>) = action.execute(address)

    /**
     * Defines the location for the server to store its artifacts at.
     */
    @get:Input
    val path: Property<Path>

    fun finalizeValue() {
        enabled.finalizeValue()
        path.finalizeValue()
        address.host.finalizeValue()
        address.port.finalizeValue()
    }

    interface ActServerAddress {
        @get:Input
        val host: Property<String>

        @get:Input
        val port: Property<Int>
    }
}
