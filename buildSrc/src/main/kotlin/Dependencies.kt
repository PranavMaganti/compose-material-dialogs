object Dependencies {
    const val desugar = "com.android.tools:desugar_jdk_libs:1.2.0"

    object ComposeMaterialDialogs {
        const val version = "0.7.0"

        const val core = "io.github.vanpra.compose-material-dialogs:core:$version"
        const val datetime = "io.github.vanpra.compose-material-dialogs:datetime:$version"
        const val color = "io.github.vanpra.compose-material-dialogs:color:$version"
    }

    object Ktlint {
        const val version = "0.47.1"
    }

    object Accompanist {
        private const val version = "0.25.1"
        const val pager = "com.google.accompanist:accompanist-pager:$version"
    }

    object Kotlin {
        private const val version = "1.7.20"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    }

    object Shot {
        private const val version = "5.14.1"
        const val core = "com.karumi:shot:$version"
        const val android = "com.karumi:shot-android:$version"
    }

    object Google {
        const val material = "com.google.android.material:material:1.6.1"
    }

    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.9.0"
        const val viewmodelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1"

        object Testing {
            const val core = "androidx.test:core:1.5.0"
            const val rules = "androidx.test:rules:1.5.0"
            const val runner = "androidx.test:runner:1.5.0"
        }

        object Compose {
            const val version = "1.3.1"
            const val compilerVersion = "1.3.2"

            const val ui = "androidx.compose.ui:ui:$version"
            const val material = "androidx.compose.material:material:$version"
            const val material3 = "androidx.compose.material3:material3:1.0.1"
            const val materialIconsExtended =
                "androidx.compose.material:material-icons-extended:$version"
            const val animation = "androidx.compose.animation:animation:$version"
            const val foundationLayout = "androidx.compose.foundation:foundation-layout:$version"

            const val testing = "androidx.compose.ui:ui-test-junit4:$version"
            const val activity = "androidx.activity:activity-compose:1.6.1"
            const val navigation = "androidx.navigation:navigation-compose:2.5.3"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1"
        }
    }
}