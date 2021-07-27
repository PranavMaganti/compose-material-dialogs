plugins {
    id("common-library")
}

android {
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    defaultConfig {
        minSdk = 21
        targetSdk = 30
        compileSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                    getDefaultProguardFile("proguard-android.txt"),
                    "proguard-rules.pro"
            )
        }
    }

    buildFeatures.compose = true

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Dependencies.AndroidX.Compose.version
    }
}

dependencies {
    api(project(":core"))
    implementation(Dependencies.Accompanist.pager)
    coreLibraryDesugaring(Dependencies.desugar)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=com.google.accompanist.pager.ExperimentalPagerApi"
        )
    }
}

val artifactName = "datetime"
val artifactGroup = "com.vanpra.compose-material-dialogs"
val artifactVersion = Dependencies.ComposeMaterialDialogs.version

val VERSION_NAME: String by project
val mavenCentralRepositoryUsername: String? by project
val mavenCentralRepositoryPassword: String? by project

publishing {
    repositories {
        withType<MavenArtifactRepository> {
            if (name == "local") {
                return@withType
            }

            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"

            url = if (VERSION_NAME.endsWith("SNAPSHOT")) {
                uri(snapshotsRepoUrl)
            } else {
                uri(releasesRepoUrl)
            }

            credentials {
                username = mavenCentralRepositoryUsername
                password = mavenCentralRepositoryPassword
            }
        }
    }
}