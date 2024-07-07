package io.github.pshevche.act;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

abstract class BaseActPluginFuncTest {

    @TempDir
    private File projectDir;

    @BeforeEach
    void setup() throws IOException {
        writeString(new File(projectDir, "settings.gradle"), "");
        writeString(getBuildFile(),
                "plugins {" +
                        "  id('io.github.pshevche.act')" +
                        "}");
    }

    private File getBuildFile() {
        return new File(projectDir, "build.gradle");
    }

    protected BuildResult run(String... args) {
        return runner(args).build();
    }

    protected BuildResult runAndFail(String... args) {
        return runner(args).buildAndFail();
    }

    private GradleRunner runner(String... args) {
        return GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withArguments(args)
                .withProjectDir(projectDir);
    }

    private static void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
