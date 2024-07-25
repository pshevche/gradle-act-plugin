package io.github.pshevche.act.internal.spec.yaml;

import javax.annotation.Nullable;
import java.util.Map;

// instantiated by SnakeYaml
public final class ActTestSpecInputYaml {
    @Nullable
    private String file;
    @Nullable
    private Map<String, Object> values;

    public ActTestSpecInputYaml() {
        this.file = null;
        this.values = null;
    }

    @Nullable
    public String getFile() {
        return file;
    }

    @Nullable
    public Map<String, Object> getValues() {
        return values;
    }

    public void setFile(@Nullable String file) {
        this.file = file;
    }

    public void setValues(@Nullable Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "ActTestSpecInputYaml[" +
            "file=" + file + ", " +
            "values=" + values + ']';
    }

}
