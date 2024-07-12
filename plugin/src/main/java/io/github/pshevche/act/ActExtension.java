package io.github.pshevche.act;

import org.gradle.api.file.DirectoryProperty;

public interface ActExtension {

    DirectoryProperty getWorkflowsRoot();

    DirectoryProperty getSpecsRoot();
}
