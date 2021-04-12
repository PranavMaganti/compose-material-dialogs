import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionAware

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
            apply("shot")
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
            implementation(Dependencies.Kotlin.stdlib)
            implementation(Dependencies.AndroidX.coreKtx)
            implementation(Dependencies.AndroidX.appcompat)
            implementation(Dependencies.material)

            implementation(Dependencies.AndroidX.Compose.ui)
            implementation(Dependencies.AndroidX.Compose.material)
            implementation(Dependencies.AndroidX.Compose.materialIconsExtended)
            implementation(Dependencies.AndroidX.composeActivity)

            androidTestImplementation(Dependencies.AndroidX.Compose.testing)
            add("androidTestImplementation", project(":test-utils"))
        }
    }

    private fun DependencyHandler.implementation(dependency: String) {
        add("implementation", dependency)
    }

    private fun DependencyHandler.androidTestImplementation(dependency: String) {
        add("androidTestImplementation", dependency)
    }
}