package io.github.pshevche.act

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional

/**
 * A generic specification of various types of workflow inputs like env, secrets, variables or inputs.
 * Those can be supplied as a file containing values or as a map of values.
 * For the exact specification of the format, see the task documentation.
 */
interface ActInputSpec {

    @get:InputFile
    @get:Optional
    val file: RegularFileProperty

    @get:Input
    @get:Optional
    val values: MapProperty<String, String>

    fun finalizeValue() {
        file.finalizeValue()
        values.finalizeValue()
    }
}
