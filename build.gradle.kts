buildscript {
    repositories {
        google()
        jcenter()
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }

    dependencies {
        classpath(Dependencies.Kotlin.gradlePlugin)
        classpath("org.jmailen.gradle:kotlinter-gradle:3.2.0")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven { url = uri("https://kotlin.bintray.com/kotlinx/") }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf(
                "-Xallow-jvm-ir-dependencies",
                "-Xskip-prerelease-check"
            )
        }
    }
}