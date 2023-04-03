import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}

android {

    namespace = ProjectSetting.PROJECT_NAME_SPACE
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
        resValue("string", "admob_app_id", AdmobValue.debugAdmobAppId)
        resValue("string", "admob_banner", AdmobValue.debugAdmobBanner)
        resValue("string", "admob_interstitial", AdmobValue.debugAdmobInterstitial)
        resValue("string", "admob_interstitial_video", AdmobValue.debugAdmobInterstitialVideo)
        resValue("string", "admob_rewarded", AdmobValue.debugAdmobRewarded)
        resValue("string", "admob_rewarded_interstitial", AdmobValue.debugAdmobRewardedInterstitial)
        resValue("string", "admob_native_advanced", AdmobValue.debugAdmobNativeAdvanced)
        resValue("string", "admob_native_advanced_video", AdmobValue.debugAdmobNativeAdvancedVideo)

    }

    signingConfigs {
        create("release") {
            // You need to specify either an absolute path or include the
            // keystore file in the same directory as the build.gradle file.
            // [PROJECT FOLDER NAME/app/[COPY YOUT KEY STORE] .jks in here
            storeFile = file(ProjectSetting.PLAYSTORE_STORE_FILE)
            storePassword = ProjectSetting.PLAYSTORE_STORE_PASSWORD
            keyAlias = ProjectSetting.PLAYSTORE_KEY_ALIAS
            keyPassword = ProjectSetting.PLAYSTORE_KEY_PASSWORD
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Generated Signed APK / AAB
            signingConfig = signingConfigs.getByName("release")

            // Inject app name for release
            resValue("string", "app_name", ProjectSetting.APP_NAME)


            // Inject admob id for release
            resValue("string", "admob_app_id", AdmobValue.releaseAdmobAppId)
            resValue("string", "admob_banner", AdmobValue.releaseAdmobBanner)
            resValue("string", "admob_interstitial", AdmobValue.releaseAdmobInterstitial)
            resValue("string", "admob_interstitial_video", AdmobValue.releaseAdmobInterstitialVideo)
            resValue("string", "admob_rewarded", AdmobValue.releaseAdmobRewarded)
            resValue("string", "admob_rewarded_interstitial", AdmobValue.releaseAdmobRewardedInterstitial)
            resValue("string", "admob_native_advanced", AdmobValue.releaseAdmobNativeAdvanced)
            resValue("string", "admob_native_advanced_video", AdmobValue.releaseAdmobNativeAdvancedVideo)

        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }
}

dependencies {

    implementation(Androidx.Work.runtimeKtx)

    implementation(Google.Hilt.android)

    implementation(Frogo.sdk)
    implementation(Frogo.ui)
    implementation(Frogo.recyclerView)
    implementation(Frogo.consumeApi)

    implementation(Androidx.emoji2)

    implementation(Util.FastAdapter.core)
    implementation(Util.FastAdapter.expandable)
    implementation(Util.FastAdapter.binding)
    implementation(Util.FastAdapter.diff)
    implementation(Util.FastAdapter.drag)
    implementation(Util.FastAdapter.paged)
    implementation(Util.FastAdapter.scroll)
    implementation(Util.FastAdapter.swipe)
    implementation(Util.FastAdapter.ui)
    implementation(Util.FastAdapter.utils)

    kapt(GitHub.glideCompiler)
    kapt(Google.Hilt.compiler)
    kapt(Androidx.Room.compiler)
    kapt(Androidx.Lifecycle.compiler)

}