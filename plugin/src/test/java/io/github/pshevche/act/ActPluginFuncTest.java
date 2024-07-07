package io.github.pshevche.act;

import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ActPluginFuncTest extends BaseActPluginFuncTest {

    @Test
    void canRunTask() {
        BuildResult result = run("greeting");

        // Verify the result
        assertTrue(result.getOutput().contains("Hello from plugin 'io.github.pshevche.greeting'"));
    }
}
