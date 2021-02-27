object Dependencies {
    const val material = "com.google.android.material:material:1.2.1"

    object ComposeMaterialDialogs {
        const val version = "0.2.11"

        const val core = "com.vanpra.compose-material-dialogs:core:$version"
        const val datetime = "com.vanpra.compose-material-dialogs:datetime:$version"
        const val color = "com.vanpra.compose-material-dialogs:color:$version"
    }

    object Accompanist {
        private const val version = "0.6.0"
        const val coil = "dev.chrisbanes.accompanist:accompanist-coil:$version"
    }

    object Kotlin {
        const val version = "1.4.30"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.2.0"
        const val coreKtx = "androidx.core:core-ktx:1.5.0-beta02"

        object Compose {
            const val version = "1.0.0-beta01"

            const val ui = "androidx.compose.ui:ui:$version"
            const val material = "androidx.compose.material:material:$version"
            const val materialIconsExtended = "androidx.compose.material:material-icons-extended:$version"
        }
    }
}