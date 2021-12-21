repositories {
    google()
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}

plugins {
    `kotlin-dsl`
}

dependencies {
    modules {
        module("org.jetbrains.trove4j:trove4j") {
            replacedBy("org.jetbrains.intellij.deps:trove4j")
        }
    }
}

gradlePlugin {
    plugins {
        register("common-library") {
            id = "common-library"
            implementationClass = "CommonModulePlugin"
        }
    }
}