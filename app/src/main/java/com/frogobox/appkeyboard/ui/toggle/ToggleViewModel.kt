package com.frogobox.appkeyboard.ui.toggle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.frogobox.appkeyboard.common.base.BaseViewModel
import com.frogobox.appkeyboard.model.KeyboardFeatureModel
import com.frogobox.appkeyboard.services.KeyboardUtil
import com.frogobox.sdk.delegate.preference.PreferenceDelegates
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by faisalamircs on 16/03/2024
 * -----------------------------------------
 * Name     : Muhammad Faisal Amir
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 */


@HiltViewModel
class ToggleViewModel @Inject constructor(
    private val pref: PreferenceDelegates,
    private val keyboardUtil: KeyboardUtil
) : BaseViewModel() {

    private var _keyboardFeatureState = MutableLiveData<List<KeyboardFeatureModel>>()
    var keyboardFeatureState: LiveData<List<KeyboardFeatureModel>> = _keyboardFeatureState

    fun switchToggle(key: String, value: Boolean) {
        pref.savePrefBoolean(key, value)
    }

    fun getSwitchToggle(key: String): Boolean {
        return pref.getPrefBoolean(key, true)
    }

    fun getKeyboardFeatureData() {
        _keyboardFeatureState.postValue(keyboardUtil.menuToggle())
    }

}