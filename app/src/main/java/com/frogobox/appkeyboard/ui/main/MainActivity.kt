package com.frogobox.appkeyboard.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.frogobox.libkeyboard.common.ext.isDarkThemeOn
import com.frogobox.appkeyboard.databinding.ActivityMainBinding
import com.frogobox.appkeyboard.ui.autotext.AutoTextActivity
import com.frogobox.appkeyboard.ui.detail.DetailActivity
import com.frogobox.appkeyboard.ui.language.KeyboardLanguageActivity
import com.frogobox.appkeyboard.ui.toggle.ToggleActivity
import com.frogobox.sdk.ext.showLogDebug
import com.frogobox.sdk.ext.startActivityExt
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseMainActivity<ActivityMainBinding>() {

    private val NONE = 0
    private val PICKING = 1
    private val CHOSEN = 2

    private var mState = 0

    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }

    private val viewModel: MainViewModel by viewModels()

    override fun setupViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun setupViewModel() {
        super.setupViewModel()
        viewModel.apply {

        }
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        if (savedInstanceState == null) {
            // Call View Model Here
            Log.d(TAG, "View Model : ${viewModel::class.java.simpleName}")
        }
        // TODO : Add your code here

        Log.d(TAG, "isDarkThemeOn : ${isDarkThemeOn()}")
        initView()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (mState === PICKING) {
            mState = CHOSEN
        } else if (mState === CHOSEN) {
            handlingState()
        }
    }

    override fun onResume() {
        super.onResume()
        handlingState()
    }

    override fun initView() {
        super.initView()
        binding.apply {
            handlingState()

            btnChangeKeyboard.setOnClickListener {
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
                mState = PICKING
            }

            btnGoToSetting.setOnClickListener {
                Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(this)
                }
            }

            btnDoSomeTest.setOnClickListener {
                startActivityExt<DetailActivity>()
            }

            btnAutoText.setOnClickListener {
                startActivityExt<AutoTextActivity>()
            }

            btnToggle.setOnClickListener {
                startActivityExt<ToggleActivity>()
            }

            btnMultiLanguage.setOnClickListener {
                startActivityExt<KeyboardLanguageActivity>()
            }

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

    private fun handlingState() {
        binding.titleState.apply {
            if (!isKeyboardEnabled()) {
                text = "Frogo Keyboard Not Active"
                setTextColor(ContextCompat.getColor(this@MainActivity, com.frogobox.libkeyboard.R.color.color_failed))
            } else {
                if (isUsingKeyboard()) {
                    text = "Frogo Keyboard Active"
                    setTextColor(ContextCompat.getColor(this@MainActivity, com.frogobox.libkeyboard.R.color.color_success))
                } else {
                    text = "Not Using Frogo Keyboard"
                    setTextColor(ContextCompat.getColor(this@MainActivity, com.frogobox.libkeyboard.R.color.color_warning))
                }
            }
        }
    }

}