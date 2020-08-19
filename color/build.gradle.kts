import java.util.Date

plugins {
    id("com.android.library")
    id("common-library")
}

android {
    buildFeatures {
        compose = true
    }
}

dependencies {
    api(project(":core"))
}

val artifactName = "color"
val artifactGroup = "com.vanpra.compose-material-dialogs"
val artifactVersion = "0.1.8"

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