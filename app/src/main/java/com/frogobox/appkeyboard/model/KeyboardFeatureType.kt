package com.frogobox.appkeyboard.model

import androidx.annotation.Keep

/**
 * Created by Faisal Amir on 10/03/23
 * https://github.com/amirisback
 */

@Keep
enum class KeyboardFeatureType(val id: String, val title: String) {
    NEWS("menu_news","News API"),
    MOVIE("menu_movie","Movie API"),
    WEB("menu_web","Search Website"),
    FORM("menu_form","Form"),
    AUTO_TEXT("menu_auto_text","Auto Text"),
    TEMPLATE_TEXT_APP("menu_play_store_app","App Review"),
    TEMPLATE_TEXT_GAME("menu_play_store_game","Game Review"),
    TEMPLATE_TEXT_SALE("menu_template_text_sale","Sale Admin"),
    TEMPLATE_TEXT_LOVE("menu_template_text_love","Love Emoji"),
    TEMPLATE_TEXT_GREETING("menu_template_text_greeting", "Greeting"),
    CHANGE_KEYBOARD("menu_change_keyboard","Change Keyboard"),
    SETTING("menu_setting","Setting")
}