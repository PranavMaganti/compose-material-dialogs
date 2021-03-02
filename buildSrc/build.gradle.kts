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
}

gradlePlugin {
    plugins {
        register("common-library") {
            id = "common-library"
            implementationClass = "CommonModulePlugin"
        }
    }
}