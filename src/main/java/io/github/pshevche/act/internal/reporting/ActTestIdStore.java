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

package io.github.pshevche.act.internal.reporting;

import io.github.pshevche.act.internal.ActException;
import io.github.pshevche.act.internal.TestDescriptor;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

class ActTestIdStore {

    private final AtomicLong globalId = new AtomicLong(1);
    private final Map<TestDescriptor, Long> store = new ConcurrentHashMap<>();

    Long assign(TestDescriptor testDescriptor) {
        if (store.containsKey(testDescriptor)) {
            throw new ActException("Cannot re-assign an ID for test " + testDescriptor.getValue());
        }

        return store.computeIfAbsent(testDescriptor, __ -> globalId.getAndIncrement());
    }

    Long retrieve(TestDescriptor testDescriptor) {
        return Objects.requireNonNull(store.get(testDescriptor), "Cannot find an ID for test " + testDescriptor.getValue());
    }
}
