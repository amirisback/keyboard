package com.frogobox.libkeyboard.common.ext

import android.graphics.Color
import kotlin.math.roundToInt

fun hsl2hsv(hsl: FloatArray): FloatArray {
    val hue = hsl[0]
    var sat = hsl[1]
    val light = hsl[2]
    sat *= if (light < .5) light else 1 - light
    val denominator = light + sat
    return if (denominator == 0f) {
        floatArrayOf(hue, 0f, 0f)
    } else {
        floatArrayOf(hue, 2f * sat / denominator, denominator)
    }
}

fun hsv2hsl(hsv: FloatArray): FloatArray {
    val hue = hsv[0]
    val sat = hsv[1]
    val value = hsv[2]

    val newHue = (2f - sat) * value
    val denominator = if (newHue < 1f) newHue else 2f - newHue
    var newSat = if (denominator == 0f) 0f else sat * value / denominator
    if (newSat > 1f) newSat = 1f

    return floatArrayOf(hue, newSat, newHue / 2f)
}

/**
 * Adjusts the lightness of a color by a given factor (in percent).
 * Positive factor lightens, negative factor darkens.
 */
private fun Int.adjustLightness(factor: Int): Int {
    if (this == Color.WHITE || this == Color.BLACK) {
        return this
    }

    var hsv = FloatArray(3)
    Color.colorToHSV(this, hsv)
    val hsl = hsv2hsl(hsv)
    hsl[2] = (hsl[2] + factor / 100f).coerceIn(0f, 1f)
    hsv = hsl2hsv(hsl)
    return Color.HSVToColor(hsv)
}

fun Int.lightenColor(factor: Int = 8): Int = adjustLightness(factor)

// taken from https://stackoverflow.com/a/40964456/1967672
fun Int.darkenColor(factor: Int = 8): Int = adjustLightness(-factor)

fun Int.adjustAlpha(factor: Float): Int {
    val alpha = (Color.alpha(this) * factor).roundToInt()
    val red = Color.red(this)
    val green = Color.green(this)
    val blue = Color.blue(this)
    return Color.argb(alpha, red, green, blue)
}

fun Int.getContrastColor(): Int {
    val DARK_GREY = 0xFF333333.toInt()
    val y = (299 * Color.red(this) + 587 * Color.green(this) + 114 * Color.blue(this)) / 1000
    return if (y >= 149 && this != Color.BLACK) DARK_GREY else Color.WHITE
}
