plugins {
    checkstyle
    idea
    `java-gradle-plugin`
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    detektPlugins(libs.detekt.ktlint)

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
    configFile = rootDir.resolve("config/checkstyle.xml")
    maxWarnings = 3
}

detekt {
    config.from(rootDir.resolve("config/detekt.yml"))
    buildUponDefaultConfig = true
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
