/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
