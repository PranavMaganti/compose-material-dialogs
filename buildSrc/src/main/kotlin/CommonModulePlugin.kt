import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.JavaVersion
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class CommonModulePlugin: Plugin<Project> {
//    private val Project.android: BaseExtension
//        get() = extensions.findByName("android") as? BaseExtension
//            ?: error("Not an Android module $name")

    override fun apply(project: Project) {
        with(project) {
            applyPlugins()
//            androidConf()
            dependenciesConf()
        }
    }

    private fun Project.applyPlugins() {
        plugins.run {
            apply("com.android.library")
            apply("kotlin-android")
            apply("com.vanniktech.maven.publish")
        }
    }

//    private fun Project.androidConf() {
//        android.run {
//            lintOptions.isAbortOnError = false
//
//            compileSdkVersion(30)
//
//            buildFeatures.compose = true
//
//            compileOptions {
//                sourceCompatibility = JavaVersion.VERSION_1_8
//                targetCompatibility = JavaVersion.VERSION_1_8
//            }
//
//            composeOptions {
//                kotlinCompilerExtensionVersion = Dependencies.AndroidX.Compose.version
//            }
//        }
//    }

    private fun Project.dependenciesConf() {
        dependencies.apply {
            add("implementation", Dependencies.Kotlin.stdlib)
            add("implementation", Dependencies.AndroidX.coreKtx)
            add("implementation", Dependencies.AndroidX.appcompat)
            add("implementation", Dependencies.material)

            add("implementation", Dependencies.AndroidX.Compose.ui)
            add("implementation", Dependencies.AndroidX.Compose.material)
            add("implementation", Dependencies.AndroidX.Compose.materialIconsExtended)
        }
    }
}