package com.frogobox.keyboard.ext

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.frogobox.keyboard.R

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.getProperTextColor() = ContextCompat.getColor(this, R.color.you_neutral_text_color)
fun Context.getProperBackgroundColor() = ContextCompat.getColor(this, R.color.you_background_color)
fun Context.getProperPrimaryColor() = ContextCompat.getColor(this, R.color.you_primary_color)
fun Context.getStrokeColor() = ContextCompat.getColor(this, R.color.md_grey_800)
fun Drawable.applyColorFilter(color: Int) = mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN)
fun ImageView.applyColorFilter(color: Int) = setColorFilter(color, PorterDuff.Mode.SRC_IN)