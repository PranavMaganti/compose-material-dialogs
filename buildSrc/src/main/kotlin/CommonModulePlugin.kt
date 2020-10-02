import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class CommonModulePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply("kotlin-android")
        project.plugins.apply("kotlin-android-extensions")
        project.plugins.apply("maven-publish")
        project.plugins.apply("com.jfrog.artifactory")
        project.plugins.apply("com.jfrog.bintray")
        project.plugins.apply("org.jmailen.kotlinter")

        val androidExtension = project.extensions.getByName("android")
        if (androidExtension is BaseExtension) {
            androidExtension.apply {
                compileSdkVersion(30)

                defaultConfig {
                    minSdkVersion(21)
                    targetSdkVersion(30)
                    versionCode = 1

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_1_8
                    targetCompatibility = JavaVersion.VERSION_1_8
                }

                project.tasks.withType(KotlinCompile::class.java).configureEach {
                    kotlinOptions {
                        jvmTarget = "1.8"
                        useIR = true
                        freeCompilerArgs =
                            listOf("-Xallow-jvm-ir-dependencies", "-Xskip-prerelease-check")
                    }
                }

                composeOptions {
                    kotlinCompilerVersion = Versions.kotlin
                    kotlinCompilerExtensionVersion = Versions.compose
                }
            }

            project.dependencies {
                add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
                add("implementation", "androidx.core:core-ktx:1.0.2")
                add("implementation", "androidx.appcompat:appcompat:1.1.0")
                add("implementation", "com.google.android.material:material:1.1.0")

                add(
                    "implementation",
                    "androidx.compose.foundation:foundation-layout:${Versions.compose}"
                )
                add("implementation", "androidx.compose.material:material:${Versions.compose}")
                add("implementation", "androidx.ui:ui-tooling:${Versions.compose}")
                add(
                    "implementation",
                    "androidx.compose.material:material-icons-extended:${Versions.compose}"
                )
                add("implementation", "androidx.compose.animation:animation:${Versions.compose}")
            }
        }
    }
}