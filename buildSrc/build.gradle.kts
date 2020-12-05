repositories {
    google()
    jcenter()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
}

plugins {
    `kotlin-dsl`
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

dependencies {
    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.17.2")
    implementation("com.android.tools.build:gradle:7.0.0-alpha02")
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("android-extensions"))
}

gradlePlugin {
    plugins {
        register("common-library") {
            id = "common-library"
            implementationClass = "CommonModulePlugin"
        }
    }
}