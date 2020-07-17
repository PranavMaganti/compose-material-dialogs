import org.jetbrains.kotlin.config.KotlinCompilerVersion
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
    api(project(":core"))
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.9")
}

val artifactName = "datetime"
val artifactGroup = "com.vanpra.compose-material-dialogs"
val artifactVersion = "0.1.0"

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