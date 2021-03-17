object Dependencies {
    const val material = "com.google.android.material:material:1.4.0-alpha01"

    object ComposeMaterialDialogs {
        const val version = "0.3.1"

        const val core = "com.vanpra.compose-material-dialogs:core:$version"
        const val datetime = "com.vanpra.compose-material-dialogs:datetime:$version"
        const val color = "com.vanpra.compose-material-dialogs:color:$version"
    }

    object Ktlint {
        const val version = "0.40.0"
    }

    object Kotlin {
        private const val version = "1.4.31"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.2.0-rc01"
        const val coreKtx = "androidx.core:core-ktx:1.5.0-alpha03"
        const val composeActivity = "androidx.activity:activity-compose:1.3.0-alpha04"

        object Compose {
            const val version = "1.0.0-beta02"

            const val ui = "androidx.compose.ui:ui:$version"
            const val material = "androidx.compose.material:material:$version"
            const val materialIconsExtended = "androidx.compose.material:material-icons-extended:$version"
        }
    }
}