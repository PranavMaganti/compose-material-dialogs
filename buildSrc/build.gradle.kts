plugins {
    `java-gradle-plugin`
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

dependencies {
    compileOnly(gradleApi())

    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
    implementation("com.android.tools.build:gradle:4.2.0-alpha10")
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("android-extensions"))
}