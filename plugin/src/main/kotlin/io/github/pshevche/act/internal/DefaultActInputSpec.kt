package io.github.pshevche.act.internal

import io.github.pshevche.act.ActInputSpec
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty

class DefaultActInputSpec(objects: ObjectFactory) : ActInputSpec {
    override val file: RegularFileProperty = objects.fileProperty()
    override val values: MapProperty<String, String> =
        objects.mapProperty(String::class.java, String::class.java).convention(emptyMap())
}
