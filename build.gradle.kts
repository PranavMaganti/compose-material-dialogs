import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.diffplug.spotless") version "6.0.4"
    id("org.jetbrains.dokka") version "1.6.0"
}

buildscript {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Dependencies.Kotlin.gradlePlugin)
        classpath("com.android.tools.build:gradle:7.0.2")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.17.0")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.6.0")
        classpath(Dependencies.Shot.core)
    }
}

//group = BuildConfig.Info.group
//version = BuildConfig.Info.version

allprojects {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    tasks.withType<KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
                "-Xopt-in=androidx.compose.animation.ExperimentalAnimationApi",
                "-Xopt-in=androidx.compose.ui.test.ExperimentalTestApi",
                "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                "-Xopt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                "-Xopt-in=com.google.accompanist.pager.ExperimentalPagerApi"
            )
        }
    }
}


tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(projectDir.resolve("docs/api"))
}

subprojects {
    plugins.apply("com.diffplug.spotless")
    spotless {
        kotlin {
            target("**/*.kt")
            ktlint(Dependencies.Ktlint.version)
        }
    }

    tasks.withType<Test> {
        testLogging {
            showStandardStreams = true
        }
    }
}