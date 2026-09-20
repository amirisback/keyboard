package com.frogobox.appkeyboard.ui.keyboard.root

import com.frogobox.appkeyboard.model.KeyboardFeatureType

/**
 * Single source of truth reactive state for active keyboard panel in [com.frogobox.appkeyboard.services.KeyboardIME].
 */
enum class KeyboardPanelState {
    MAIN,
    EMOJI,
    AUTO_TEXT,
    TEMPLATE_TEXT_GAME,
    TEMPLATE_TEXT_APP,
    TEMPLATE_TEXT_SALE,
    TEMPLATE_TEXT_LOVE,
    TEMPLATE_TEXT_GREETING,
    NEWS,
    MOVIE,
    WEBVIEW,
    FORM;

    val isTemplate: Boolean
        get() = this in TEMPLATE_STATES

    val templateFeatureType: KeyboardFeatureType?
        get() = when (this) {
            TEMPLATE_TEXT_GAME -> KeyboardFeatureType.TEMPLATE_TEXT_GAME
            TEMPLATE_TEXT_APP -> KeyboardFeatureType.TEMPLATE_TEXT_APP
            TEMPLATE_TEXT_SALE -> KeyboardFeatureType.TEMPLATE_TEXT_SALE
            TEMPLATE_TEXT_LOVE -> KeyboardFeatureType.TEMPLATE_TEXT_LOVE
            TEMPLATE_TEXT_GREETING -> KeyboardFeatureType.TEMPLATE_TEXT_GREETING
            else -> null
        }

    companion object {
        private val TEMPLATE_STATES = setOf(
            TEMPLATE_TEXT_GAME,
            TEMPLATE_TEXT_APP,
            TEMPLATE_TEXT_SALE,
            TEMPLATE_TEXT_LOVE,
            TEMPLATE_TEXT_GREETING
        )

        fun fromFeature(featureType: KeyboardFeatureType): KeyboardPanelState? {
            return when (featureType) {
                KeyboardFeatureType.AUTO_TEXT -> AUTO_TEXT
                KeyboardFeatureType.TEMPLATE_TEXT_GAME -> TEMPLATE_TEXT_GAME
                KeyboardFeatureType.TEMPLATE_TEXT_APP -> TEMPLATE_TEXT_APP
                KeyboardFeatureType.TEMPLATE_TEXT_SALE -> TEMPLATE_TEXT_SALE
                KeyboardFeatureType.TEMPLATE_TEXT_LOVE -> TEMPLATE_TEXT_LOVE
                KeyboardFeatureType.TEMPLATE_TEXT_GREETING -> TEMPLATE_TEXT_GREETING
                KeyboardFeatureType.NEWS -> NEWS
                KeyboardFeatureType.MOVIE -> MOVIE
                KeyboardFeatureType.WEB -> WEBVIEW
                KeyboardFeatureType.FORM -> FORM
                KeyboardFeatureType.SUGGESTION,
                KeyboardFeatureType.CHANGE_KEYBOARD,
                KeyboardFeatureType.SETTING -> null
            }
        }
    }
}
