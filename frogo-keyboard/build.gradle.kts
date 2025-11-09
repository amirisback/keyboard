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
        getByName("release") {
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
            register("release", MavenPublication::class) {
                from(components["release"])
                groupId = LibrarySetting.GROUP_ID
                artifactId = LibrarySetting.ARTIFACT_ID
                version = ProjectSetting.PROJECT_VERSION_NAME
            }
        }
    }
}