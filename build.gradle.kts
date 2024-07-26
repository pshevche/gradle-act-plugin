plugins {
    checkstyle
    idea
    `java-gradle-plugin`
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.publish)
    signing
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

group = "io.github.pshevche"
version = "1.0"
gradlePlugin {
    website = "https://github.com/pshevche/gradle-act-plugin"
    vcsUrl = "https://github.com/pshevche/gradle-act-plugin.git"
    plugins.create("act") {
        id = "io.github.pshevche.act"
        displayName = "Plugin for validating GitHub workflows locally using 'nektos/act' runner"
        description = "Plugin for validating GitHub workflows locally using 'nektos/act' runner"
        tags = listOf("github", "github-workflow", "github-actions", "testing", "act")
        implementationClass = "io.github.pshevche.act.ActPlugin"
    }
}

signing {
    isRequired = providers.environmentVariable("CI").isPresent
    useInMemoryPgpKeys(
        providers.environmentVariable("PGP_SIGNING_KEY").orNull,
        providers.environmentVariable("PGP_SIGNING_KEY_PASSPHRASE").orNull
    )
}
