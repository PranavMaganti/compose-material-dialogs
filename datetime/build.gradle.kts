import java.util.Date

plugins {
    id("com.android.library")
    id("common-library")
}

android {
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("com.vanpra.compose-material-dialogs:core:0.1.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.9")
}

val artifactName = "datetime"
val artifactGroup = "com.vanpra.compose-material-dialogs"
val artifactVersion = "0.1.0"

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