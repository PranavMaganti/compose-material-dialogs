object Dependencies {
    const val desugar = "com.android.tools:desugar_jdk_libs:1.1.5"

    object ComposeMaterialDialogs {
        const val version = "0.5.1"

        const val core = "io.github.vanpra.compose-material-dialogs:core:$version"
        const val datetime = "io.github.vanpra.compose-material-dialogs:datetime:$version"
        const val color = "io.github.vanpra.compose-material-dialogs:color:$version"
    }

    object Ktlint {
        const val version = "0.42.1"
        const val core = "com.printrest:ktlint:$version"
    }

    object Accompanist {
        private const val version = "0.16.1"
        const val pager = "com.google.accompanist:accompanist-pager:$version"
    }

    object Kotlin {
        private const val version = "1.5.30-RC"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    }

    object Shot {
        private const val version = "5.11.2"
        const val core = "com.karumi:shot:$version"
    }

    object AndroidX {
        object Testing {
            const val version = "1.4.0"
            const val core = "androidx.test:core:$version"
            const val rules = "androidx.test:rules:$version"
            const val runner = "androidx.test:runner:$version"
        }

        object Compose {
            const val version = "1.1.0-alpha02"

            const val ui = "androidx.compose.ui:ui:$version"
            const val material = "androidx.compose.material:material:$version"
            const val materialIconsExtended =
                "androidx.compose.material:material-icons-extended:$version"

            const val testing = "androidx.compose.ui:ui-test-junit4:1.1.0-alpha02"
            const val activity = "androidx.activity:activity-compose:1.3.1"
            const val navigation = "androidx.navigation:navigation-compose:2.4.0-alpha07"
        }
    }
}