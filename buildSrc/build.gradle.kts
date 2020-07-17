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
}

dependencies {
    compileOnly(gradleApi())

    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
    implementation("com.android.tools.build:gradle:4.2.0-alpha04")
    implementation(kotlin("gradle-plugin", "1.3.72"))
    implementation(kotlin("android-extensions"))
}