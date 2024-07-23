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
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.InputDirectory;
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

@CacheableTask
public abstract class ActTest extends DefaultTask {

    private static final String FORWARD_ACT_OUTPUT_SYS_PROP = "act.forwardOutput";
    private static final Pattern SPEC_FILE_EXTENSION = Pattern.compile(".*\\.act\\.(yml|yaml)$");
    private static final Comparator<File> SPEC_FILE_COMPARATOR = Comparator.comparing(File::getAbsolutePath);

    private final DirectoryProperty workflowsRoot = directoryProperty();
    private final DirectoryProperty specsRoot = directoryProperty();
    private final DirectoryProperty reportsDir = directoryProperty().convention(buildDirectory().dir("reports/act"));

    private DirectoryProperty directoryProperty() {
        return getProject().getObjects().directoryProperty();
    }

    private DirectoryProperty buildDirectory() {
        return getProject().getLayout().getBuildDirectory();
    }

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    public DirectoryProperty getWorkflowsRoot() {
        return workflowsRoot;
    }

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    public DirectoryProperty getSpecsRoot() {
        return specsRoot;
    }

    @OutputDirectory
    public DirectoryProperty getReportsDir() {
        return reportsDir;
    }

    @TaskAction
    void test() {
        var specs = findSpecFiles();
        if (specs.isEmpty()) {
            getLogger().warn("Act plugin has not detected any specs to execute in '{}'. Make sure your specs are defined in files ending with *.act.yml or *.act.yaml", specsRoot.get().getAsFile().getAbsolutePath());
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
        var xmlReportFile = reportsDir.file(getName() + ".xml").get().getAsFile().toPath();
        var htmlReportFile = reportsDir.file(getName() + ".html").get().getAsFile().toPath();
        return new CompositeActTestReporter(List.of(
                new XmlActTestReporter(xmlReportFile),
                new HtmlActTestReporter(htmlReportFile)
        ));
    }
}
