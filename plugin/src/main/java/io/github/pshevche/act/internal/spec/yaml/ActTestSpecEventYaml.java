package io.github.pshevche.act.internal.spec.yaml;

import javax.annotation.Nullable;

// instantiated by SnakeYaml
public final class ActTestSpecEventYaml {
    @Nullable
    private String type;
    @Nullable
    private String payload;

    public ActTestSpecEventYaml() {
        this.type = null;
        this.payload = null;
    }

    @Nullable
    public String getType() {
        return type;
    }

    @Nullable
    public String getPayload() {
        return payload;
    }

    public void setType(@Nullable String type) {
        this.type = type;
    }

    public void setPayload(@Nullable String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "ActTestSpecEventYaml[" +
                "type=" + type + ", " +
                "payload=" + payload + ']';
    }

}
