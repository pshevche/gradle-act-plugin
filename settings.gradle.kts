plugins {
    id("com.gradle.enterprise") version "3.16.2"
}

rootProject.name = "gradle-act-plugin"
include("plugin")

gradleEnterprise {
    buildScan {
        publishOnFailure()
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}
