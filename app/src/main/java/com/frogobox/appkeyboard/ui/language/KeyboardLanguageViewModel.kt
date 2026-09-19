package com.frogobox.appkeyboard.ui.language

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.frogobox.appkeyboard.common.base.BaseViewModel
import com.frogobox.appkeyboard.services.KeyboardUtil
import com.frogobox.coresdk.response.FrogoStateResponse
import com.frogobox.sdk.delegate.preference.PreferenceDelegates
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */


@HiltViewModel
class KeyboardLanguageViewModel @Inject constructor(
    private val pref: PreferenceDelegates
) : BaseViewModel() {

    private var _keyboardLanguage = MutableLiveData<List<KeyboardLanguage>>()
    var keyboardLanguage: LiveData<List<KeyboardLanguage>> = _keyboardLanguage

    private var _showloading = MutableLiveData<Boolean>()
    var showloading: LiveData<Boolean> = _showloading

    fun getKeyboardLanguage(context: Context) {
        val list = mutableListOf<KeyboardLanguage>()
        list.add(
            KeyboardLanguage(
                name = "${context.getString(com.frogobox.libkeyboard.R.string.translation_english)} (QWERTY)",
                xml = com.frogobox.libkeyboard.R.xml.keys_letters_qwerty,
                layoutType = "QWERTY",
                code = "EN",
                script = "Latin"
            )
        )
        list.add(
            KeyboardLanguage(
                name = "Programmer / Coding (QWERTY)",
                xml = com.frogobox.libkeyboard.R.xml.keys_letters_programmer,
                layoutType = "Codeboard",
                code = "DEV",
                script = "Code & Symbols"
            )
        )
        list.add(
            KeyboardLanguage(
                name = "${context.getString(com.frogobox.libkeyboard.R.string.translation_english)} (QWERTZ)",
                xml = com.frogobox.libkeyboard.R.xml.keys_letters_english_qwertz,
                layoutType = "QWERTZ",
                code = "EN",
                script = "Latin"
            )
        )
        list.add(
            KeyboardLanguage(
                name = "${context.getString(com.frogobox.libkeyboard.R.string.translation_english)} (DVORAK)",
                xml = com.frogobox.libkeyboard.R.xml.keys_letters_english_dvorak,
                layoutType = "DVORAK",
                code = "EN",
                script = "Latin"
            )
        )

        list.add(
            KeyboardLanguage(
                name = context.getString(com.frogobox.libkeyboard.R.string.translation_bengali),
                xml = com.frogobox.libkeyboard.R.xml.keys_letters_bengali,
                layoutType = "Standard",
                code = "BN",
                script = "বাংলা (Bengali)"
            )
        )
        list.add(
            KeyboardLanguage(
                name = context.getString(com.frogobox.libkeyboard.R.string.translation_bulgarian),
                xml = com.frogobox.libkeyboard.R.xml.keys_letters_bulgarian,
                layoutType = "Standard",
                code = "BG",
                script = "Български (Cyrillic)"
            )
        )

        list.add(
            KeyboardLanguage(
                name = context.getString(com.frogobox.libkeyboard.R.string.translation_french),
                xml = com.frogobox.libkeyboard.R.xml.keys_letters_french,
                layoutType = "AZERTY",
                code = "FR",
                script = "Français (Latin)"
            )
        )
        list.add(
            KeyboardLanguage(
                name = context.getString(com.frogobox.libkeyboard.R.string.translation_german),
                xml = com.frogobox.libkeyboard.R.xml.keys_letters_german,
                layoutType = "QWERTZ",
                code = "DE",
                script = "Deutsch (Latin)"
            )
        )
        list.add(
            KeyboardLanguage(
                name = context.getString(com.frogobox.libkeyboard.R.string.translation_greek),
                xml = com.frogobox.libkeyboard.R.xml.keys_letters_greek,
                layoutType = "Standard",
                code = "EL",
                script = "Ελληνικά (Greek)"
            )
        )
        list.add(
            KeyboardLanguage(
                name = context.getString(com.frogobox.libkeyboard.R.string.translation_lithuanian),
                xml = com.frogobox.libkeyboard.R.xml.keys_letters_lithuanian,
                layoutType = "ĄŽERTY",
                code = "LT",
                script = "Lietuvių (Baltic)"
            )
        )
        list.add(
            KeyboardLanguage(
                name = context.getString(com.frogobox.libkeyboard.R.string.translation_romanian),
                xml = com.frogobox.libkeyboard.R.xml.keys_letters_romanian,
                layoutType = "QWERTY",
                code = "RO",
                script = "Română (Latin)"
            )
        )
        list.add(
            KeyboardLanguage(
                name = context.getString(com.frogobox.libkeyboard.R.string.translation_russian),
                xml = com.frogobox.libkeyboard.R.xml.keys_letters_russian,
                layoutType = "ЙЦУКЕН",
                code = "RU",
                script = "Русский (Cyrillic)"
            )
        )
        list.add(
            KeyboardLanguage(
                name = context.getString(com.frogobox.libkeyboard.R.string.translation_slovenian),
                xml = com.frogobox.libkeyboard.R.xml.keys_letters_slovenian,
                layoutType = "QWERTZ",
                code = "SL",
                script = "Slovenščina (Latin)"
            )
        )
        list.add(
            KeyboardLanguage(
                name = context.getString(com.frogobox.libkeyboard.R.string.translation_spanish),
                xml = com.frogobox.libkeyboard.R.xml.keys_letters_spanish_qwerty,
                layoutType = "QWERTY",
                code = "ES",
                script = "Español (Latin)"
            )
        )
        list.add(
            KeyboardLanguage(
                name = "${context.getString(com.frogobox.libkeyboard.R.string.translation_turkish)} (Q)",
                xml = com.frogobox.libkeyboard.R.xml.keys_letters_turkish_q,
                layoutType = "QWERTY",
                code = "TR",
                script = "Türkçe (Latin)"
            )
        )
        list.add(
            KeyboardLanguage(
                name = context.getString(com.frogobox.libkeyboard.R.string.translation_persian),
                xml = com.frogobox.libkeyboard.R.xml.keys_letter_persian,
                layoutType = "Standard",
                code = "FA",
                script = "فارسی (Perso-Arabic)",
                isRtl = true
            )
        )
        _keyboardLanguage.postValue(list)
    }

    fun setKeyboard(xml: Int, onSuccess: () -> Unit) {
        _showloading.postValue(true)
        pref.savePrefInt(KeyboardUtil.KEYBOARD_TYPE, xml)
        _showloading.postValue(false)
        onSuccess()
    }

    fun checkKeyboardType(xml: Int): Boolean {
        return pref.getPrefInt(
            KeyboardUtil.KEYBOARD_TYPE,
            com.frogobox.libkeyboard.R.xml.keys_letters_qwerty
        ) == xml
    }

    fun getActiveKeyboardXml(): Int {
        return pref.getPrefInt(
            KeyboardUtil.KEYBOARD_TYPE,
            com.frogobox.libkeyboard.R.xml.keys_letters_qwerty
        )
    }

}