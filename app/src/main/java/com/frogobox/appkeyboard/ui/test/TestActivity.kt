package com.frogobox.appkeyboard.ui.test

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.frogobox.appkeyboard.common.base.BaseComposeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestActivity : BaseComposeActivity() {

    companion object {
        private val TAG: String = TestActivity::class.java.simpleName
    }

    private val viewModel: TestViewModel by viewModels()

    private var isUsingFrogoKeyboard by mutableStateOf(false)
    private var isFrogoKeyboardEnabled by mutableStateOf(false)

    private val dummyOptions = listOf(
        "Kuningan",
        "Menteng",
        "Pegangsaan",
        "Kebon Jeruk",
        "Kemang",
        "Senayan",
        "Sudirman",
        "Thamrin"
    )

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        if (savedInstanceState == null) {
            Log.d(TAG, "Initialized TestActivity with ViewModel: ${viewModel::class.java.simpleName}")
        }
        updateKeyboardStatus()
    }

    override fun onResume() {
        super.onResume()
        updateKeyboardStatus()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            updateKeyboardStatus()
        }
    }

    private fun isUsingKeyboard(): Boolean {
        val currentKeyboard = Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        val targetKeyboard = "$packageName/com.frogobox.appkeyboard.services.KeyboardIME"
        return currentKeyboard == targetKeyboard
    }

    private fun isKeyboardEnabled(): Boolean {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val enabledKeyboards = inputMethodManager.enabledInputMethodList
        return enabledKeyboards.any {
            it.serviceInfo.packageName == packageName
        }
    }

    private fun updateKeyboardStatus() {
        isUsingFrogoKeyboard = isUsingKeyboard()
        isFrogoKeyboardEnabled = isKeyboardEnabled()
    }

    @Composable
    override fun Content() {
        val sandboxText by viewModel.sandboxText.collectAsState()
        val metrics by viewModel.metrics.collectAsState()
        val activeTab by viewModel.activeTab.collectAsState()
        val autoTextList by viewModel.autoTextList.collectAsState()

        TestScreen(
            sandboxText = sandboxText,
            metrics = metrics,
            activeTab = activeTab,
            autoTextList = autoTextList,
            isKeyboardActive = isUsingFrogoKeyboard,
            dummyOptions = dummyOptions,
            onTextChanged = viewModel::onTextChanged,
            onInsertText = viewModel::onInsertText,
            onClearText = viewModel::onClearText,
            onResetTimer = viewModel::onResetTimer,
            onTabSelected = viewModel::onTabSelected,
            onChangeKeyboard = {
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
            },
            onGoToSettings = {
                val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            },
            onBackClick = { finish() }
        )
    }
}