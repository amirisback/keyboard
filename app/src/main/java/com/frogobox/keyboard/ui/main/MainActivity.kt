package com.frogobox.keyboard.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.frogobox.keyboard.R
import com.frogobox.keyboard.core.BaseActivity
import com.frogobox.keyboard.databinding.ActivityMainBinding
import com.frogobox.keyboard.ext.isDarkThemeOn
import com.frogobox.keyboard.ui.detail.DetailActivity
import com.frogobox.sdk.ext.showLogDebug
import com.frogobox.sdk.ext.startActivityExt

class MainActivity : BaseActivity<ActivityMainBinding>() {

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

        }
    }

    private fun isUsingKeyboard(): Boolean {
        val currentKeyboard = Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val enabledKeyboards = inputMethodManager.enabledInputMethodList
        val check = enabledKeyboards.find {
            it.settingsActivity == MainActivity::class.java.canonicalName
        }
        return if (isKeyboardEnabled()) {
            check?.id == currentKeyboard
        } else {
            false
        }
    }

    private fun isKeyboardEnabled(): Boolean {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val enabledKeyboards = inputMethodManager.enabledInputMethodList
        return enabledKeyboards.any {
            it.settingsActivity == MainActivity::class.java.canonicalName
        }
    }

    private fun checkKeyboard() {
        val currentKeyboard = Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val enabledKeyboards = inputMethodManager.inputMethodList
        showLogDebug("Enabled Keyboards         : $currentKeyboard")
        enabledKeyboards.forEachIndexed { index, it ->
            showLogDebug("Index                     : $index")
            showLogDebug("ID                        : ${it.id}")
            showLogDebug("Class name                : ${it.component.className}")
            showLogDebug("Package Name              : ${it.component.packageName}")
            showLogDebug("Short Class Name          : ${it.component.shortClassName}")
            showLogDebug("Settings Activity         : ${it.settingsActivity}")
            showLogDebug("Service Name              : ${it.serviceName}")
            showLogDebug("Service Info Name         : ${it.serviceInfo.name}")
            showLogDebug("Service Info Package Name : ${it.serviceInfo.packageName}")
            showLogDebug("Service Info Package Flag : ${it.serviceInfo.flags}")
            showLogDebug("-----------------------------------------------------")
        }
    }

    private fun handlingState() {
        checkKeyboard()
        binding.titleState.apply {
            if (!isKeyboardEnabled()) {
                text = "Frogo Keyboard Not Active"
                setTextColor(ContextCompat.getColor(this@MainActivity, R.color.redSecondary))
            } else {
                if (isUsingKeyboard()) {
                    text = "Frogo Keyboard Active"
                    setTextColor(ContextCompat.getColor(this@MainActivity, R.color.color_success))
                } else {
                    text = "Not Using Frogo Keyboard"
                    setTextColor(ContextCompat.getColor(this@MainActivity, R.color.color_primary))
                }
            }
        }
    }

}