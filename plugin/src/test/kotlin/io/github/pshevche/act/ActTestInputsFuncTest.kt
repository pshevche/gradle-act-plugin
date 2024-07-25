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

package io.github.pshevche.act

import io.github.pshevche.act.fixtures.GradleProject
import io.github.pshevche.act.fixtures.assertEvents
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldContain
import org.opentest4j.reporting.events.core.Result.Status.SUCCESSFUL

class ActTestInputsFuncTest : FreeSpec({

    val project = extension(GradleProject(tempdir()))

    beforeTest {
        project.addWorkflow("print_inputs")
    }

    "supports setting workflow input values directly" {
        project.addSpec(
            "input_values.act.yml",
            """
            name: input values
            workflow: print_inputs.yml
            inputs:
                values:
                    greeting: Hello
                    name: Bruce
            """.trimIndent(),
        )

        val result = project.test("-Dact.forwardOutput=true")

        result.output shouldContain "Hello, Bruce!"
        assertEvents(project.xmlReport()) {
            spec(name = "input values", result = SUCCESSFUL) {
                job(name = "print_greeting", result = SUCCESSFUL)
            }
        }
    }

    "supports setting workflow inputs from file" {
        project.addSpecResource("inputs/greeting.input")
        project.addSpec(
            "inputs_file.act.yml",
            """
            name: inputs file
            workflow: print_inputs.yml
            inputs:
                file: inputs/greeting.input
            """.trimIndent(),
        )

        val result = project.test("-Dact.forwardOutput=true")

        result.output shouldContain "Hallo, Falco!"
        assertEvents(project.xmlReport()) {
            spec(name = "inputs file", result = SUCCESSFUL) {
                job(name = "print_greeting", result = SUCCESSFUL)
            }
        }
    }
})
