package com.frogobox.libkeyboard.common.ext

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.frogobox.libkeyboard.R

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

fun Context.isDarkThemeOn(): Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
}

fun Context.getProperTextColor() = if (isDarkThemeOn()) {
    ContextCompat.getColor(this, R.color.system_accent1_10)
} else {
    ContextCompat.getColor(this, R.color.system_accent1_900)
}

fun Context.getProperBackgroundColor() = if (isDarkThemeOn()) {
    ContextCompat.getColor(this, R.color.system_neutral1_900)
} else {
    ContextCompat.getColor(this, R.color.system_neutral1_10)
}

fun Context.getProperPrimaryColor() = if (isDarkThemeOn()) {
    ContextCompat.getColor(this, R.color.system_accent1_400)
} else {
    ContextCompat.getColor(this, R.color.system_accent1_400)
}

fun Context.getStrokeColor() = ContextCompat.getColor(this, R.color.keyboard_bg_item_grey)

fun Drawable.applyColorFilter(color: Int) = mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN)

fun ImageView.applyColorFilter(color: Int) = setColorFilter(color, PorterDuff.Mode.SRC_IN)