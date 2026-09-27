package com.frogobox.appkeyboard.ui.keyboard.autotext

import com.frogobox.appkeyboard.model.KeyboardFeatureType

/**
 * Sub-menu categories for consolidated AutoText keyboard panel.
 * Combines user custom snippets with built-in template categories.
 */
enum class AutoTextCategory(val id: String, val title: String, val icon: String) {
    MY_CUSTOM("cat_my_custom", "My Custom", "✏️"),
    GAME("cat_game", "Game", "🎮"),
    APP("cat_app", "App", "📱"),
    SALE("cat_sale", "Sale", "💰"),
    GREETING("cat_greeting", "Greeting", "👋"),
    LOVE("cat_love", "Love", "❤️");

    val isCustom: Boolean
        get() = this == MY_CUSTOM

    val templateFeatureType: KeyboardFeatureType?
        get() = when (this) {
            GAME -> KeyboardFeatureType.TEMPLATE_TEXT_GAME
            APP -> KeyboardFeatureType.TEMPLATE_TEXT_APP
            SALE -> KeyboardFeatureType.TEMPLATE_TEXT_SALE
            GREETING -> KeyboardFeatureType.TEMPLATE_TEXT_GREETING
            LOVE -> KeyboardFeatureType.TEMPLATE_TEXT_LOVE
            MY_CUSTOM -> null
        }

    companion object {
        fun fromFeatureType(featureType: KeyboardFeatureType): AutoTextCategory {
            return when (featureType) {
                KeyboardFeatureType.TEMPLATE_TEXT_GAME -> GAME
                KeyboardFeatureType.TEMPLATE_TEXT_APP -> APP
                KeyboardFeatureType.TEMPLATE_TEXT_SALE -> SALE
                KeyboardFeatureType.TEMPLATE_TEXT_GREETING -> GREETING
                KeyboardFeatureType.TEMPLATE_TEXT_LOVE -> LOVE
                else -> MY_CUSTOM
            }
        }
    }
}
