plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.vanpra.amblor"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Dependencies.AndroidX.Compose.version
        kotlinCompilerVersion = Dependencies.Kotlin.version
    }

}

dependencies {
    implementation(project(":core"))
    implementation(project(":datetime"))
    implementation(project(":color"))

   // implementation(Dependencies.ComposeMaterialDialogs.core)
   // implementation(Dependencies.ComposeMaterialDialogs.datetime)
   // implementation(Dependencies.ComposeMaterialDialogs.color)

    implementation("androidx.activity:activity-compose:1.3.0-alpha03")
    implementation(Dependencies.AndroidX.Compose.ui)
    implementation(Dependencies.AndroidX.Compose.material)
    implementation(Dependencies.AndroidX.Compose.materialIconsExtended)

    implementation(Dependencies.AndroidX.coreKtx)
    implementation(Dependencies.AndroidX.appcompat)

    implementation(Dependencies.material)

    implementation(Dependencies.Accompanist.coil)
}
