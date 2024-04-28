package io.github.pshevche.act.extensions

import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.SpecFilterResult
import io.kotest.mpp.annotation
import kotlin.reflect.KClass

class LocalOnlyExtension : SpecFilter {
    override fun filter(kclass: KClass<*>): SpecFilterResult {
        kclass.annotation<LocalOnly>()?.let { localOnly ->
            if (System.getenv("CI") == "true") {
                return SpecFilterResult.Exclude(localOnly.reason)
            }
        }
        return SpecFilterResult.Include
    }
}
