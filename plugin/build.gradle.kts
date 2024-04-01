import com.gradle.enterprise.gradleplugin.testretry.retry

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
    config.from(file("$rootDir/config/detekt.yml"))
}

val isCiServer = System.getenv("CI") == "true"
tasks.test {
    dependsOn("detekt")
    useJUnitPlatform()
    if (isCiServer) {
        retry.maxRetries = 2
    }
}

gradlePlugin {
    val act by plugins.creating {
        id = "io.github.pshevche.act"
        implementationClass = "io.github.pshevche.act.ActPlugin"
    }
}
