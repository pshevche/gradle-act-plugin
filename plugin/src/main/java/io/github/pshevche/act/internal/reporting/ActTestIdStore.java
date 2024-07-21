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
