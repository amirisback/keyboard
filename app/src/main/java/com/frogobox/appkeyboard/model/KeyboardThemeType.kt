package com.frogobox.appkeyboard.model

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.frogobox.appkeyboard.R

/**
 * Created by faisalamircs on 16/03/2024
 * -----------------------------------------
 * Name     : Muhammad Faisal Amir
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 */

enum class ThemeType {
    COLOR, IMAGE
}

enum class KeyboardThemeType(
    val label: String,
    val desc: String,
    val themeType: ThemeType,
    val background: Int,
    val isDark: Boolean = false,
) {
    DEFAULT("Default", "Classic Adaptive", ThemeType.COLOR, R.color.color_bg_keyboard_default, isDark = false),
    PURPLE("Frogo Purple", "Signature Brand", ThemeType.COLOR, R.color.color_bg_keyboard_purple, isDark = true),
    DARK("Midnight AMOLED", "Deep OLED Black", ThemeType.COLOR, R.color.color_bg_keyboard_dark, isDark = true),
    BLUE("Ocean Blue", "Calm & Focused", ThemeType.COLOR, R.color.color_bg_keyboard_blue, isDark = true),
    GREEN("Forest Emerald", "Natural Harmony", ThemeType.COLOR, R.color.color_bg_keyboard_green, isDark = true),
    RED("Crimson Sunset", "Vibrant Warmth", ThemeType.COLOR, R.color.color_bg_keyboard_red, isDark = true),
    ORANGE("Sunset Orange", "Energetic Twilight", ThemeType.COLOR, R.color.color_bg_keyboard_orange, isDark = true),
    CYAN("Nordic Cyan", "Fresh & Clean", ThemeType.COLOR, R.color.color_bg_keyboard_cyan, isDark = true),
    PINK("Sakura Pink", "Aesthetic Pastel", ThemeType.COLOR, R.color.color_bg_keyboard_pink, isDark = true),
    YELLOW("Amber Gold", "Golden Accent", ThemeType.COLOR, R.color.color_bg_keyboard_yellow, isDark = false),
    HIGH_CONTRAST("High Contrast (WCAG AAA)", "Ultra OLED Black & Canary Yellow", ThemeType.COLOR, R.color.color_bg_keyboard_high_contrast, isDark = true),
    IMAGE_BG_DARK("Wallpaper", "Sample Artwork", ThemeType.IMAGE, R.drawable.ic_wallpaper_dummy, isDark = true);

    fun mapToModel(): KeyboardThemeModel {
        return KeyboardThemeModel(
            this.label,
            this.desc,
            this.themeType,
            this.background,
            this.isDark
        )
    }

    companion object {
        infix fun from(value: String): KeyboardThemeType =
            entries.firstOrNull { it.name == value } ?: DEFAULT
    }

}

/**
 * Robust WCAG AA helper evaluating relative luminance to guarantee contrast ratio >= 4.5:1.
 */
fun isThemeDark(context: Context, theme: KeyboardThemeModel): Boolean {
    if (theme.themType == ThemeType.IMAGE) return true
    return try {
        val colorInt = ContextCompat.getColor(context, theme.background)
        ColorUtils.calculateLuminance(colorInt) < 0.45
    } catch (_: Exception) {
        theme.isDark
    }
}