package io.github.pshevche.act.internal

import io.github.pshevche.act.ActServerSpec
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import java.nio.file.Path

class DefaultActServerSpec(
    objects: ObjectFactory,
    defaultHost: String,
    defaultPort: Int,
    defaultPath: Path
) : ActServerSpec {
    override val enabled: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    override val address: ActServerSpec.ActServerAddress = DefaultActServerAddress(objects, defaultHost, defaultPort)
    override val path: Property<Path> = objects.property(Path::class.java).convention(defaultPath)
}
