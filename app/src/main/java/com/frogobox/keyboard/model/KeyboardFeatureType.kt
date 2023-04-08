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
    PLAY_STORE_APP("menu_play_store_app","App Play Store"),
    PLAY_STORE_GAME("menu_play_store_game","Game Play Store"),
}