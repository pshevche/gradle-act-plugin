plugins {
    checkstyle
    idea
    `java-gradle-plugin`
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.open.test.reporting)
    implementation(libs.freemarker)
    implementation(libs.snakeyaml)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotest.dataset)
    testRuntimeOnly(libs.junit.platform.launcher)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

checkstyle {
    maxWarnings = 3
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
