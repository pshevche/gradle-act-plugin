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
    val greeting by plugins.creating {
        id = "io.github.pshevche.greeting"
        implementationClass = "io.github.pshevche.GradleActPluginPlugin"
    }
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {
                implementation(libs.kotlin.junit5)
                runtimeOnly(libs.junit.platform.launcher)
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
