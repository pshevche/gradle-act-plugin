plugins {
    id("com.gradle.develocity") version "3.17.1"
}

rootProject.name = "gradle-act-plugin"
include("plugin")

develocity {
    buildScan {
        publishing.onlyIf { System.getenv("CI") == "true" && it.buildResult.failures.isNotEmpty() }
        termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
        termsOfUseAgree = "yes"
    }
}
