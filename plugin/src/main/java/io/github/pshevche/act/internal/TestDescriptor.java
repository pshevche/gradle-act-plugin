package io.github.pshevche.act.internal;

public sealed interface TestDescriptor {

    String getValue();

    record SpecDescriptor(String name) implements TestDescriptor {

        @Override
        public String getValue() {
            return "spec(" + name + ")";
        }
    }

    record JobDescriptor(SpecDescriptor spec, String name) implements TestDescriptor {

        @Override
        public String getValue() {
            return "job(" + spec.name() + "/" + name + ")";
        }
    }
}
