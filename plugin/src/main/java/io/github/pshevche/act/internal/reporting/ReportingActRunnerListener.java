package io.github.pshevche.act.internal.reporting;

import io.github.pshevche.act.internal.ActTestSpecRunnerListener;
import io.github.pshevche.act.internal.TestDescriptor.JobDescriptor;
import io.github.pshevche.act.internal.TestDescriptor.SpecDescriptor;

import java.util.HashMap;
import java.util.Map;

public class ReportingActRunnerListener implements ActTestSpecRunnerListener {

    private final ActTestReporter reporter;
    private final Map<JobDescriptor, StringBuilder> outputByJob = new HashMap<>();

    public ReportingActRunnerListener(ActTestReporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public void onOutput(SpecDescriptor spec, String line) {
        onOutputOrError(spec, line);
    }

    @Override
    public void onError(SpecDescriptor spec, String line) {
        onOutputOrError(spec, line);
    }

    private void onOutputOrError(SpecDescriptor spec, String line) {
        if (line.startsWith("[")) {
            var jobName = extractJobName(line);
            var jobDescriptor = new JobDescriptor(spec, jobName);
            if (!outputByJob.containsKey(jobDescriptor)) {
                reporter.reportJobStarted(jobDescriptor);
            }
            outputByJob.computeIfAbsent(jobDescriptor, __ -> new StringBuilder())
                    .append(line)
                    .append(System.lineSeparator());

            if (line.contains("Success - Main")) {
                reporter.reportSuccessfulJobStep(jobDescriptor);
            } else if (line.endsWith("Job succeeded")) {
                reporter.reportJobFinishedOrSkipped(jobDescriptor, outputByJob.get(jobDescriptor).toString());
            } else if (line.endsWith("Job failed")) {
                reporter.reportJobFinishedWithFailure(jobDescriptor, outputByJob.get(jobDescriptor).toString());
            }
        }
    }

    private String extractJobName(String line) {
        var prefixSeparator = line.indexOf("/");
        var prefixEnd = line.indexOf("]");
        return line.substring(prefixSeparator + 1, prefixEnd).trim();
    }
}
