package io.github.pshevche.act.internal.spec.yaml;

import javax.annotation.Nullable;

public final class ActTestSpecResourceYaml {
    private boolean enabled;
    @Nullable
    private String storage;
    @Nullable
    private String host;
    @Nullable
    private Integer port;

    public ActTestSpecResourceYaml() {
        this.enabled = false;
        this.storage = null;
        this.host = null;
        this.port = null;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Nullable
    public String getStorage() {
        return storage;
    }

    @Nullable
    public String getHost() {
        return host;
    }

    @Nullable
    public Integer getPort() {
        return port;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setStorage(@Nullable String storage) {
        this.storage = storage;
    }

    public void setHost(@Nullable String host) {
        this.host = host;
    }

    public void setPort(@Nullable Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "ActTestSpecResourceYaml[" +
            "enabled=" + enabled + ", " +
            "storage=" + storage + ", " +
            "host=" + host + ", " +
            "port=" + port + ']';
    }

}
