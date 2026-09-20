package com.frogobox.appkeyboard.ui.language

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.frogobox.appkeyboard.common.base.BaseComposeActivity
import com.frogobox.sdk.ext.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KeyboardLanguageActivity : BaseComposeActivity() {

    private val viewModel: KeyboardLanguageViewModel by viewModels()

    private var languageList by mutableStateOf<List<KeyboardLanguage>>(emptyList())
    private var activeLanguageXml by mutableIntStateOf(0)

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        viewModel.keyboardLanguage.observe(this) {
            languageList = it
            val active = it.firstOrNull { lang -> viewModel.checkKeyboardType(lang.xml) }
            activeLanguageXml = active?.xml ?: 0
        }
        viewModel.getKeyboardLanguage(this)
    }

    @Composable
    override fun Content() {
        KeyboardLanguageScreen(
            languageList = languageList,
            activeLanguageXml = activeLanguageXml,
            checkIsSelected = { xml -> xml == activeLanguageXml },
            onApplyLanguage = { language ->
                viewModel.setKeyboard(language.xml) {
                    activeLanguageXml = language.xml
                    showToast("${language.name} Language Applied")
                    viewModel.getKeyboardLanguage(this@KeyboardLanguageActivity)
                }
            },
            onBackClick = { finish() }
        )
    }
}