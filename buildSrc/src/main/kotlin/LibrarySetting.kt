/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */


import ProjectSetting.APP_DOMAIN
import ProjectSetting.APP_PLAY_CONSOLE

object LibrarySetting {

    const val GITHUB_ACCOUNT = "amirisback"
    const val GITHUB_REPOSITORY = "keyboard"

    const val LIB_NAME = "libkeyboard"

    const val ARTIFACT_ID = GITHUB_REPOSITORY
    const val GROUP_ID = "com.github.$GITHUB_ACCOUNT"

    const val NAME_SPACE = "$APP_DOMAIN.$APP_PLAY_CONSOLE.$LIB_NAME"

    const val MAVEN_URI = "https://maven.pkg.github.com/$GITHUB_ACCOUNT/$GITHUB_REPOSITORY"

}