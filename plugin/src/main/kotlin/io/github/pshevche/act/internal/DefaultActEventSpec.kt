package io.github.pshevche.act.internal

import io.github.pshevche.act.ActEventSpec
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import java.io.File

class DefaultActEventSpec(objects: ObjectFactory) : ActEventSpec {
    override val type: Property<String> = objects.property(String::class.java)
    override val payload: Property<File> = objects.property(File::class.java)
}
