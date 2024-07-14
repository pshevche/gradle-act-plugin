package io.github.pshevche.act.internal.spec;

import io.github.pshevche.act.internal.ActException;
import io.github.pshevche.act.internal.spec.yaml.ActTestSpecEventYaml;
import io.github.pshevche.act.internal.spec.yaml.ActTestSpecInputYaml;
import io.github.pshevche.act.internal.spec.yaml.ActTestSpecResourceYaml;
import io.github.pshevche.act.internal.spec.yaml.ActTestSpecResourcesYaml;
import io.github.pshevche.act.internal.spec.yaml.ActTestSpecYaml;
import org.gradle.internal.impldep.org.yaml.snakeyaml.LoaderOptions;
import org.gradle.internal.impldep.org.yaml.snakeyaml.Yaml;
import org.gradle.internal.impldep.org.yaml.snakeyaml.constructor.Constructor;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.pshevche.act.internal.util.CollectionUtils.nullToEmpty;
import static java.util.stream.Collectors.joining;

public class ActTestSpecParser {

    private static final LoaderOptions DEFAULT_LOADER_OPTIONS = new LoaderOptions();

    private final Path workflowsRoot;
    private final Path specsRoot;

    public ActTestSpecParser(Path workflowsRoot, Path specsRoot) {
        this.workflowsRoot = workflowsRoot;
        this.specsRoot = specsRoot;
    }

    public ActTestSpec parse(File specFile) {
        var specYaml = loadSpecYaml(specFile);
        return validateAndResolve(specFile, specYaml);
    }

    private static ActTestSpecYaml loadSpecYaml(File specFile) {
        try (var inputStream = new FileInputStream(specFile)) {
            var yaml = new Yaml(new Constructor(ActTestSpecYaml.class, DEFAULT_LOADER_OPTIONS));
            return yaml.load(inputStream);
        } catch (Exception e) {
            throw new ActException("Failed to parse an act spec file", e);
        }
    }

    private ActTestSpec validateAndResolve(File specFile, ActTestSpecYaml specYaml) {
        var validator = new Validator(specFile);
        var actTestSpec = new ActTestSpec(
                toWorkflowPath(specYaml.getWorkflow(), validator),
                specYaml.getJob(),
                toWorkflowEvent(specYaml.getEvent(), validator),
                toWorkflowInputs(specYaml.getEnv(), validator),
                toWorkflowInputs(specYaml.getInputs(), validator),
                toWorkflowInputs(specYaml.getSecrets(), validator),
                toWorkflowInputs(specYaml.getVariables(), validator),
                nullToEmpty(specYaml.getMatrix()),
                toWorkflowResources(specYaml.getResources()),
                nullToEmpty(specYaml.getAdditionalArgs()),
                specYaml.getDescription()
        );
        validator.throwIfFailed();
        return actTestSpec;
    }

    private Path toWorkflowPath(@Nullable String workflow, Validator validator) {
        validator.validate(
                workflow != null,
                "Spec file must have a single 'workflow' property specifying the path to the workflow file relative to the workspace root"
        );

        return Optional.ofNullable(workflow)
                .map(workflowsRoot::resolve)
                .map(it -> {
                    validator.validate(
                            it.toFile().exists(),
                            "The workflow file '%s' does not exist in the workflows root directory".formatted(workflow)
                    );
                    return it;
                })
                // this value is irrelevant and will not be accessed anywhere
                .orElse(null);
    }

    @Nullable
    private ActTestSpecEvent toWorkflowEvent(@Nullable ActTestSpecEventYaml event, Validator validator) {
        return Optional.ofNullable(event)
                .map(it -> {
                    var payloadFile = it.getPayload() == null ? null : specsRoot.resolve(it.getPayload());
                    validator.validate(
                            payloadFile == null || payloadFile.toFile().exists(),
                            "The event payload file '%s' does not exist in the specs root directory".formatted(event.getPayload())
                    );
                    return new ActTestSpecEvent(event.getType(), payloadFile);
                })
                .orElse(null);
    }

    @Nullable
    private ActTestSpecInput toWorkflowInputs(@Nullable ActTestSpecInputYaml inputs, Validator validator) {
        return Optional.ofNullable(inputs)
                .map(it -> {
                    var valuesFile = it.getFile() == null ? null : specsRoot.resolve(it.getFile());
                    validator.validate(
                            valuesFile == null || specsRoot.resolve(it.getFile()).toFile().exists(),
                            "The input values file '%s' does not exist in the specs root directory".formatted(it.getFile())
                    );
                    return new ActTestSpecInput(valuesFile, nullToEmpty(inputs.getValues()));
                })
                .orElse(null);
    }

    @Nullable
    private ActTestSpecResources toWorkflowResources(@Nullable ActTestSpecResourcesYaml resources) {
        return Optional.ofNullable(resources)
                .map(it -> new ActTestSpecResources(
                        toWorkflowResource(it.getArtifactServer()),
                        toWorkflowResource(it.getCacheServer())
                ))
                .orElse(null);
    }

    @Nullable
    private ActTestSpecResource toWorkflowResource(@Nullable ActTestSpecResourceYaml resource) {
        return Optional.ofNullable(resource)
                .map(it -> new ActTestSpecResource(
                        it.isEnabled(),
                        it.getStorage() == null ? null : Paths.get(it.getStorage()),
                        it.getHost(),
                        it.getPort()
                ))
                .orElse(null);
    }

    private static class Validator {
        private final File specFile;
        private final List<String> violations = new ArrayList<>();

        private Validator(File specFile) {
            this.specFile = specFile;
        }

        void validate(boolean condition, String message) {
            if (!condition) {
                violations.add(message);
            }
        }

        void throwIfFailed() {
            if (!violations.isEmpty()) {
                throw new ActException("Provided spec file ('%s') has following violations:\n%s".formatted(
                        specFile.getAbsolutePath(),
                        violations.stream().collect(joining("\n", "- ", ""))
                ));
            }
        }
    }
}
