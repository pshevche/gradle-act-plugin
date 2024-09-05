plugins {
    id("com.gradle.develocity") version "3.18"
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}

rootProject.name = "gradle-act-plugin"

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
        termsOfUseAgree = "yes"
        publishing.onlyIf {
            providers.environmentVariable("CI").isPresent || it.buildResult.failures.isNotEmpty()
        }
    }
}