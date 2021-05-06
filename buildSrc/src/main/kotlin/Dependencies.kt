object Dependencies {
    const val material = "com.google.android.material:material:1.4.0-alpha02"

    object ComposeMaterialDialogs {
        const val version = "0.3.5"

        const val core = "io.github.vanpra.compose-material-dialogs:core:$version"
        const val datetime = "io.github.vanpra.compose-material-dialogs:datetime:$version"
        const val color = "io.github.vanpra.compose-material-dialogs:color:$version"
    }

    object Ktlint {
        const val version = "0.41.0"
    }

    object Accompanist {
        private const val version = "0.9.0"
        const val pager = "com.google.accompanist:accompanist-pager:$version"
    }

    object Kotlin {
        private const val version = "1.4.32"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    }
    
    object Shot {
        private const val version = "5.10.4"
        const val shot = "com.karumi:shot-android:$version"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.3.0-rc01"
        const val coreKtx = "androidx.core:core-ktx:1.6.0-alpha03"
        const val testRules = "androidx.test:rules:1.4.0-alpha05"

        object Compose {
            const val version = "1.0.0-beta06"

            const val ui = "androidx.compose.ui:ui:$version"
            const val material = "androidx.compose.material:material:$version"
            const val materialIconsExtended = "androidx.compose.material:material-icons-extended:$version"
            const val testing = "androidx.compose.ui:ui-test-junit4:$version"

            const val activity = "androidx.activity:activity-compose:1.3.0-alpha07"
            const val navigation = "androidx.navigation:navigation-compose:1.0.0-alpha10"
        }
    }
}