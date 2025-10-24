// settings.gradle.kts

pluginManagement {
    repositories {
        google()
        mavenCentral() // <-- Add this line
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral() // <-- Also ensure this is here
    }
}
rootProject.name = "Meadow"
include(":app")

