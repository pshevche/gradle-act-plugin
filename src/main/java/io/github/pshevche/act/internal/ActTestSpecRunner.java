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

package io.github.pshevche.act.internal;

import io.github.pshevche.act.internal.spec.ActTestSpec;
import io.github.pshevche.act.internal.spec.ActTestSpecEvent;
import io.github.pshevche.act.internal.spec.ActTestSpecInput;
import io.github.pshevche.act.internal.spec.ActTestSpecResource;
import io.github.pshevche.act.internal.spec.ActTestSpecResources;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class ActTestSpecRunner {

    private final ActTestSpecRunnerListener listener;

    public ActTestSpecRunner(ActTestSpecRunnerListener listener) {
        this.listener = listener;
    }

    public enum ActExecResult {
        PASSED,
        FAILED
    }

    public ActExecResult exec(ActTestSpec spec) {
        try {
            return execSpecInterruptibly(spec).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            return ActExecResult.FAILED;
        }

        return ActExecResult.FAILED;
    }

    private CompletableFuture<ActExecResult> execSpecInterruptibly(ActTestSpec spec) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return executeSpecWithListenerNotification(spec).waitFor() == 0
                    ? ActExecResult.PASSED
                    : ActExecResult.FAILED;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                return ActExecResult.FAILED;
            }
            return ActExecResult.FAILED;
        });
    }

    private Process executeSpecWithListenerNotification(ActTestSpec spec) throws IOException {
        var specDescriptor = new TestDescriptor.SpecDescriptor(spec.name());
        var process = new ProcessBuilder()
            .command(cmd(spec))
            .start();

        notifyListenersOnOutput(process.getInputStream(), it -> listener.onOutput(specDescriptor, it));
        notifyListenersOnOutput(process.getErrorStream(), it -> listener.onError(specDescriptor, it));

        return process;
    }

    private void notifyListenersOnOutput(InputStream stream, Consumer<String> lineConsumer) {
        try (var reader = new BufferedReader(new InputStreamReader(stream))) {
            var line = reader.readLine();
            while (line != null) {
                lineConsumer.accept(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new ActException("Failed to consume act output", e);
        }
    }

    private static List<String> cmd(ActTestSpec spec) {
        var cmd = new ArrayList<String>();
        cmd.add("act");
        addEvent(cmd, spec.event());
        addIfPresent(cmd, spec.workflow(), "workflows");
        addIfPresent(cmd, spec.job(), "job");
        addInputs(cmd, spec.env(), "env-file", "env");
        addInputs(cmd, spec.inputs(), "input-file", "input");
        addInputs(cmd, spec.secrets(), "secret-file", "secret");
        addInputs(cmd, spec.variables(), "var-file", "var");
        addMatrix(cmd, spec.matrix());
        addResources(cmd, spec.resources());
        cmd.addAll(spec.additionalArgs());
        return cmd;
    }

    private static void addResources(List<String> cmd, @Nullable ActTestSpecResources resources) {
        Optional.ofNullable(resources)
            .ifPresent(rs -> {
                addResource(cmd, rs.artifactServer(), "artifact-server-path", "artifact-server-addr", "artifact-server-port");
                addResource(cmd, rs.cacheServer(), "cache-server-path", "cache-server-addr", "cache-server-port");
            });
    }

    private static void addResource(List<String> cmd, @Nullable ActTestSpecResource resource, String storageParam, String addressParam, String portParam) {
        Optional.ofNullable(resource)
            .filter(ActTestSpecResource::enabled)
            .ifPresent(r -> {
                addIfPresent(cmd, r.storage(), storageParam);
                addIfPresent(cmd, r.host(), addressParam);
                addIfPresent(cmd, r.port(), portParam);
            });
    }

    private static void addMatrix(List<String> cmd, Map<String, Object> matrix) {
        matrix.forEach((k, v) -> addIfPresent(cmd, "%s:%s".formatted(k, v), "matrix"));
    }

    private static void addInputs(ArrayList<String> cmd, @Nullable ActTestSpecInput inputs, String fileParamName, String valueParamName) {
        Optional.ofNullable(inputs)
            .ifPresent(i -> {
                addIfPresent(cmd, i.file(), fileParamName);
                i.values().forEach((k, v) -> addIfPresent(cmd, "%s=%s".formatted(k, v), valueParamName));
            });
    }

    private static void addEvent(List<String> cmd, @Nullable ActTestSpecEvent event) {
        Optional.ofNullable(event)
            .ifPresentOrElse(
                it -> {
                    Optional.ofNullable(it.type())
                        .ifPresentOrElse(
                            cmd::add,
                            () -> cmd.add("--detect-event")
                        );
                    addIfPresent(cmd, it.payload(), "eventpath");
                },
                () -> cmd.add("--detect-event")
            );
    }

    private static void addIfPresent(List<String> cmd, @Nullable Object value, String paramName) {
        Optional.ofNullable(value)
            .ifPresent(v -> {
                cmd.add("--" + paramName);
                cmd.add(v.toString());
            });
    }
}
