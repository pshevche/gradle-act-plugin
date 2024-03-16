plugins {
    idea
    `java-gradle-plugin`
    `jvm-test-suite`
    alias(libs.plugins.jvm)
    alias(libs.plugins.detekt)
}

repositories {
    mavenCentral()
}

dependencies {
    detektPlugins(libs.detekt.ktlint)
}

gradlePlugin {
    val act by plugins.creating {
        id = "io.github.pshevche.act"
        implementationClass = "io.github.pshevche.act.ActPlugin"
    }
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {
                implementation(libs.kotest.runner)
                implementation(libs.kotest.assertions)
            }
        }

        val functionalTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()

            configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])
            configurations["functionalTestRuntimeOnly"].extendsFrom(configurations["testRuntimeOnly"])

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

tasks.named<Task>("check") {
    dependsOn(testing.suites.named("functionalTest"))
}
