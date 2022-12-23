plugins {
    id("common-library")
}

android {
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    defaultConfig {
        minSdk = 21
        compileSdk = 33
        targetSdk = 33

        testInstrumentationRunner = "com.karumi.shot.ShotTestRunner"
        testApplicationId = "com.vanpra.composematerialdialogs.test"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures.compose = true

    packagingOptions.excludes.addAll(
        listOf(
            "META-INF/DEPENDENCIES.txt",
            "META-INF/LICENSE",
            "META-INF/LICENSE.txt",
            "META-INF/NOTICE",
            "META-INF/NOTICE.txt",
            "META-INF/AL2.0",
            "META-INF/LGPL2.1"
        )
    )

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Dependencies.AndroidX.Compose.compilerVersion
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=com.google.accompanist.pager.ExperimentalPagerApi"
        )
    }
    namespace = "com.vanpra.composematerialdialogs.datetime"
}

dependencies {
    api(project(":core"))
    implementation(Dependencies.Accompanist.pager)
    implementation("androidx.compose.ui:ui-tooling-preview:1.1.1")
    coreLibraryDesugaring(Dependencies.desugar)
    debugImplementation("androidx.compose.ui:ui-tooling:1.1.1")
}

shot {
    tolerance = 1.0 // Tolerance needed for CI
}
