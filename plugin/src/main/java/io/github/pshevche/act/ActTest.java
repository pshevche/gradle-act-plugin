package io.github.pshevche.act;

import io.github.pshevche.act.internal.ActException;
import io.github.pshevche.act.internal.ActTestSpecRunner;
import io.github.pshevche.act.internal.ActTestSpecRunnerListener;
import io.github.pshevche.act.internal.CompositeActTestSpecRunnerListener;
import io.github.pshevche.act.internal.TestDescriptor;
import io.github.pshevche.act.internal.TestDescriptor.SpecDescriptor;
import io.github.pshevche.act.internal.reporting.ActTestReporter;
import io.github.pshevche.act.internal.reporting.CompositeActTestReporter;
import io.github.pshevche.act.internal.reporting.HtmlActTestReporter;
import io.github.pshevche.act.internal.reporting.OutputForwardingActRunnerListener;
import io.github.pshevche.act.internal.reporting.ReportingActRunnerListener;
import io.github.pshevche.act.internal.reporting.XmlActTestReporter;
import io.github.pshevche.act.internal.spec.ActTestSpecParser;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.util.PatternFilterable;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class ActTest extends DefaultTask {

    private static final String FORWARD_ACT_OUTPUT_SYS_PROP = "act.forwardOutput";
    private static final Pattern SPEC_FILE_EXTENSION = Pattern.compile(".*\\.act\\.(yml|yaml)$");

    private final DirectoryProperty workflowsRoot;
    private final DirectoryProperty specsRoot;
    private final DirectoryProperty reportsDir;

    @Inject
    ActTest(DirectoryProperty workflowsRoot, DirectoryProperty specsRoot) {
        this.workflowsRoot = workflowsRoot;
        this.specsRoot = specsRoot;
        this.reportsDir = getProject().getObjects().directoryProperty().convention(getProject().getLayout().getBuildDirectory().dir("reports/act"));
    }

    @TaskAction
    void test() {
        var specs = findSpecFiles();
        if (specs.isEmpty()) {
            getLogger().warn("Act plugin did not detect any specs to execute in '{}'. Make sure your specs are defined in files ending with *.act.yml or *.act.yaml", specsRoot.get().getAsFile().getAbsolutePath());
            return;
        }

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
                            reporter.reportSpecFinishedWithFailure(specDescriptor);
                        }
                    });
            reporter.reportTestExecutionFinished();
        } catch (Exception e) {
            throw new ActException(e);
        }
    }

    private ActTestSpecRunnerListener createRunnerListener(ActTestReporter reporter) {
        if (isOutputForwardingEnabled()) {
            return new CompositeActTestSpecRunnerListener(List.of(
                    new OutputForwardingActRunnerListener(getLogger()),
                    new ReportingActRunnerListener(reporter)
            ));
        } else {
            return new ReportingActRunnerListener(reporter);
        }
    }

    private boolean isOutputForwardingEnabled() {
        return getProject().getProviders().systemProperty(FORWARD_ACT_OUTPUT_SYS_PROP)
                .map(Boolean::parseBoolean)
                .getOrElse(false);
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
