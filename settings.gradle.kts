pluginManagement {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
    
}
rootProject.name = "compose-material-dialogs"

include(":android")
include(":desktop")
include(":common")
include(":core")
include(":datetime")
include(":color")
include(":test-utils")