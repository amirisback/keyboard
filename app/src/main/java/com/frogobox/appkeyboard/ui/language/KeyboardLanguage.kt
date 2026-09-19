package com.frogobox.appkeyboard.ui.language

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */


data class KeyboardLanguage(
    val name: String,
    val xml: Int,
    val layoutType: String = "Standard",
    val code: String = "EN",
    val script: String = "Latin",
    val isRtl: Boolean = false
)