rootProject.name = "gradle-maven-publish-plugin"

pluginManagement {
  repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
  }
}

plugins {
  id("com.gradle.develocity") version "4.1"
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
    // TODO: workaround for https://github.com/gradle/gradle/issues/22879.
    val isCI = providers.environmentVariable("CI").isPresent
    publishing.onlyIf { isCI }
  }
}

dependencyResolutionManagement {
  versionCatalogs {
    create("alpha") {
      from(files("gradle/alpha.versions.toml"))
    }
    create("beta") {
      from(files("gradle/beta.versions.toml"))
    }
    create("rc") {
      from(files("gradle/rc.versions.toml"))
    }
  }
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":plugin")
include(":central-portal")
includeBuild("build-logic")
