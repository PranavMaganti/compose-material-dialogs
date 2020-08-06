import com.android.build.gradle.BaseExtension
import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.extra
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Date

class CommonModulePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply("kotlin-android")
        project.plugins.apply("kotlin-android-extensions")
        project.plugins.apply("maven-publish")
        project.plugins.apply("com.jfrog.bintray")
        // project.plugins.apply("org.jmailen.kotlinter")

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
                        freeCompilerArgs = listOf("-Xallow-jvm-ir-dependencies", "-Xskip-prerelease-check")
                    }
                }

                composeOptions {
                    kotlinCompilerVersion = "1.4.0-rc"
                    kotlinCompilerExtensionVersion = "0.1.0-dev16"
                }
            }

            project.dependencies {
                val composeVersion = "0.1.0-dev16"
                val kotlinVersion = "1.4-M3"

                add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
                add("implementation", "androidx.core:core-ktx:1.0.2")
                add("implementation", "androidx.appcompat:appcompat:1.1.0")
                add("implementation", "com.google.android.material:material:1.1.0")

                add("implementation", "androidx.compose.foundation:foundation-layout:$composeVersion")
                add("implementation", "androidx.compose.material:material:$composeVersion")
                add("implementation", "androidx.ui:ui-tooling:$composeVersion")
                add("implementation", "androidx.compose.material:material-icons-extended:$composeVersion")
                add("implementation", "androidx.compose.animation:animation:$composeVersion")

                add("testImplementation", "junit:junit:4.12")
                add("androidTestImplementation", "androidx.test.ext:junit:1.1.1")
                add("androidTestImplementation", "androidx.test.espresso:espresso-core:3.2.0")
            }
        }
    }
}