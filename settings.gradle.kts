pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }

}

rootProject.name = "Meadow"

// App
include(":app")

// Core
include(":core:core-ai")
include(":core:core-auth")
include(":core:core-common")
include(":core:core-data")
include(":core:core-database")
include(":core:core-domain")
include(":core:core-export")
include(":core:core-google")
include(":core:core-media")
include(":core:core-network")
include(":core:core-node")
include(":core:core-sync")
include(":core:core-ui")
include(":core:core-utils")

// Features
include(":feature:common")
include(":feature:project")
include(":feature:script")
include(":feature:catalog")
include(":feature:calendar")
include(":feature:ai")
include(":feature:plotcards")
include(":feature:timeline")
include(":feature:storyboard")
// include(":feature:shotlist")
include(":feature:wiki")
include(":feature:mindmap")
include(":feature:familytree")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
