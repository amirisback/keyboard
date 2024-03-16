package com.frogobox.appkeyboard.model

import androidx.annotation.Keep
import com.frogobox.appkeyboard.R

/**
 * Created by Faisal Amir on 10/03/23
 * https://github.com/amirisback
 */

@Keep
enum class KeyboardFeatureType(val id: String, val text: String, val icon: Int) {
    NEWS("menu_news", "News API", R.drawable.ic_menu_news),
    MOVIE("menu_movie", "Movie API", R.drawable.ic_menu_movie),
    WEB("menu_web", "Search Website", R.drawable.ic_menu_website),
    FORM("menu_form", "Form", R.drawable.ic_menu_form),
    AUTO_TEXT("menu_auto_text", "Auto Text", R.drawable.ic_menu_auto_text),
    TEMPLATE_TEXT_APP("menu_play_store_app", "App Review", R.drawable.ic_menu_ps_app),
    TEMPLATE_TEXT_GAME("menu_play_store_game", "Game Review", R.drawable.ic_menu_ps_game),
    TEMPLATE_TEXT_SALE("menu_template_text_sale", "Sale Admin", R.drawable.ic_menu_ps_sale),
    TEMPLATE_TEXT_LOVE("menu_template_text_love", "Love Emoji", R.drawable.ic_menu_ps_love),
    TEMPLATE_TEXT_GREETING("menu_template_text_greeting", "Greeting", R.drawable.ic_menu_ps_greeting),
    CHANGE_KEYBOARD("menu_change_keyboard", "Change Keyboard", R.drawable.ic_menu_keyboard),
    SETTING("menu_setting", "Setting", R.drawable.ic_menu_setting);

    fun mapToModel(): KeyboardFeatureModel {
        return KeyboardFeatureModel(
            this.id,
            this.text,
            this.icon
        )
    }

    companion object {
        infix fun from(value: String): KeyboardFeatureType =
            entries.firstOrNull { it.id == value } ?: AUTO_TEXT
    }

}