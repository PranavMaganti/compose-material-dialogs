import java.util.*

plugins {
    id("common-library")
}

android {
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        compileSdkVersion(30)

        versionCode = 1

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

    packagingOptions.excludes.addAll(
        listOf(
            "META-INF/LICENSE",
            "META-INF/AL2.0",
            "META-INF/**",
            "META-INF/*.kotlin_module"
        )
    )

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
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
}

val artifactName = "datetime"
val artifactGroup = "com.vanpra.compose-material-dialogs"
val artifactVersion = Dependencies.ComposeMaterialDialogs.version

val sourcesJar by tasks.creating(Jar::class) {
    from(android.sourceSets.getByName("main").java.srcDirs)
    archiveClassifier.set("sources")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components.getByName("release"))
                artifact(sourcesJar)

                groupId = artifactGroup
                artifactId = artifactName
                version = artifactVersion
            }
        }
    }

    bintray {
        user = project.findProperty("bintrayUser").toString()
        key = project.findProperty("bintrayKey").toString()
        publish = true
        override = true

        setPublications("release")

        pkg.apply {
            repo = "maven"
            name = "compose-material-dialogs:$artifactName"

            version.apply {
                name = artifactVersion
                released = Date().toString()
                vcsTag = artifactVersion
            }
        }
    }
}