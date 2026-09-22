package com.frogobox.appkeyboard.model

import androidx.annotation.Keep

/**
 * Standardized category definition for keyboard template texts.
 */
@Keep
enum class TemplateCategoryType(
    val key: String,
    val title: String,
    val icon: String,
    val featureType: KeyboardFeatureType
) {
    GAME("GAME", "Game", "🎮", KeyboardFeatureType.TEMPLATE_TEXT_GAME),
    APP("APP", "App", "📱", KeyboardFeatureType.TEMPLATE_TEXT_APP),
    SALE("SALE", "Sale", "💰", KeyboardFeatureType.TEMPLATE_TEXT_SALE),
    GREETING("GREETING", "Greeting", "👋", KeyboardFeatureType.TEMPLATE_TEXT_GREETING),
    LOVE("LOVE", "Love", "❤️", KeyboardFeatureType.TEMPLATE_TEXT_LOVE);

    companion object {
        fun fromKey(key: String?): TemplateCategoryType {
            if (key.isNullOrBlank()) return GAME
            return entries.firstOrNull {
                it.key.equals(key, ignoreCase = true) ||
                        it.name.equals(key, ignoreCase = true) ||
                        it.featureType.name.equals(key, ignoreCase = true) ||
                        it.title.equals(key, ignoreCase = true)
            } ?: GAME
        }

        fun fromFeatureType(featureType: KeyboardFeatureType): TemplateCategoryType {
            return entries.firstOrNull { it.featureType == featureType } ?: GAME
        }
    }
}
