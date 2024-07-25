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
