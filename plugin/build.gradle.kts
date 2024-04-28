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

    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)
}

detekt {
    buildUponDefaultConfig = true
}

tasks.test {
    dependsOn("detekt")
    useJUnitPlatform()
}

gradlePlugin {
    val act by plugins.creating {
        id = "io.github.pshevche.act"
        implementationClass = "io.github.pshevche.act.ActPlugin"
    }
}
