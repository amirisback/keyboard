package com.frogobox.appkeyboard.ui.toggle

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.frogobox.appkeyboard.common.base.BaseComposeActivity
import com.frogobox.appkeyboard.model.KeyboardFeatureModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ToggleActivity : BaseComposeActivity() {

    private val viewModel: ToggleViewModel by viewModels()

    private var featureList by mutableStateOf<List<KeyboardFeatureModel>>(emptyList())
    private val toggleStates = mutableStateMapOf<String, Boolean>()

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        viewModel.keyboardFeatureState.observe(this) { list ->
            featureList = list
            list.forEach { feature ->
                toggleStates[feature.id] = viewModel.getSwitchToggle(feature.id)
            }
        }
        viewModel.getKeyboardFeatureData()
    }

    @Composable
    override fun Content() {
        ToggleScreen(
            features = featureList,
            getToggleState = { id -> toggleStates[id] ?: viewModel.getSwitchToggle(id) },
            onToggleChanged = { id, isChecked ->
                toggleStates[id] = isChecked
                viewModel.switchToggle(id, isChecked)
            },
            onBackClick = { finish() }
        )
    }
}