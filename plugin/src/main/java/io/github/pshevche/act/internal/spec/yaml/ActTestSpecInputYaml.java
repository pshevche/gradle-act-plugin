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
