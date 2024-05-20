package com.frogobox.appkeyboard.ui.theme

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.frogobox.appkeyboard.R
import com.frogobox.appkeyboard.common.base.BaseViewModel
import com.frogobox.appkeyboard.model.KeyboardThemeModel
import com.frogobox.appkeyboard.services.KeyboardUtil
import com.frogobox.coresdk.response.FrogoStateResponse
import com.frogobox.sdk.delegate.preference.PreferenceDelegates
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by faisalamircs on 16/02/2024
 * -----------------------------------------
 * Name     : Muhammad Faisal Amir
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 */


@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val pref: PreferenceDelegates,
    private val keyboardUtil: KeyboardUtil
) : BaseViewModel() {

    private var _keyboardThemeState = MutableLiveData<List<KeyboardThemeModel>>()
    var keyboardThemeState: LiveData<List<KeyboardThemeModel>> = _keyboardThemeState

    fun getThemeData() {
        _keyboardThemeState.postValue(keyboardUtil.keyboardTheme())
    }

    fun getThemeColor(): Int {
        return pref.getPrefInt(KeyboardUtil.KEYBOARD_COLOR, R.color.color_bg_keyboard_default)
    }

    fun setThemeColor(data: KeyboardThemeModel, onSuccess: () -> Unit) {
        pref.savePrefString(KeyboardUtil.KEYBOARD_COLOR_TYPE, data.themType.name)
        pref.savePrefInt(KeyboardUtil.KEYBOARD_COLOR, data.background)
        onSuccess()
    }

}