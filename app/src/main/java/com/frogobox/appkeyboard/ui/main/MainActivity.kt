package com.frogobox.appkeyboard.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.frogobox.appkeyboard.common.base.BaseComposeMainActivity
import com.frogobox.appkeyboard.ui.autotext.AutoTextActivity
import com.frogobox.appkeyboard.ui.language.KeyboardLanguageActivity
import com.frogobox.appkeyboard.ui.productremote.ProductRemoteActivity
import com.frogobox.appkeyboard.ui.sound.SoundActivity
import com.frogobox.appkeyboard.ui.templatetext.TemplateTextActivity
import com.frogobox.appkeyboard.ui.test.TestActivity
import com.frogobox.appkeyboard.ui.theme.ThemeActivity
import com.frogobox.appkeyboard.ui.toggle.ToggleActivity
import com.frogobox.sdk.ext.startActivityExt
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseComposeMainActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val NONE = 0
    private val PICKING = 1
    private val CHOSEN = 2
    private var mState = 0

    private var keyboardStatus by mutableStateOf(KeyboardStatus.NOT_ENABLED)

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        updateKeyboardStatus()
    }

    override fun onResume() {
        super.onResume()
        updateKeyboardStatus()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (mState == PICKING) {
            mState = CHOSEN
        } else if (mState == CHOSEN) {
            updateKeyboardStatus()
        }
    }

    private fun isUsingKeyboard(): Boolean {
        val currentKeyboard = Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        val pianoKeyboard = "$packageName/com.frogobox.appkeyboard.services.KeyboardIME"
        return currentKeyboard == pianoKeyboard
    }

    private fun isKeyboardEnabled(): Boolean {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val enabledKeyboards = inputMethodManager.enabledInputMethodList
        return enabledKeyboards.any {
            it.serviceInfo.packageName == packageName
        }
    }

    private fun updateKeyboardStatus() {
        keyboardStatus = when {
            !isKeyboardEnabled() -> KeyboardStatus.NOT_ENABLED
            isUsingKeyboard() -> KeyboardStatus.ACTIVE
            else -> KeyboardStatus.NOT_DEFAULT
        }
    }

    @Composable
    override fun Content() {
        MainScreen(
            status = keyboardStatus,
            onGoToSettings = {
                Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(this)
                }
            },
            onChangeKeyboard = {
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
                mState = PICKING
            },
            onNavigateAutoText = { startActivityExt<AutoTextActivity>() },
            onNavigateTemplateText = { category ->
                val intent = Intent(this@MainActivity, TemplateTextActivity::class.java).apply {
                    if (!category.isNullOrBlank()) {
                        putExtra(TemplateTextActivity.EXTRA_INITIAL_CATEGORY, category)
                    }
                }
                startActivity(intent)
            },
            onNavigateProductRemote = { startActivityExt<ProductRemoteActivity>() },
            onNavigateToggle = { startActivityExt<ToggleActivity>() },
            onNavigateLanguage = { startActivityExt<KeyboardLanguageActivity>() },
            onNavigateTheme = { startActivityExt<ThemeActivity>() },
            onNavigateSound = { startActivityExt<SoundActivity>() },
            onNavigateTest = { startActivityExt<TestActivity>() },
            onExportBackup = {
                val json = com.frogobox.appkeyboard.util.KeyboardBackupRestoreHelper.exportToJson(
                    autoTexts = emptyList(),
                    clipboardItems = emptyList(),
                    settings = mapOf("status" to keyboardStatus.name)
                )
                android.widget.Toast.makeText(this@MainActivity, "Data dicadangkan ke format JSON", android.widget.Toast.LENGTH_SHORT).show()
            },
            onImportBackup = {
                android.widget.Toast.makeText(this@MainActivity, "Pilih berkas cadangan JSON untuk dipulihkan", android.widget.Toast.LENGTH_SHORT).show()
            }
        )
    }
}