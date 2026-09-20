package com.frogobox.appkeyboard.ui.sound

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.frogobox.appkeyboard.common.base.BaseComposeActivity
import com.frogobox.libkeyboard.common.sound.MechanicalSoundType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoundActivity : BaseComposeActivity() {

    private val viewModel: SoundViewModel by viewModels()

    private var soundEnabled by mutableStateOf(true)
    private var selectedSoundType by mutableStateOf(MechanicalSoundType.CHERRY_MX_BLUE)
    private var soundVolume by mutableIntStateOf(80)
    private var vibrateEnabled by mutableStateOf(true)

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        viewModel.soundEnabled.observe(this) { soundEnabled = it }
        viewModel.selectedSoundType.observe(this) { selectedSoundType = it }
        viewModel.soundVolume.observe(this) { soundVolume = it }
        viewModel.vibrateEnabled.observe(this) { vibrateEnabled = it }
        viewModel.loadSettings()
    }

    @Composable
    override fun Content() {
        SoundScreen(
            soundEnabled = soundEnabled,
            selectedSoundType = selectedSoundType,
            soundVolume = soundVolume,
            vibrateEnabled = vibrateEnabled,
            onSoundEnabledChange = { viewModel.setSoundEnabled(it) },
            onSoundTypeSelected = { viewModel.setSoundType(it) },
            onVolumeChange = { viewModel.setSoundVolume(it) },
            onVibrateChange = { viewModel.setVibrateEnabled(it) },
            onTestKeyTap = { viewModel.playTestKeySound() },
            onBackClick = { finish() }
        )
    }
}
