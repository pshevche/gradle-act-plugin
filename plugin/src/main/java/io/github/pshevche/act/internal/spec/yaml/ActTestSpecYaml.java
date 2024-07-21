package io.github.pshevche.act.internal.spec.yaml;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

// instantiated by SnakeYaml
@SuppressWarnings("unused")
public class ActTestSpecYaml {

    @Nullable
    private String name;
    @Nullable
    private String workflow;
    @Nullable
    private String job;
    @Nullable
    private ActTestSpecEventYaml event;
    @Nullable
    private ActTestSpecInputYaml env;
    @Nullable
    private ActTestSpecInputYaml inputs;
    @Nullable
    private ActTestSpecInputYaml secrets;
    @Nullable
    private ActTestSpecInputYaml variables;
    @Nullable
    private Map<String, Object> matrix;
    @Nullable
    private ActTestSpecResourcesYaml resources;
    @Nullable
    private List<String> additionalArgs;

    // Default constructor
    public ActTestSpecYaml() {
        this.name = null;
        this.workflow = null;
        this.job = null;
        this.event = null;
        this.env = null;
        this.inputs = null;
        this.secrets = null;
        this.variables = null;
        this.matrix = null;
        this.resources = null;
        this.additionalArgs = null;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(@Nullable String workflow) {
        this.workflow = workflow;
    }

    @Nullable
    public String getJob() {
        return job;
    }

    public void setJob(@Nullable String job) {
        this.job = job;
    }

    @Nullable
    public ActTestSpecEventYaml getEvent() {
        return event;
    }

    public void setEvent(@Nullable ActTestSpecEventYaml event) {
        this.event = event;
    }

    @Nullable
    public ActTestSpecInputYaml getEnv() {
        return env;
    }

    public void setEnv(@Nullable ActTestSpecInputYaml env) {
        this.env = env;
    }

    @Nullable
    public ActTestSpecInputYaml getInputs() {
        return inputs;
    }

    public void setInputs(@Nullable ActTestSpecInputYaml inputs) {
        this.inputs = inputs;
    }

    @Nullable
    public ActTestSpecInputYaml getSecrets() {
        return secrets;
    }

    public void setSecrets(@Nullable ActTestSpecInputYaml secrets) {
        this.secrets = secrets;
    }

    @Nullable
    public ActTestSpecInputYaml getVariables() {
        return variables;
    }

    public void setVariables(@Nullable ActTestSpecInputYaml variables) {
        this.variables = variables;
    }

    @Nullable
    public Map<String, Object> getMatrix() {
        return matrix;
    }

    public void setMatrix(@Nullable Map<String, Object> matrix) {
        this.matrix = matrix;
    }

    @Nullable
    public ActTestSpecResourcesYaml getResources() {
        return resources;
    }

    public void setResources(@Nullable ActTestSpecResourcesYaml resources) {
        this.resources = resources;
    }

    @Nullable
    public List<String> getAdditionalArgs() {
        return additionalArgs;
    }

    public void setAdditionalArgs(@Nullable List<String> additionalArgs) {
        this.additionalArgs = additionalArgs;
    }

    @Override
    public String toString() {
        return "ActTestSpecYaml{" +
                "name='" + name + '\'' +
                ", workflow='" + workflow + '\'' +
                ", job='" + job + '\'' +
                ", event=" + event +
                ", env=" + env +
                ", inputs=" + inputs +
                ", secrets=" + secrets +
                ", variables=" + variables +
                ", matrix=" + matrix +
                ", resources=" + resources +
                ", additionalArgs=" + additionalArgs +
                '}';
    }
}
