package com.frogobox.appkeyboard.model

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
    private val label: String,
    private val desc: String,
    private val themeType: ThemeType,
    private val background: Int,
) {
    DEFAULT("Default", "Classic Adaptive", ThemeType.COLOR, R.color.color_bg_keyboard_default),
    PURPLE("Frogo Purple", "Signature Brand", ThemeType.COLOR, R.color.color_bg_keyboard_purple),
    DARK("Midnight AMOLED", "Deep OLED Black", ThemeType.COLOR, R.color.color_bg_keyboard_dark),
    BLUE("Ocean Blue", "Calm & Focused", ThemeType.COLOR, R.color.color_bg_keyboard_blue),
    GREEN("Forest Emerald", "Natural Harmony", ThemeType.COLOR, R.color.color_bg_keyboard_green),
    RED("Crimson Sunset", "Vibrant Warmth", ThemeType.COLOR, R.color.color_bg_keyboard_red),
    ORANGE("Sunset Orange", "Energetic Twilight", ThemeType.COLOR, R.color.color_bg_keyboard_orange),
    CYAN("Nordic Cyan", "Fresh & Clean", ThemeType.COLOR, R.color.color_bg_keyboard_cyan),
    PINK("Sakura Pink", "Aesthetic Pastel", ThemeType.COLOR, R.color.color_bg_keyboard_pink),
    YELLOW("Amber Gold", "Golden Accent", ThemeType.COLOR, R.color.color_bg_keyboard_yellow),
    IMAGE_BG_DARK("Wallpaper", "Sample Artwork", ThemeType.IMAGE, R.drawable.ic_wallpaper_dummy);

    fun mapToModel(): KeyboardThemeModel {
        return KeyboardThemeModel(
            this.label,
            this.desc,
            this.themeType,
            this.background
        )
    }

    companion object {
        infix fun from(value: String): KeyboardThemeType =
            entries.firstOrNull { it.name == value } ?: DEFAULT
    }

}