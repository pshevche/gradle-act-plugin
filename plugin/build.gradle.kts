plugins {
    idea
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

gradlePlugin {
    val greeting by plugins.creating {
        id = "io.github.pshevche.greeting"
        implementationClass = "io.github.pshevche.GradleActPluginPlugin"
    }
}

tasks.test {
    useJUnitPlatform()
}
