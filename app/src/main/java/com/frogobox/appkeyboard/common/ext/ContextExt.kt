package com.frogobox.appkeyboard.common.ext

import android.content.Context
import com.frogobox.appkeyboard.util.Constant.PREF_KEYBOARD_TYPE
import com.frogobox.appkeyboard.util.Constant.PREF_ROOT_NAME
import com.frogobox.libkeyboard.R
import com.frogobox.sdk.delegate.preference.PreferenceDelegatesImpl

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

fun Context.getKeyboardType(): Int {
    val pref = PreferenceDelegatesImpl(this, PREF_ROOT_NAME)
    return pref.loadPrefInt(PREF_KEYBOARD_TYPE, R.xml.keys_letters_qwerty)
}
