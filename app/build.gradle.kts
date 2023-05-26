plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}

android {

    namespace = ProjectSetting.PROJECT_NAME_SPACE_APP
    compileSdk = ProjectSetting.PROJECT_TARGET_SDK

    defaultConfig {
        applicationId = ProjectSetting.PROJECT_APP_ID
        minSdk = ProjectSetting.PROJECT_MIN_SDK
        targetSdk = ProjectSetting.PROJECT_TARGET_SDK
        versionCode = ProjectSetting.PROJECT_VERSION_CODE
        versionName = ProjectSetting.PROJECT_VERSION_NAME

        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Declaration build config
        buildConfigField("String", "DATABASE_NAME", ProjectSetting.DB)
        buildConfigField("String", "PREF_NAME", ProjectSetting.PREF)

        // Naming APK // AAB
        setProperty("archivesBaseName", "${ProjectSetting.NAME_APK}(${versionName})")

        // Declaration apps name debug mode
        val debugAttribute = "Dev"
        val nameAppDebug = "${ProjectSetting.NAME_APP} $debugAttribute"
        resourceConfigurations += setOf("en", "id")

        // Inject app name for debug
        resValue("string", "app_name", nameAppDebug)

        // Inject admob id for debug
        resValue("string", "admob_app_id", AdmobValue.Debug.ADMOB_APP_ID)
        resValue("string", "admob_interstitial", AdmobValue.Debug.ADMOB_INTERSTITIAL)

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }

    }

    signingConfigs {
        create("release") {
            // You need to specify either an absolute path or include the
            // keystore file in the same directory as the build.gradle file.
            // [PROJECT FOLDER NAME/app/[COPY YOUT KEY STORE] .jks in here
            storeFile = file(ProjectSign.PLAYSTORE_STORE_FILE)
            storePassword = ProjectSign.PLAYSTORE_STORE_PASSWORD
            keyAlias = ProjectSign.PLAYSTORE_KEY_ALIAS
            keyPassword = ProjectSign.PLAYSTORE_KEY_PASSWORD
        }
    }

    buildTypes {

        getByName("debug") {
            applicationIdSuffix = ".dev"
        }

        getByName("release") {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Generated Signed APK / AAB
            signingConfig = signingConfigs.getByName("release")

            // Inject app name for release
            resValue("string", "app_name", ProjectSetting.NAME_APP)


            // Inject admob id for release
            resValue("string", "admob_app_id", AdmobValue.ADMOB_APP_ID)
            resValue("string", "admob_interstitial", AdmobValue.ADMOB_INTERSTITIAL)

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

    implementation(project(DependencyGradle.MODULE_LIB_FROGO_KEYBOARD))

    implementation(Androidx.Work.runtimeKtx)
    implementation(Google.Hilt.android)

    implementation(DependencyGradle.FROGO_CONSUME_API)

    kapt(GitHub.glideCompiler)
    kapt(Google.Hilt.compiler)
    kapt(Androidx.Room.compiler)
    kapt(Androidx.Lifecycle.compiler)

}