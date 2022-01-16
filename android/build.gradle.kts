plugins {
    id("org.jetbrains.compose") version "1.0.1"
    id("com.android.application")
    kotlin("android")
    id("kotlin-kapt")
}

//group = BuildConfig.Info.group
//version = BuildConfig.Info.version

android {
    compileSdk = ProjectConfig.compileSdk
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.vanpra.android"
        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk

        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packagingOptions.excludes.addAll(
        listOf(
            "META-INF/LICENSE",
            "META-INF/AL2.0",
            "META-INF/**",
        )
    )
    composeOptions {
        kotlinCompilerExtensionVersion = Dependencies.AndroidX.Compose.version
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":common"))

//    implementation(Dependencies.ComposeMaterialDialogs.core)
//    implementation(Dependencies.ComposeMaterialDialogs.datetime)
//    implementation(Dependencies.ComposeMaterialDialogs.color)

    implementation(Dependencies.Google.material)
    implementation(Dependencies.AndroidX.coreKtx)

    implementation(Dependencies.AndroidX.Compose.ui)
    implementation(Dependencies.AndroidX.Compose.material)
    implementation(Dependencies.AndroidX.Compose.materialIconsExtended)
    implementation(Dependencies.AndroidX.Compose.animation)
    implementation(Dependencies.AndroidX.Compose.foundationLayout)

    implementation(Dependencies.AndroidX.Compose.activity)
    implementation(Dependencies.AndroidX.Compose.navigation)

    coreLibraryDesugaring(Dependencies.desugar)
}