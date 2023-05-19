package com.frogobox.appkeyboard.ui.language

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.frogobox.appkeyboard.common.base.BaseViewModel
import com.frogobox.appkeyboard.util.Constant
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
    private val pref : PreferenceDelegates
) : BaseViewModel() {

    private var _keyboardLanguage = MutableLiveData<List<KeyboardLanguage>>()
    var keyboardLanguage: LiveData<List<KeyboardLanguage>> = _keyboardLanguage

    private var _showloading = MutableLiveData<Boolean>()
    var showloading: LiveData<Boolean> = _showloading

    fun getKeyboardLanguage(context: Context) {
        val list = mutableListOf<KeyboardLanguage>()
        list.add(KeyboardLanguage("${context.getString(com.frogobox.libkeyboard.R.string.translation_english)} (QWERTY)", com.frogobox.libkeyboard.R.xml.keys_letters_qwerty))
        list.add(KeyboardLanguage("${context.getString(com.frogobox.libkeyboard.R.string.translation_english)} (QWERTZ)", com.frogobox.libkeyboard.R.xml.keys_letters_english_qwertz))
        list.add(KeyboardLanguage("${context.getString(com.frogobox.libkeyboard.R.string.translation_english)} (DVORAK)", com.frogobox.libkeyboard.R.xml.keys_letters_english_dvorak))

        list.add(KeyboardLanguage(context.getString(com.frogobox.libkeyboard.R.string.translation_bengali), com.frogobox.libkeyboard.R.xml.keys_letters_bengali))
        list.add(KeyboardLanguage(context.getString(com.frogobox.libkeyboard.R.string.translation_bulgarian), com.frogobox.libkeyboard.R.xml.keys_letters_bulgarian))

        list.add(KeyboardLanguage(context.getString(com.frogobox.libkeyboard.R.string.translation_french), com.frogobox.libkeyboard.R.xml.keys_letters_french))
        list.add(KeyboardLanguage(context.getString(com.frogobox.libkeyboard.R.string.translation_german), com.frogobox.libkeyboard.R.xml.keys_letters_german))
        list.add(KeyboardLanguage(context.getString(com.frogobox.libkeyboard.R.string.translation_greek), com.frogobox.libkeyboard.R.xml.keys_letters_greek))
        list.add(KeyboardLanguage(context.getString(com.frogobox.libkeyboard.R.string.translation_lithuanian), com.frogobox.libkeyboard.R.xml.keys_letters_lithuanian))
        list.add(KeyboardLanguage(context.getString(com.frogobox.libkeyboard.R.string.translation_romanian), com.frogobox.libkeyboard.R.xml.keys_letters_romanian))
        list.add(KeyboardLanguage(context.getString(com.frogobox.libkeyboard.R.string.translation_slovenian), com.frogobox.libkeyboard.R.xml.keys_letters_slovenian))
        list.add(KeyboardLanguage(context.getString(com.frogobox.libkeyboard.R.string.translation_spanish), com.frogobox.libkeyboard.R.xml.keys_letters_spanish_qwerty))
        list.add(KeyboardLanguage("${context.getString(com.frogobox.libkeyboard.R.string.translation_turkish)} (Q)", com.frogobox.libkeyboard.R.xml.keys_letters_turkish_q))
        _keyboardLanguage.postValue(list)
    }

    fun setKeyboard(xml: Int, onSuccess: () -> Unit) {
        pref.savePrefInt(Constant.PREF_KEYBOARD_TYPE, xml, object : FrogoStateResponse {
            override fun onFailed(statusCode: Int, errorMessage: String) {}
            override fun onFinish() {}
            override fun onHideProgress() {
                _showloading.postValue(false)
            }
            override fun onShowProgress() {
                _showloading.postValue(true)
            }
            override fun onSuccess() {
                onSuccess()
            }
        })
    }

    fun checkKeyboardType(xml: Int) : Boolean {
        return pref.loadPrefInt(Constant.PREF_KEYBOARD_TYPE, com.frogobox.libkeyboard.R.xml.keys_letters_qwerty) == xml
    }

}