import org.jetbrains.kotlin.config.KotlinCompilerVersion
import java.util.*

plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.4"
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    defaultConfig {
        multiDexEnabled = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "0.1.0-dev14"
        kotlinCompilerVersion = "1.3.70-dev-withExperimentalGoogleExtensions-20200424"
    }
}

dependencies {
    val composeVersion = "0.1.0-dev14"

    api(project(":core"))

    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
    implementation("androidx.core:core-ktx:1.3.0")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.ui:ui-layout:$composeVersion")
    implementation("androidx.ui:ui-material:$composeVersion")
    implementation("androidx.ui:ui-tooling:$composeVersion")
    implementation("androidx.ui:ui-material-icons-extended:$composeVersion")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.9")

    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
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