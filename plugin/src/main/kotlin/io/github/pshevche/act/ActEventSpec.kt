package io.github.pshevche.act

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import java.io.File

/**
 * Event type and payload to trigger the workflow with.
 */
interface ActEventSpec {

    @get:Input
    @get:Optional
    val type: Property<String>

    @get:Input
    @get:Optional
    val payload: Property<File>
}
