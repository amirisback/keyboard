plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {

    namespace = ProjectSetting.PROJECT_NAME_SPACE_LIB_FROGO_KEYBOARD
    compileSdk = ProjectSetting.PROJECT_TARGET_SDK

    defaultConfig {

        minSdk = ProjectSetting.PROJECT_MIN_SDK
        targetSdk = ProjectSetting.PROJECT_TARGET_SDK

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
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of("17"))
        }
    }

}

dependencies {

    api(Frogo.sdk)
    api(Frogo.ui)
    api(Frogo.recyclerView)

    api(Androidx.emoji2)
}