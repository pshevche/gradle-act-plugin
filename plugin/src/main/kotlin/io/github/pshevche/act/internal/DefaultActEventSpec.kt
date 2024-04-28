package io.github.pshevche.act.internal

import io.github.pshevche.act.ActEventSpec
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

class DefaultActEventSpec(objects: ObjectFactory) : ActEventSpec {
    override val type: Property<String> = objects.property(String::class.java)
    override val payload: RegularFileProperty = objects.fileProperty()
}
