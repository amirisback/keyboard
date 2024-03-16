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
    DEFAULT("Default", "Default Color", ThemeType.COLOR, R.color.color_bg_keyboard_default),
    RED("Red", "Red Color", ThemeType.COLOR, R.color.color_bg_keyboard_red),
    GREEN("Green", "Green Color", ThemeType.COLOR, R.color.color_bg_keyboard_green),
    YELLOW("Yellow", "Yellow Color", ThemeType.COLOR, R.color.color_bg_keyboard_yellow),
    BLUE("Blue", "Blue Color", ThemeType.COLOR, R.color.color_bg_keyboard_blue),
    IMAGE_BG_DARK("Image", "Sample Wallpaper", ThemeType.IMAGE, R.drawable.ic_wallpaper_dummy);

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