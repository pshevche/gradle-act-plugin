plugins {
    id("com.gradle.develocity") version "3.17.6"
}

rootProject.name = "gradle-act-plugin"

include("plugin")

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
        termsOfUseAgree = "yes"
        publishing.onlyIf {
            it.buildResult.failures.isNotEmpty()
        }
    }
}