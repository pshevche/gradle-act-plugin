package io.github.pshevche.act

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional

/**
 * Event type and payload to trigger the workflow with.
 */
interface ActEventSpec {

    @get:Input
    @get:Optional
    val type: Property<String>

    @get:InputFile
    @get:Optional
    val payload: RegularFileProperty
}
