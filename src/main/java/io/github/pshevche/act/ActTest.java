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

package io.github.pshevche.act;

import io.github.pshevche.act.internal.ActException;
import io.github.pshevche.act.internal.ActTestSpecRunner;
import io.github.pshevche.act.internal.ActTestSpecRunnerListener;
import io.github.pshevche.act.internal.CompositeActTestSpecRunnerListener;
import io.github.pshevche.act.internal.TestDescriptor.SpecDescriptor;
import io.github.pshevche.act.internal.reporting.ActTestReporter;
import io.github.pshevche.act.internal.reporting.CompositeActTestReporter;
import io.github.pshevche.act.internal.reporting.HtmlActTestReporter;
import io.github.pshevche.act.internal.reporting.OutputForwardingActRunnerListener;
import io.github.pshevche.act.internal.reporting.ReportingActRunnerListener;
import io.github.pshevche.act.internal.reporting.XmlActTestReporter;
import io.github.pshevche.act.internal.spec.ActTestSpecParser;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.VerificationException;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * Executes GitHub workflows using <a href="https://github.com/nektos/act">nektos/act</a> according to the provided
 * specification files.
 */
@CacheableTask
public abstract class ActTest extends DefaultTask {

    private static final String FORWARD_ACT_OUTPUT_SYS_PROP = "act.forwardOutput";
    private static final Pattern SPEC_FILE_EXTENSION = Pattern.compile(".*\\.act\\.(yml|yaml)$");
    private static final Comparator<File> SPEC_FILE_COMPARATOR = Comparator.comparing(File::getAbsolutePath);

    private final DirectoryProperty workflowsRoot = directoryProperty();
    private final DirectoryProperty specsRoot = directoryProperty();
    private final Property<Boolean> forwardActOutput = booleanProperty().convention(Boolean.getBoolean(FORWARD_ACT_OUTPUT_SYS_PROP));

    private Property<Boolean> booleanProperty() {
        return getProject().getObjects().property(Boolean.class);
    }

    private final DirectoryProperty reportsDir = directoryProperty().convention(buildDirectory().dir("reports/act/" + getName()));

    private DirectoryProperty directoryProperty() {
        return getProject().getObjects().directoryProperty();
    }

    private DirectoryProperty buildDirectory() {
        return getProject().getLayout().getBuildDirectory();
    }

    /**
     * The directory containing all workflow files referenced by specification files.
     */
    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    public DirectoryProperty getWorkflowsRoot() {
        return workflowsRoot;
    }

    /**
     * Directory containing specification files.
     * Specification files should end with *.act.yml or *.act.yaml.
     * Specification files from nested directories will also be discovered.
     */
    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    public DirectoryProperty getSpecsRoot() {
        return specsRoot;
    }

    /**
     * When {@code true}, {@code act} output will be forwarded to the standard output.
     */
    @Internal
    public Property<Boolean> getForwardActOutput() {
        return forwardActOutput;
    }

    /**
     * Directory in which XML and HTML test reports will be generated.
     */
    @OutputDirectory
    public DirectoryProperty getReportsDir() {
        return reportsDir;
    }

    @TaskAction
    void test() {
        var specs = findSpecFiles();
        if (specs.isEmpty()) {
            getLogger().warn(
                "Act plugin has not detected any specs to execute in '{}'. Make sure your specs are defined in files ending with *.act.yml or *.act.yaml",
                specsRoot.get().getAsFile().getAbsolutePath()
            );
            return;
        }

        var failed = new AtomicBoolean(false);
        var specParser = createSpecParser();
        try (var reporter = createActTestReporter()) {
            var listener = createRunnerListener(reporter);
            var runner = new ActTestSpecRunner(listener);

            reporter.reportTestExecutionStarted();
            specs.stream()
                .map(specParser::parse)
                .forEach(spec -> {
                    var specDescriptor = new SpecDescriptor(spec.name());
                    reporter.reportSpecStarted(specDescriptor);

                    var execResult = runner.exec(spec);
                    if (execResult == ActTestSpecRunner.ActExecResult.PASSED) {
                        reporter.reportSpecFinishedSuccessfully(specDescriptor);
                    } else {
                        failed.set(true);
                        reporter.reportSpecFinishedWithFailure(specDescriptor);
                    }
                });
            reporter.reportTestExecutionFinished();
        } catch (Exception e) {
            throw new ActException(e);
        }

        if (failed.get()) {
            throw new VerificationException("There were failing workflow executions. See the report at: file://%s".formatted(reportsDir.file("test.html").get().getAsFile().toPath()));
        }
    }

    private ActTestSpecRunnerListener createRunnerListener(ActTestReporter reporter) {
        if (forwardActOutput.get()) {
            return new CompositeActTestSpecRunnerListener(List.of(
                new OutputForwardingActRunnerListener(getLogger()),
                new ReportingActRunnerListener(reporter)
            ));
        } else {
            return new ReportingActRunnerListener(reporter);
        }
    }

    private List<File> findSpecFiles() {
        return specsRoot.getAsFileTree()
            .getFiles()
            .stream()
            .filter(ActTest::isActSpecFile)
            .sorted(SPEC_FILE_COMPARATOR)
            .toList();
    }

    private static boolean isActSpecFile(File file) {
        var matcher = SPEC_FILE_EXTENSION.matcher(file.getPath());
        return matcher.matches();
    }

    private ActTestSpecParser createSpecParser() {
        return new ActTestSpecParser(
            workflowsRoot.getAsFile().get().toPath(),
            specsRoot.getAsFile().get().toPath()
        );
    }

    private ActTestReporter createActTestReporter() throws Exception {
        var xmlReportFile = reportsDir.file("test.xml").get().getAsFile().toPath();
        var htmlReportFile = reportsDir.file("test.html").get().getAsFile().toPath();
        return new CompositeActTestReporter(List.of(
            new XmlActTestReporter(xmlReportFile),
            new HtmlActTestReporter(htmlReportFile)
        ));
    }
}
