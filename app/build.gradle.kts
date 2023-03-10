import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}

android {

    namespace = "com.frogobox.keyboard"
    compileSdk = 33

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
        val debugAttribute = "Development"
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

    implementation(Androidx.Core.ktx)
    implementation(Androidx.appCompat)
    implementation(Androidx.activityKtx)
    implementation(Androidx.fragmentKtx)
    implementation(Androidx.constraintLayout)
    implementation(Androidx.Work.runtimeKtx)

    implementation(Androidx.Lifecycle.livedataKtx)
    implementation(Androidx.Lifecycle.viewmodelKtx)
    implementation(Androidx.Lifecycle.runtimeKtx)
    implementation(Androidx.Lifecycle.process)

    implementation(Androidx.Room.ktx)
    implementation(Androidx.Room.runtime)
    implementation(Androidx.Room.rxJava3)

    implementation(Google.material)
    implementation(Google.gson)

    implementation(Google.Hilt.android)

    implementation(Square.Retrofit2.retrofit)
    implementation(Square.Retrofit2.adapterRxJava3)
    implementation(Square.Retrofit2.converterGson)

    implementation(Square.OkHttp.okhttp)
    implementation(Square.OkHttp.loggingInterceptor)

    implementation(Reactivex.rxJava3)
    implementation(Reactivex.rxKotlin3)
    implementation(Reactivex.rxAndroid3)

    implementation(GitHub.glide)
    implementation(GitHub.chucker)

    implementation(Frogo.sdk)
    implementation(Frogo.ui)
    implementation(Frogo.recyclerView)
    implementation(Frogo.consumeApi)

    implementation(Koin.core)
    implementation(Koin.android)
    implementation(Koin.androidCompat)

    implementation("androidx.emoji2:emoji2-bundled:1.2.0")

    implementation("com.mikepenz:fastadapter:5.7.0")
    implementation("com.mikepenz:fastadapter-extensions-binding:5.7.0") // view binding helpers
    implementation("com.mikepenz:fastadapter-extensions-diff:5.7.0") // diff util helpers
    implementation("com.mikepenz:fastadapter-extensions-drag:5.7.0") // drag support
    implementation("com.mikepenz:fastadapter-extensions-paged:5.7.0") // paging support
    implementation("com.mikepenz:fastadapter-extensions-scroll:5.7.0") // scroll helpers
    implementation("com.mikepenz:fastadapter-extensions-swipe:5.7.0") // swipe support
    implementation("com.mikepenz:fastadapter-extensions-ui:5.7.0") // pre-defined ui components
    implementation("com.mikepenz:fastadapter-extensions-utils:5.7.0") // needs the `expandable`, `drag` and `scroll` extension.

    kapt(GitHub.glideCompiler)
    kapt(Google.Hilt.compiler)
    kapt(Androidx.Room.compiler)
    kapt(Androidx.Lifecycle.compiler)

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}