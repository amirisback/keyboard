package com.frogobox.appkeyboard.services

import com.frogobox.appkeyboard.R
import com.frogobox.appkeyboard.model.KeyboardFeature
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.sdk.delegate.preference.PreferenceDelegatesImpl
import org.koin.java.KoinJavaComponent.inject

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */

class KeyboardUtil {

    private val pref: PreferenceDelegatesImpl by inject(PreferenceDelegatesImpl::class.java)

    fun menuToggle(): List<KeyboardFeature> {
        return listOf(
            KeyboardFeature(
                KeyboardFeatureType.AUTO_TEXT.id,
                KeyboardFeatureType.AUTO_TEXT,
                R.drawable.ic_menu_auto_text,
                pref.loadPrefBoolean(KeyboardFeatureType.AUTO_TEXT.id, true)
            ),
            KeyboardFeature(
                KeyboardFeatureType.TEMPLATE_TEXT_APP.id,
                KeyboardFeatureType.TEMPLATE_TEXT_APP,
                R.drawable.ic_menu_ps_app,
                pref.loadPrefBoolean(KeyboardFeatureType.TEMPLATE_TEXT_APP.id, true)
            ),
            KeyboardFeature(
                KeyboardFeatureType.TEMPLATE_TEXT_GAME.id,
                KeyboardFeatureType.TEMPLATE_TEXT_GAME,
                R.drawable.ic_menu_ps_game,
                pref.loadPrefBoolean(KeyboardFeatureType.TEMPLATE_TEXT_GAME.id, true)
            ),
            KeyboardFeature(
                KeyboardFeatureType.TEMPLATE_TEXT_LOVE.id,
                KeyboardFeatureType.TEMPLATE_TEXT_LOVE,
                R.drawable.ic_menu_ps_love,
                pref.loadPrefBoolean(KeyboardFeatureType.TEMPLATE_TEXT_LOVE.id, true)
            ),
            KeyboardFeature(
                KeyboardFeatureType.TEMPLATE_TEXT_GREETING.id,
                KeyboardFeatureType.TEMPLATE_TEXT_GREETING,
                R.drawable.ic_menu_ps_greeting,
                pref.loadPrefBoolean(KeyboardFeatureType.TEMPLATE_TEXT_GREETING.id, true)
            ),
            KeyboardFeature(
                KeyboardFeatureType.TEMPLATE_TEXT_SALE.id,
                KeyboardFeatureType.TEMPLATE_TEXT_SALE,
                R.drawable.ic_menu_ps_sale,
                pref.loadPrefBoolean(KeyboardFeatureType.TEMPLATE_TEXT_SALE.id, true)
            ),
            KeyboardFeature(
                KeyboardFeatureType.NEWS.id,
                KeyboardFeatureType.NEWS,
                R.drawable.ic_menu_news,
                pref.loadPrefBoolean(KeyboardFeatureType.NEWS.id, true)
            ),
            KeyboardFeature(
                KeyboardFeatureType.MOVIE.id,
                KeyboardFeatureType.MOVIE,
                R.drawable.ic_menu_movie,
                pref.loadPrefBoolean(KeyboardFeatureType.MOVIE.id, true)
            ),
            KeyboardFeature(
                KeyboardFeatureType.WEB.id,
                KeyboardFeatureType.WEB,
                R.drawable.ic_menu_website,
                pref.loadPrefBoolean(KeyboardFeatureType.WEB.id, true)
            ),
            KeyboardFeature(
                KeyboardFeatureType.FORM.id,
                KeyboardFeatureType.FORM,
                R.drawable.ic_menu_form,
                pref.loadPrefBoolean(KeyboardFeatureType.FORM.id, true)
            ),
            KeyboardFeature(
                KeyboardFeatureType.CHANGE_KEYBOARD.id,
                KeyboardFeatureType.CHANGE_KEYBOARD,
                R.drawable.ic_menu_keyboard,
                pref.loadPrefBoolean(KeyboardFeatureType.CHANGE_KEYBOARD.id, true)
            ),
            KeyboardFeature(
                KeyboardFeatureType.SETTING.id,
                KeyboardFeatureType.SETTING,
                R.drawable.ic_menu_setting,
                pref.loadPrefBoolean(KeyboardFeatureType.SETTING.id, true)
            )
        ).sortedBy { it.state }
    }

    fun menuKeyboard(): List<KeyboardFeature> {
        val listFeature = mutableListOf<KeyboardFeature>()
        menuToggle().forEach { data ->
            if (data.state) {
                listFeature.add(data)
            }
        }
        return listFeature
    }

}