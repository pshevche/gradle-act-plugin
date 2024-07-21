package io.github.pshevche.act.internal.reporting;

import io.github.pshevche.act.internal.TestDescriptor;
import org.opentest4j.reporting.events.api.DocumentWriter;
import org.opentest4j.reporting.events.api.NamespaceRegistry;
import org.opentest4j.reporting.events.core.CoreFactory;
import org.opentest4j.reporting.events.core.Result.Status;
import org.opentest4j.reporting.events.root.Events;
import org.opentest4j.reporting.schema.Namespace;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static org.opentest4j.reporting.events.core.Result.Status.FAILED;
import static org.opentest4j.reporting.events.core.Result.Status.SUCCESSFUL;
import static org.opentest4j.reporting.events.root.RootFactory.finished;
import static org.opentest4j.reporting.events.root.RootFactory.started;

public class XmlActTestReporter extends AbstractActTestReporter {

    private final DocumentWriter<Events> writer;
    private final ActTestIdStore idStore = new ActTestIdStore();

    public XmlActTestReporter(Path reportFile) throws Exception {
        if (!reportFile.toFile().exists()) {
            Files.createDirectories(reportFile.getParent());
            Files.createFile(reportFile);
        }
        this.writer = Events.createDocumentWriter(
                NamespaceRegistry.builder(Namespace.REPORTING_CORE)
                        .add("e", Namespace.REPORTING_EVENTS)
                        .build(),
                reportFile
        );
    }

    @Override
    public void reportSpecStarted(TestDescriptor.SpecDescriptor spec) {
        writer.append(started(idStore.assign(spec).toString(), Instant.now(), spec.name()));
    }

    @Override
    public void reportSpecFinishedSuccessfully(TestDescriptor.SpecDescriptor spec) {
        writer.append(finished(idStore.retrieve(spec).toString(), Instant.now()), it -> {
            it.append(CoreFactory.result(SUCCESSFUL));
        });
    }

    @Override
    public void reportSpecFinishedWithFailure(TestDescriptor.SpecDescriptor spec) {
        writer.append(finished(idStore.retrieve(spec).toString(), Instant.now()), it -> {
            it.append(CoreFactory.result(FAILED));
        });
    }

    @Override
    public void reportJobStarted(TestDescriptor.JobDescriptor job) {
        writer.append(started(idStore.assign(job).toString(), Instant.now(), job.name()), it -> {
            it.withParentId(idStore.retrieve(job.spec()).toString());
        });
    }

    @Override
    public void reportJobFinishedSuccessfully(TestDescriptor.JobDescriptor job, String output) {
        reportJobFinished(job, SUCCESSFUL, output);
    }

    @Override
    public void reportJobFinishedWithFailure(TestDescriptor.JobDescriptor job, String output) {
        reportJobFinished(job, FAILED, output);
    }

    private void reportJobFinished(TestDescriptor.JobDescriptor job, Status status, String output) {
        writer.append(finished(idStore.retrieve(job).toString(), Instant.now()), it -> {
            it.append(CoreFactory.result(status), result -> {
                result.append(CoreFactory.reason(output));
            });
        });
    }

    @Override
    public void close() throws Exception {
        writer.close();
    }
}
