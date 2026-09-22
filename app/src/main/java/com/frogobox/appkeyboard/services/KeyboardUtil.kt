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

        val DEFAULT_FEATURE_TYPES = listOf(
            KeyboardFeatureType.SUGGESTION,
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