package io.github.pshevche.act;

import io.github.pshevche.act.internal.ActTestSpecRunner;
import io.github.pshevche.act.internal.spec.ActTestSpecParser;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.util.PatternFilterable;

import javax.inject.Inject;
import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class ActTest extends DefaultTask {

    private static final Pattern SPEC_FILE_EXTENSION = Pattern.compile(".*\\.act\\.(yml|yaml)$");

    private final DirectoryProperty workflowsRoot;
    private final DirectoryProperty specsRoot;

    @Inject
    ActTest(DirectoryProperty workflowsRoot, DirectoryProperty specsRoot) {
        this.workflowsRoot = workflowsRoot;
        this.specsRoot = specsRoot;
    }

    @TaskAction
    void test() {
        var specs = findSpecFiles();
        if (specs.isEmpty()) {
            getLogger().warn("Act plugin did not detect any specs to execute in '{}'. Make sure your specs are defined in files ending with *.act.yml or *.act.yaml", specsRoot.get().getAsFile().getAbsolutePath());
            return;
        }

        specs.stream()
                .map(ActTestSpecParser::parse)
                .forEach(ActTestSpecRunner::run);
    }

    private Set<File> findSpecFiles() {
        return specsRoot.getAsFileTree()
                .matching(isActSpecFile())
                .getFiles();
    }

    private static Action<PatternFilterable> isActSpecFile() {
        return patterns -> patterns.include(include -> {
            var matcher = SPEC_FILE_EXTENSION.matcher(include.getPath());
            return matcher.matches();
        });
    }
}
