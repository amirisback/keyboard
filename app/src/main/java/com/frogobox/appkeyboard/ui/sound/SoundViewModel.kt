package com.frogobox.appkeyboard.ui.sound

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.frogobox.libkeyboard.common.sound.MechanicalSoundManager
import com.frogobox.libkeyboard.common.sound.MechanicalSoundType
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard
import com.frogobox.sdk.delegate.preference.PreferenceDelegates
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SoundViewModel @Inject constructor(
    application: Application,
    private val pref: PreferenceDelegates
) : AndroidViewModel(application) {

    private val _soundEnabled = MutableLiveData<Boolean>()
    val soundEnabled: LiveData<Boolean> = _soundEnabled

    private val _selectedSoundType = MutableLiveData<MechanicalSoundType>()
    val selectedSoundType: LiveData<MechanicalSoundType> = _selectedSoundType

    private val _soundVolume = MutableLiveData<Int>()
    val soundVolume: LiveData<Int> = _soundVolume

    private val _vibrateEnabled = MutableLiveData<Boolean>()
    val vibrateEnabled: LiveData<Boolean> = _vibrateEnabled

    init {
        loadSettings()
    }

    fun loadSettings() {
        val soundOn = pref.getPrefBoolean(MechanicalSoundManager.PREF_KEYBOARD_SOUND_ENABLED, true)
        val soundTypeId = pref.getPrefString(
            MechanicalSoundManager.PREF_KEYBOARD_SOUND_TYPE,
            MechanicalSoundType.CHERRY_MX_BLUE.id
        )
        val volume = pref.getPrefInt(MechanicalSoundManager.PREF_KEYBOARD_SOUND_VOLUME, 80)
        val vibrateOn = pref.getPrefBoolean(MechanicalSoundManager.PREF_KEYBOARD_VIBRATE_ENABLED, true)

        _soundEnabled.value = soundOn
        _selectedSoundType.value = MechanicalSoundType.fromId(soundTypeId)
        _soundVolume.value = volume
        _vibrateEnabled.value = vibrateOn

        ItemMainKeyboard.SOUND_ON_KEYPRESS = soundOn
        ItemMainKeyboard.MECHANICAL_SOUND_TYPE = soundTypeId
        ItemMainKeyboard.SOUND_VOLUME = volume / 100f
        ItemMainKeyboard.VIBRATE_ON_KEYPRESS = vibrateOn
    }

    fun setSoundEnabled(enabled: Boolean) {
        pref.savePrefBoolean(MechanicalSoundManager.PREF_KEYBOARD_SOUND_ENABLED, enabled)
        ItemMainKeyboard.SOUND_ON_KEYPRESS = enabled
        _soundEnabled.value = enabled
    }

    fun setSoundType(type: MechanicalSoundType) {
        pref.savePrefString(MechanicalSoundManager.PREF_KEYBOARD_SOUND_TYPE, type.id)
        ItemMainKeyboard.MECHANICAL_SOUND_TYPE = type.id
        _selectedSoundType.value = type
        playTestKeySound()
    }

    fun setSoundVolume(volume: Int) {
        pref.savePrefInt(MechanicalSoundManager.PREF_KEYBOARD_SOUND_VOLUME, volume)
        ItemMainKeyboard.SOUND_VOLUME = volume / 100f
        _soundVolume.value = volume
    }

    fun setVibrateEnabled(enabled: Boolean) {
        pref.savePrefBoolean(MechanicalSoundManager.PREF_KEYBOARD_VIBRATE_ENABLED, enabled)
        ItemMainKeyboard.VIBRATE_ON_KEYPRESS = enabled
        _vibrateEnabled.value = enabled
    }

    fun playTestKeySound() {
        val isSoundOn = _soundEnabled.value ?: true
        if (!isSoundOn) return
        val soundType = _selectedSoundType.value ?: MechanicalSoundType.CHERRY_MX_BLUE
        val volume = (_soundVolume.value ?: 80) / 100f
        MechanicalSoundManager.getInstance(getApplication()).playKeySound(soundType, volume)
    }
}
