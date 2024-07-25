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

package io.github.pshevche.act.internal.spec.yaml;

import javax.annotation.Nullable;

public final class ActTestSpecResourcesYaml {
    @Nullable
    private ActTestSpecResourceYaml artifactServer;
    @Nullable
    private ActTestSpecResourceYaml cacheServer;

    public ActTestSpecResourcesYaml() {
        this.artifactServer = null;
        this.cacheServer = null;
    }

    @Nullable
    public ActTestSpecResourceYaml getArtifactServer() {
        return artifactServer;
    }

    @Nullable
    public ActTestSpecResourceYaml getCacheServer() {
        return cacheServer;
    }

    public void setArtifactServer(@Nullable ActTestSpecResourceYaml artifactServer) {
        this.artifactServer = artifactServer;
    }

    public void setCacheServer(@Nullable ActTestSpecResourceYaml cacheServer) {
        this.cacheServer = cacheServer;
    }

    @Override
    public String toString() {
        return "ActTestSpecResourcesYaml[" +
            "artifactServer=" + artifactServer + ", " +
            "cacheServer=" + cacheServer + ']';
    }

}
