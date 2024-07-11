plugins {
    idea
    `java-gradle-plugin`
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)
    testRuntimeOnly(libs.junit.platform.launcher)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    val act by plugins.creating {
        id = "io.github.pshevche.act"
        implementationClass = "io.github.pshevche.act.ActPlugin"
    }
}
