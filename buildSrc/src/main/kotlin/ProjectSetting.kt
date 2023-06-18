/*
 * Created by faisalamir on 19/09/21
 * FrogoRecyclerView
 * -----------------------------------------
 * Name     : Muhammad Faisal Amir
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) 2021 FrogoBox Inc.
 * All rights reserved
 *
 */

object ProjectSetting {

    const val NAME_APP = "Frogo Keyboard"
    const val NAME_APP_DEBUG = "$NAME_APP Dev"

    const val APP_DOMAIN = "com"
    const val APP_PLAY_CONSOLE = "frogobox"

    // ---------------------------------------------------------------------------------------------

    const val VERSION_MAJOR = 1
    const val VERSION_MINOR = 1
    const val VERSION_PATCH = 4

    // ---------------------------------------------------------------------------------------------

    const val PROJECT_MIN_SDK = Version.Gradle.minSdk
    const val PROJECT_COMPILE_SDK = Version.Gradle.compileSdk
    const val PROJECT_TARGET_SDK = PROJECT_COMPILE_SDK

    // ---------------------------------------------------------------------------------------------

    const val BASE_PACAKGE_NAME = "com.frogobox"

    const val MODULE_APP = "appkeyboard"
    const val MODULE_LIB_FROGO_KEYBOARD = "libkeyboard"

    val APP_NAME = NAME_APP.lowercase().replace(" ", "")

    val PROJECT_APP_ID = "$APP_DOMAIN.$APP_PLAY_CONSOLE.$APP_NAME"

    const val PROJECT_NAME_SPACE_APP = "$BASE_PACAKGE_NAME.$MODULE_APP"
    const val PROJECT_NAME_SPACE_LIB_FROGO_KEYBOARD = "$BASE_PACAKGE_NAME.$MODULE_LIB_FROGO_KEYBOARD"

    const val PROJECT_VERSION_CODE = (VERSION_MAJOR * 100) + (VERSION_MINOR * 10) + (VERSION_PATCH * 1)
    const val PROJECT_VERSION_NAME = "$VERSION_MAJOR.$VERSION_MINOR.$VERSION_PATCH"

    // ---------------------------------------------------------------------------------------------

    val NAME_APK = NAME_APP.lowercase().replace(" ", "-")
    val NAME_DB = NAME_APP.lowercase().replace(" ", "_")
    val DB = "\"$NAME_DB.db\""
    val PREF = "\"pref_$NAME_DB\""

}
