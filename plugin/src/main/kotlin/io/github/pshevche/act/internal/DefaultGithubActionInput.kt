package io.github.pshevche.act.internal

import io.github.pshevche.act.GithubActionInput
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import java.io.File

class DefaultGithubActionInput(objects: ObjectFactory, fileConvention: File) : GithubActionInput {
    override val file: Property<File> = objects.property(File::class.java).convention(fileConvention)
    override val values: MapProperty<String, String> =
        objects.mapProperty(String::class.java, String::class.java).convention(emptyMap())
}
