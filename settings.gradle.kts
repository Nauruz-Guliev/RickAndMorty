pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "RickAndMorty"
include(":app")
include(":common")
include(":core:ui")
include(":core:data")
include(":feature:characters")
include(":feature:locations")
include(":feature:episodes")
