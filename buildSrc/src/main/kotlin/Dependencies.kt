object Dependencies {
    const val desugar = "com.android.tools:desugar_jdk_libs:1.1.5"

    object ComposeMaterialDialogs {
        const val version = "0.6.2"

        const val core = "io.github.vanpra.compose-material-dialogs:core:$version"
        const val datetime = "io.github.vanpra.compose-material-dialogs:datetime:$version"
        const val color = "io.github.vanpra.compose-material-dialogs:color:$version"
    }

    object Ktlint {
        const val version = "0.43.2"
    }

    object Accompanist {
        private const val version = "0.22.0-rc"
        const val pager = "com.google.accompanist:accompanist-pager:$version"
        const val flowLayout = "com.google.accompanist:accompanist-flowlayout:$version"
    }

    object Kotlin {
        private const val version = "1.6.0"

        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    }

    object Shot {
        private const val version = "5.12.2"
        const val core = "com.karumi:shot:$version"
        const val android = "com.karumi:shot-android:$version"
    }

    object Google {
        const val material = "com.google.android.material:material:1.6.0-alpha02"
    }

    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.8.0-alpha02"

        object Testing {
            const val version = "1.4.1-alpha03"
            const val core = "androidx.test:core:$version"
            const val rules = "androidx.test:rules:$version"
            const val runner = "androidx.test:runner:$version"
        }

        object Compose {
            const val version = "1.1.0-rc01"

            const val ui = "androidx.compose.ui:ui:$version"
            const val material = "androidx.compose.material:material:$version"
            const val material3 = "androidx.compose.material3:material3:1.0.0-alpha02"
            const val materialIconsExtended =
                "androidx.compose.material:material-icons-extended:$version"
            const val animation = "androidx.compose.animation:animation:$version"
            const val foundationLayout = "androidx.compose.foundation:foundation-layout:$version"

            const val testing = "androidx.compose.ui:ui-test-junit4:$version"
            const val activity = "androidx.activity:activity-compose:1.4.0"
            const val navigation = "androidx.navigation:navigation-compose:2.4.0-rc01"
        }
    }
}