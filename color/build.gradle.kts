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
val artifactVersion = "0.1.2"

val sourcesJar by tasks.creating(Jar::class) {
    from(android.sourceSets.getByName("main").java.srcDirs)
    archiveClassifier.set("sources")
}

publishing {
    publications {
        create<MavenPublication>("compose-material-dialogs") {
            groupId = artifactGroup
            artifactId = artifactName
            version = artifactVersion

            artifact(sourcesJar)
            artifact("$buildDir/outputs/aar/$artifactName-release.aar")

            pom.withXml {
                val dependenciesNode = asNode().appendNode("dependencies")
                configurations.implementation.get().allDependencies.forEach {
                    if (it.name != "unspecified") {
                        val dependencyNode = dependenciesNode.appendNode("dependency")
                        dependencyNode.apply {
                            appendNode("groupId", it.group)
                            appendNode("artifactId", it.name)
                            appendNode("version", it.version)

                        }
                    }
                }
            }
        }
    }
}

bintray {
    user = project.findProperty("bintrayUser").toString()
    key = project.findProperty("bintrayKey").toString()
    publish = true

    setPublications("compose-material-dialogs")

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