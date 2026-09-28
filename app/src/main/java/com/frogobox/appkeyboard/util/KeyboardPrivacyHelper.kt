package com.frogobox.appkeyboard.util

import android.text.InputType
import android.view.inputmethod.EditorInfo

/**
 * Utility helper for detecting password fields, incognito mode, and private browsing sessions.
 * Ensures zero credential leakage, suppresses predictive word generation, and isolates clipboard history.
 */
object KeyboardPrivacyHelper {

    /**
     * Checks if the given [EditorInfo] represents a password or PIN field.
     */
    fun isPasswordField(info: EditorInfo?): Boolean {
        if (info == null) return false
        val inputType = info.inputType
        val inputClass = inputType and InputType.TYPE_MASK_CLASS
        val variation = inputType and InputType.TYPE_MASK_VARIATION

        val isTextPassword = inputClass == InputType.TYPE_CLASS_TEXT && (
            variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
            variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
            variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
        )

        val isNumberPassword = inputClass == InputType.TYPE_CLASS_NUMBER && (
            variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD
        )

        return isTextPassword || isNumberPassword
    }

    /**
     * Checks if the target field requested no personalized learning (e.g. Incognito browser tab).
     */
    fun isNoPersonalizedLearning(info: EditorInfo?): Boolean {
        if (info == null) return false
        return (info.imeOptions and EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING) != 0
    }

    /**
     * Combines password field checks and no-personalized-learning checks to determine
     * whether the keyboard should operate in Incognito / Privacy Guard mode.
     */
    fun isIncognitoOrPassword(info: EditorInfo?): Boolean {
        return isPasswordField(info) || isNoPersonalizedLearning(info)
    }
}
