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


enum class KeyboardThemeType(
    private val label: String,
    private val desc: String,
    private val background: Int,
) {
    DEFAULT("Default", "Default Color", R.color.color_bg_keyboard_default),
    RED("Red", "Red Color", R.color.color_bg_keyboard_red),
    GREEN("Green", "Green Color", R.color.color_bg_keyboard_green),
    YELLOW("Yellow", "Yellow Color", R.color.color_bg_keyboard_yellow),
    BLUE("Blue", "Blue Color", R.color.color_bg_keyboard_blue),
    IMAGE_BG_DARK("Image", "Sample Wallpaper", R.drawable.ic_wallpaper_dummy)
    ;

    fun mapToModel(): KeyboardThemeModel {
        return KeyboardThemeModel(
            this.label,
            this.desc,
            this.background
        )
    }

    companion object {
        infix fun from(value: String): KeyboardThemeType = entries.firstOrNull { it.name == value } ?: DEFAULT
    }

}