package io.github.pshevche.act.internal.reporting;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import io.github.pshevche.act.internal.ActException;
import io.github.pshevche.act.internal.TestDescriptor;
import io.github.pshevche.act.internal.TestDescriptor.SpecDescriptor;
import org.apache.groovy.util.Maps;
import org.opentest4j.reporting.events.core.Result.Status;

import javax.annotation.Nullable;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HtmlActTestReporter extends AbstractActTestReporter {

    private static final Configuration FREEMARKER_CONFIGURATION = createFreemarkerConfiguration();

    private static Configuration createFreemarkerConfiguration() {
        var config = new Configuration(new Version(2, 3, 33));
        config.setClassLoaderForTemplateLoading(HtmlActTestReporter.class.getClassLoader(), "templates");
        config.setDefaultEncoding("UTF-8");
        config.setLocalizedLookup(false);
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return config;
    }

    private final Path reportFile;
    private final List<Spec> specs = new ArrayList<>();
    private final Set<TestDescriptor.JobDescriptor> jobsWithSuccessfulSteps = new HashSet<>();

    public HtmlActTestReporter(Path reportFile) {
        this.reportFile = reportFile;
    }

    @Override
    public void reportTestExecutionFinished() {
        var templateInputs = Maps.of(
            "execution", new ExecutionSummary(
                specs.size(),
                specs.stream().mapToLong(it -> it.jobs.size()).sum(),
                specs.stream().flatMap(it -> it.jobs.stream()).filter(it -> "failed".equals(it.status)).count()
            ),
            "specs", specs
        );
        try (var writer = new FileWriter(reportFile.toFile())) {
            var template = FREEMARKER_CONFIGURATION.getTemplate("html_report.ftl");
            template.process(templateInputs, writer);
        } catch (TemplateException | IOException e) {
            throw new ActException("Failed to generate the HTML report", e);
        }
    }

    @Override
    public void reportSpecStarted(SpecDescriptor spec) {
        specs.add(new Spec(spec.name()));
    }

    @Override
    public void reportSpecFinishedSuccessfully(SpecDescriptor spec) {
        lastSpec().status = Status.SUCCESSFUL.name().toLowerCase();
    }

    @Override
    public void reportSpecFinishedWithFailure(SpecDescriptor spec) {
        lastSpec().status = Status.FAILED.name().toLowerCase();
    }

    @Override
    public void reportSuccessfulJobStep(TestDescriptor.JobDescriptor job) {
        jobsWithSuccessfulSteps.add(job);
    }

    @Override
    public void reportJobFinishedOrSkipped(TestDescriptor.JobDescriptor job, String output) {
        var status = jobsWithSuccessfulSteps.contains(job) ? Status.SUCCESSFUL : Status.SKIPPED;
        lastSpec().addJob(job.name(), status.name().toLowerCase(), sanitizeOutputForHTML(output));
    }

    @Override
    public void reportJobFinishedWithFailure(TestDescriptor.JobDescriptor job, String output) {
        lastSpec().addJob(job.name(), Status.FAILED.name().toLowerCase(), sanitizeOutputForHTML(output));
    }

    private String sanitizeOutputForHTML(String output) {
        return output.replaceAll(System.lineSeparator(), "<br/>");
    }

    private Spec lastSpec() {
        return specs.get(specs.size() - 1);
    }

    @Override
    public void close() {
        // do nothing
    }

    // getters are required by freemarker to populate the template
    @SuppressWarnings("unused")
    public static final class ExecutionSummary {
        private final int specsCount;
        private final long jobsCount;
        private final long failingJobsCount;

        ExecutionSummary(
            int specsCount,
            long jobsCount,
            long failingJobsCount
        ) {
            this.specsCount = specsCount;
            this.jobsCount = jobsCount;
            this.failingJobsCount = failingJobsCount;
        }

        public long getJobsCount() {
            return jobsCount;
        }

        public int getSpecsCount() {
            return specsCount;
        }

        public long getFailingJobsCount() {
            return failingJobsCount;
        }
    }

    // getters are required by freemarker to populate the template
    @SuppressWarnings("unused")
    public static class Spec {
        private final String name;
        private final List<Job> jobs = new ArrayList<>();
        @Nullable
        private String status;

        Spec(String name) {
            this.name = name;
        }

        void addJob(String name, String status, String output) {
            jobs.add(new Job(name, status, output));
        }

        public String getName() {
            return name;
        }

        public List<Job> getJobs() {
            return jobs;
        }

        @Nullable
        public String getStatus() {
            return status;
        }
    }

    // getters are required by freemarker to populate the template
    @SuppressWarnings("unused")
    public static final class Job {
        private final String name;
        private final String status;
        private final String output;

        Job(String name, String status, String output) {
            this.name = name;
            this.status = status;
            this.output = output;
        }

        public String getName() {
            return name;
        }

        public String getStatus() {
            return status;
        }

        public String getOutput() {
            return output;
        }
    }
}
