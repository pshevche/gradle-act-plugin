package io.github.pshevche.act;

import org.gradle.api.file.DirectoryProperty;

/**
 * Project extension to configure default <code>actTest</code> task.
 * <p>
 * Example usage:
 * <pre>
 *     {@code
 *     act {
 *         workflowsRoot = file('.github/workflows')
 *         specsRoot = file('.github/act')
 *     }
 *     }
 * </pre>
 */
public interface ActExtension {

    DirectoryProperty getWorkflowsRoot();

    DirectoryProperty getSpecsRoot();
}
