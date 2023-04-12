package com.frogobox.keyboard.model

/**
 * Created by Faisal Amir on 10/03/23
 * https://github.com/amirisback
 */


enum class KeyboardFeatureType(val id: String, val title: String) {
    NEWS("menu_news","News API"),
    MOVIE("menu_movie","Movie API"),
    WEB("menu_web","Search Website"),
    FORM("menu_form","Form"),
    AUTO_TEXT("menu_auto_text","Auto Text"),
    TEMPLATE_TEXT_APP("menu_play_store_app","Game Review"),
    TEMPLATE_TEXT_GAME("menu_play_store_game","App Review"),
    TEMPLATE_TEXT_SALE("menu_template_text_sale","Sale Admin"),
    TEMPLATE_TEXT_GREETING("menu_template_text_greeting", "Greeting")
}