package io.github.pshevche.act.internal.util

import io.github.pshevche.act.internal.util.CollectionUtils.nullToEmpty
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.equals.shouldBeEqual

class CollectionUtilsTest : FreeSpec({

    "null can be transformed to an empty list" {
        val list: List<String>? = null
        nullToEmpty(list) shouldBeEqual listOf()
    }

    "null can be transformed to an empty map" {
        val map: Map<String, String>? = null
        nullToEmpty(map) shouldBeEqual mapOf()
    }

    "non-null list is returned as-is" {
        val list = listOf("a")
        nullToEmpty(list) shouldBeEqual list
    }

    "non-null map is returned as-is" {
        val map = mapOf("a" to "b")
        nullToEmpty(map) shouldBeEqual map
    }
})
