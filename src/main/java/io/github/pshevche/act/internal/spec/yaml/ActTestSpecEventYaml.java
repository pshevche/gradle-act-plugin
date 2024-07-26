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
