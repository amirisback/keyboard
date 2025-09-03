import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    `maven-publish`
}

android {

    namespace = ProjectSetting.PROJECT_NAME_SPACE_LIB_FROGO_KEYBOARD
    compileSdk = ProjectSetting.PROJECT_TARGET_SDK

    defaultConfig {

        minSdk = ProjectSetting.PROJECT_MIN_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}

dependencies {

    api(libs.frogo.android)
    api(libs.frogo.consume.api)
    api(libs.androidx.emoji2)

}

afterEvaluate {
    publishing {
        publications {

            repositories {
                maven {
                    name = LibrarySetting.LIB_NAME
                    url = uri(LibrarySetting.MAVEN_URI)
                    credentials {
                        username = project.findProperty("gpr.user") as String? ?: ""
                        password = project.findProperty("gpr.key") as String? ?: ""
                    }
                }
            }

            // Creates a Maven publication called "release".
            register("release", MavenPublication::class) {

                // Applies the component for the release build variant.
                // NOTE : Delete this line code if you publish Native Java / Kotlin Library
                from(components["release"])

                // Library Package Name (Example : "com.frogobox.androidfirstlib")
                // NOTE : Different GroupId For Each Library / Module, So That Each Library Is Not Overwritten
                groupId = LibrarySetting.GROUP_ID

                // Library Name / Module Name (Example : "androidfirstlib")
                // NOTE : Different ArtifactId For Each Library / Module, So That Each Library Is Not Overwritten
                artifactId = LibrarySetting.ARTIFACT_ID

                // Version Library Name (Example : "1.0.0")
                version = ProjectSetting.PROJECT_VERSION_NAME

            }
        }
    }
}