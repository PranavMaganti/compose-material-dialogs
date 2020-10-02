import java.util.Date

plugins {
    id("com.android.library")
    id("common-library")
    id("org.jmailen.kotlinter") version "3.2.0"
}

android {
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("dev.chrisbanes.accompanist:accompanist-coil:${Versions.accompanist}")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-Xallow-jvm-ir-dependencies",
            "-Xskip-prerelease-check",
            "-Xopt-in=kotlin.Experimental"
        )
    }
}

val artifactName = "core"
val artifactGroup = "com.vanpra.compose-material-dialogs"
val artifactVersion = Versions.library

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
}

artifactory {
    setContextUrl("http://oss.jfrog.org")
    publish(delegateClosureOf<org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig> {
        repository(delegateClosureOf<groovy.lang.GroovyObject> {
            setProperty("repoKey", "oss-snapshot-local")
            setProperty("username", project.findProperty("bintrayUser").toString())
            setProperty("password", project.findProperty("bintrayKey").toString())
            setProperty("maven", true)
        })

        defaults(delegateClosureOf<groovy.lang.GroovyObject> {
            invokeMethod("publications", "release")
            setProperty("publishPom", true)
            setProperty("publishArtifacts", true)
        })
    })

    resolve(delegateClosureOf<org.jfrog.gradle.plugin.artifactory.dsl.ResolverConfig> {
        setProperty("repoKey", "jcenter")
    })
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
