package io.github.pshevche.act.internal

import io.github.pshevche.act.ActInputSpec
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import java.io.File

class DefaultActInputSpec(objects: ObjectFactory, fileConvention: File) : ActInputSpec {
    override val file: Property<File> = objects.property(File::class.java).convention(fileConvention)
    override val values: MapProperty<String, String> =
        objects.mapProperty(String::class.java, String::class.java).convention(emptyMap())
}
