package com.frogobox.appkeyboard.services

import com.frogobox.appkeyboard.model.KeyboardFeatureModel
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.model.KeyboardThemeModel
import com.frogobox.appkeyboard.model.KeyboardThemeType
import com.frogobox.sdk.delegate.preference.PreferenceDelegates
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */

@Singleton
class KeyboardUtil @Inject constructor(
    private val pref: PreferenceDelegates
) {

    companion object {
        const val KEYBOARD_TYPE = "KEYBOARD_TYPE"
        const val KEYBOARD_COLOR = "KEYBOARD_COLOR"
        const val KEYBOARD_COLOR_TYPE = "KEYBOARD_COLOR_TYPE"
        const val KEYBOARD_SUGGESTION_ENABLED = "KEYBOARD_SUGGESTION_ENABLED"
        const val KEY_ALWAYS_SHOW_FEATURE = "KEY_ALWAYS_SHOW_FEATURE"
        const val KEY_FEATURE_ORDER = "KEY_FEATURE_ORDER"
        const val KEY_NUMBER_ROW_ENABLED = "KEY_NUMBER_ROW_ENABLED"
        const val KEY_ONE_HANDED_MODE = "KEY_ONE_HANDED_MODE"
        const val KEY_SYMBOL_HINTS_ENABLED = "KEY_SYMBOL_HINTS_ENABLED"
        const val KEY_DYNAMIC_THEME_ENABLED = "KEY_DYNAMIC_THEME_ENABLED"
        const val KEY_SPLIT_MODE_ENABLED = "KEY_SPLIT_MODE_ENABLED"
        const val KEY_HAPTIC_INTENSITY = "KEY_HAPTIC_INTENSITY"
        const val KEY_SMART_PUNCTUATION_ENABLED = "KEY_SMART_PUNCTUATION_ENABLED"
        const val KEY_ACTIVE_LANGUAGE = "KEY_ACTIVE_LANGUAGE"
        const val KEY_HEIGHT_SCALE = "KEY_HEIGHT_SCALE"
        const val KEY_BOTTOM_CHIN_OFFSET = "KEY_BOTTOM_CHIN_OFFSET"
        const val KEY_FLOATING_MODE_ENABLED = "KEY_FLOATING_MODE_ENABLED"
        const val KEY_HAPTIC_DURATION_MS = "KEY_HAPTIC_DURATION_MS"
        const val KEY_SOUND_VOLUME_PERCENT = "KEY_SOUND_VOLUME_PERCENT"
        const val KEY_GESTURE_TYPING_ENABLED = "KEY_GESTURE_TYPING_ENABLED"
        const val KEY_USER_DICTIONARY_LEARNING_ENABLED = "KEY_USER_DICTIONARY_LEARNING_ENABLED"
        const val KEY_SMART_CALCULATOR_ENABLED = "KEY_SMART_CALCULATOR_ENABLED"

        val DEFAULT_FEATURE_TYPES = listOf(
            KeyboardFeatureType.SUGGESTION,
            KeyboardFeatureType.NUMBER_ROW,
            KeyboardFeatureType.ONE_HANDED,
            KeyboardFeatureType.AI_ASSISTANT,
            KeyboardFeatureType.VOICE_TYPING,
            KeyboardFeatureType.AUTO_TEXT,
            KeyboardFeatureType.PRODUCT_REMOTE,
            KeyboardFeatureType.CLIPBOARD,
            KeyboardFeatureType.TEXT_EDIT,
            KeyboardFeatureType.NEWS,
            KeyboardFeatureType.MOVIE,
            KeyboardFeatureType.WEB,
            KeyboardFeatureType.FORM,
            KeyboardFeatureType.CHANGE_KEYBOARD,
            KeyboardFeatureType.SETTING
        )
    }

    fun getStateToggle(key: String): Boolean {
        return pref.getPrefBoolean(key, true)
    }

    fun isNumberRowEnabled(): Boolean {
        return pref.getPrefBoolean(KEY_NUMBER_ROW_ENABLED, false)
    }

    fun setNumberRowEnabled(enabled: Boolean) {
        pref.savePrefBoolean(KEY_NUMBER_ROW_ENABLED, enabled)
    }

    fun getOneHandedMode(): String {
        return pref.getPrefString(KEY_ONE_HANDED_MODE, "OFF")
    }

    fun setOneHandedMode(mode: String) {
        pref.savePrefString(KEY_ONE_HANDED_MODE, mode)
    }

    fun isSymbolHintsEnabled(): Boolean {
        return pref.getPrefBoolean(KEY_SYMBOL_HINTS_ENABLED, true)
    }

    fun setSymbolHintsEnabled(enabled: Boolean) {
        pref.savePrefBoolean(KEY_SYMBOL_HINTS_ENABLED, enabled)
    }

    fun isDynamicThemeEnabled(): Boolean {
        return pref.getPrefBoolean(KEY_DYNAMIC_THEME_ENABLED, false)
    }

    fun setDynamicThemeEnabled(enabled: Boolean) {
        pref.savePrefBoolean(KEY_DYNAMIC_THEME_ENABLED, enabled)
    }

    fun isSplitModeEnabled(): Boolean {
        return pref.getPrefBoolean(KEY_SPLIT_MODE_ENABLED, false)
    }

    fun setSplitModeEnabled(enabled: Boolean) {
        pref.savePrefBoolean(KEY_SPLIT_MODE_ENABLED, enabled)
    }

    fun getHapticIntensity(): String {
        return pref.getPrefString(KEY_HAPTIC_INTENSITY, "MEDIUM")
    }

    fun setHapticIntensity(intensity: String) {
        pref.savePrefString(KEY_HAPTIC_INTENSITY, intensity)
    }

    fun isSmartPunctuationEnabled(): Boolean {
        return pref.getPrefBoolean(KEY_SMART_PUNCTUATION_ENABLED, true)
    }

    fun setSmartPunctuationEnabled(enabled: Boolean) {
        pref.savePrefBoolean(KEY_SMART_PUNCTUATION_ENABLED, enabled)
    }

    fun getActiveLanguage(): String {
        return pref.getPrefString(KEY_ACTIVE_LANGUAGE, "ID")
    }

    fun setActiveLanguage(lang: String) {
        pref.savePrefString(KEY_ACTIVE_LANGUAGE, lang)
    }

    fun getHeightScale(): String {
        return pref.getPrefString(KEY_HEIGHT_SCALE, "NORMAL")
    }

    fun setHeightScale(scale: String) {
        pref.savePrefString(KEY_HEIGHT_SCALE, scale)
    }

    fun getBottomChinOffset(): String {
        return pref.getPrefString(KEY_BOTTOM_CHIN_OFFSET, "NONE")
    }

    fun setBottomChinOffset(offset: String) {
        pref.savePrefString(KEY_BOTTOM_CHIN_OFFSET, offset)
    }

    fun isFloatingModeEnabled(): Boolean {
        return pref.getPrefBoolean(KEY_FLOATING_MODE_ENABLED, false)
    }

    fun setFloatingModeEnabled(enabled: Boolean) {
        pref.savePrefBoolean(KEY_FLOATING_MODE_ENABLED, enabled)
    }

    fun getHapticDurationMs(): Long {
        return pref.getPrefString(KEY_HAPTIC_DURATION_MS, "20").toLongOrNull() ?: 20L
    }

    fun setHapticDurationMs(durationMs: Long) {
        pref.savePrefString(KEY_HAPTIC_DURATION_MS, durationMs.toString())
    }

    fun getSoundVolumePercent(): Int {
        return pref.getPrefString(KEY_SOUND_VOLUME_PERCENT, "100").toIntOrNull() ?: 100
    }

    fun setSoundVolumePercent(volume: Int) {
        pref.savePrefString(KEY_SOUND_VOLUME_PERCENT, volume.toString())
    }

    fun isGestureTypingEnabled(): Boolean {
        return pref.getPrefBoolean(KEY_GESTURE_TYPING_ENABLED, true)
    }

    fun setGestureTypingEnabled(enabled: Boolean) {
        pref.savePrefBoolean(KEY_GESTURE_TYPING_ENABLED, enabled)
    }

    fun isUserDictionaryLearningEnabled(): Boolean {
        return pref.getPrefBoolean(KEY_USER_DICTIONARY_LEARNING_ENABLED, true)
    }

    fun setUserDictionaryLearningEnabled(enabled: Boolean) {
        pref.savePrefBoolean(KEY_USER_DICTIONARY_LEARNING_ENABLED, enabled)
    }

    fun isSmartCalculatorEnabled(): Boolean {
        return pref.getPrefBoolean(KEY_SMART_CALCULATOR_ENABLED, true)
    }

    fun setSmartCalculatorEnabled(enabled: Boolean) {
        pref.savePrefBoolean(KEY_SMART_CALCULATOR_ENABLED, enabled)
    }

    fun isSuggestionEnabled(): Boolean {
        return getStateToggle(KeyboardFeatureType.SUGGESTION.id)
    }

    fun isClipboardEnabled(): Boolean {
        return getStateToggle(KeyboardFeatureType.CLIPBOARD.id)
    }

    fun getAlwaysShowFeature(): String? {
        val featureId = pref.getPrefString(KEY_ALWAYS_SHOW_FEATURE, "")
        return if (featureId.isBlank()) null else featureId
    }

    fun setAlwaysShowFeature(featureId: String?) {
        pref.savePrefString(KEY_ALWAYS_SHOW_FEATURE, featureId ?: "")
    }

    fun isAlwaysShowFeature(featureId: String): Boolean {
        return getAlwaysShowFeature() == featureId
    }

    fun getFeatureOrder(): List<String> {
        val raw = pref.getPrefString(KEY_FEATURE_ORDER, "")
        val defaultIds = DEFAULT_FEATURE_TYPES.map { it.id }
        if (raw.isBlank()) return defaultIds
        val saved = raw.split(",").filter { it.isNotBlank() }
        val validSaved = saved.filter { id -> defaultIds.contains(id) }.toMutableList()
        defaultIds.forEach { id ->
            if (!validSaved.contains(id)) {
                validSaved.add(id)
            }
        }
        return validSaved
    }

    fun saveFeatureOrder(order: List<String>) {
        pref.savePrefString(KEY_FEATURE_ORDER, order.joinToString(","))
    }

    fun resetFeatureOrder() {
        pref.savePrefString(KEY_FEATURE_ORDER, "")
    }

    fun menuToggle(): List<KeyboardFeatureModel> {
        val order = getFeatureOrder()
        val map = DEFAULT_FEATURE_TYPES.associateBy { it.id }
        return order.mapNotNull { id -> map[id]?.mapToModel() }
    }

    fun menuKeyboard(): List<KeyboardFeatureModel> {
        val listFeature = mutableListOf<KeyboardFeatureModel>()
        menuToggle().forEach { data ->
            if (getStateToggle(data.id)) {
                listFeature.add(data)
            }
        }
        return listFeature
    }

    fun keyboardTheme(): List<KeyboardThemeModel> {
        return KeyboardThemeType.entries.map { it.mapToModel() }
    }

}