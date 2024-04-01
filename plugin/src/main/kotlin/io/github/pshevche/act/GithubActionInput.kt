package io.github.pshevche.act

import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import java.io.File

/**
 * A generic specification of various types of workflow inputs like env, secrets, variables or inputs.
 * Those can be supplied as a file containing values or as a map of values.
 * For the exact specification of the format, see the task documentation.
 */
interface GithubActionInput {

    @get:Input
    @get:Optional
    val file: Property<File>

    @get:Input
    @get:Optional
    val values: MapProperty<String, String>
}
