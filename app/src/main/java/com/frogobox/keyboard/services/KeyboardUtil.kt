package com.frogobox.keyboard.services

import com.frogobox.keyboard.R
import com.frogobox.keyboard.model.KeyboardHeaderData
import com.frogobox.keyboard.model.KeyboardHeaderType

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */

object KeyboardUtil {

    fun menuKeyboard(): List<KeyboardHeaderData> {
        return listOf(
            KeyboardHeaderData(
                KeyboardHeaderType.AUTOTEXT,
                R.drawable.ic_keyboard_mini_auto_text
            ),
            KeyboardHeaderData(
                KeyboardHeaderType.PLAY_STORE,
                R.drawable.ic_keyboard_mini_app
            ),
            KeyboardHeaderData(
                KeyboardHeaderType.NEWS,
                R.drawable.ic_keyboard_mini_news
            ),
            KeyboardHeaderData(
                KeyboardHeaderType.MOVIE,
                R.drawable.ic_keyboard_mini_movie
            ),
            KeyboardHeaderData(
                KeyboardHeaderType.WEB,
                R.drawable.ic_keyboard_mini_search
            ),
            KeyboardHeaderData(
                KeyboardHeaderType.FORM,
                R.drawable.ic_keyboard_mini_form
            ),
        )
    }

}