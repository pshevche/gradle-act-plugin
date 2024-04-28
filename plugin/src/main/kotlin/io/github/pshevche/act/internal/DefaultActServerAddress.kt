package io.github.pshevche.act.internal

import io.github.pshevche.act.ActServerSpec
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

class DefaultActServerAddress(objects: ObjectFactory, defaultHost: String, defaultPort: Int) :
    ActServerSpec.ActServerAddress {
    override val host: Property<String> = objects.property(String::class.java).convention(defaultHost)
    override val port: Property<Int> = objects.property(Int::class.java).convention(defaultPort)
}
