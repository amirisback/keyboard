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
                R.drawable.ic_auto_text
            ),
            KeyboardHeaderData(
                KeyboardHeaderType.NEWS,
                R.drawable.ic_keyboard_news
            ),
            KeyboardHeaderData(
                KeyboardHeaderType.MOVIE,
                R.drawable.ic_keyboard_movie
            ),
            KeyboardHeaderData(
                KeyboardHeaderType.WEB,
                R.drawable.ic_search
            ),
            KeyboardHeaderData(
                KeyboardHeaderType.FORM,
                R.drawable.ic_keyboard
            ),
        )
    }

}