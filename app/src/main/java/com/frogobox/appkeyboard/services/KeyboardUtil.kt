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
    }

    private fun getStateToggle(key: String) : Boolean {
        return pref.loadPrefBoolean(key, true)
    }

    fun menuToggle(): List<KeyboardFeatureModel> {
        return listOf(
            KeyboardFeatureType.AUTO_TEXT.mapToModel(),
            KeyboardFeatureType.TEMPLATE_TEXT_APP.mapToModel(),
            KeyboardFeatureType.TEMPLATE_TEXT_GAME.mapToModel(),
            KeyboardFeatureType.TEMPLATE_TEXT_LOVE.mapToModel(),
            KeyboardFeatureType.TEMPLATE_TEXT_GREETING.mapToModel(),
            KeyboardFeatureType.NEWS.mapToModel(),
            KeyboardFeatureType.MOVIE.mapToModel(),
            KeyboardFeatureType.WEB.mapToModel(),
            KeyboardFeatureType.FORM.mapToModel(),
            KeyboardFeatureType.CHANGE_KEYBOARD.mapToModel(),
            KeyboardFeatureType.SETTING.mapToModel(),
        ).sortedBy { getStateToggle(it.id) }
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
        return listOf(
            KeyboardThemeType.DEFAULT.mapToModel(),
            KeyboardThemeType.RED.mapToModel(),
            KeyboardThemeType.GREEN.mapToModel(),
            KeyboardThemeType.YELLOW.mapToModel(),
            KeyboardThemeType.BLUE.mapToModel(),
            KeyboardThemeType.IMAGE_BG_DARK.mapToModel()
        )
    }

}