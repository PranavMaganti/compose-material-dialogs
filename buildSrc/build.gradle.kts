plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("common-library") {
            id = "common-library"
            implementationClass = "CommonModulePlugin"
        }
    }
}

repositories {
    google()
    mavenCentral()
    jcenter()

    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap/")
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

dependencies {
    compileOnly(gradleApi())

    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.17.2")
    implementation("com.android.tools.build:gradle:4.2.0-alpha13")
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("android-extensions"))
}