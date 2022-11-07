package com.frogobox.research.services

import android.inputmethodservice.InputMethodService
import android.os.Build
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.frogobox.research.databinding.KeyboardImeBinding

/**
 * Created by Faisal Amir on 07/11/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

class KeyboardIME : InputMethodService() {

    companion object {
        private const val TAG = "KeyboardIME"
    }

    private var _binding: KeyboardImeBinding? = null
    private val binding: KeyboardImeBinding
        get() = _binding!!

    override fun onCreateInputView(): View {
        _binding = KeyboardImeBinding.inflate(layoutInflater, null, false)
        return binding.root
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        initView()
    }

    private fun initView() {
        binding.apply {
            keyboardMain.setInputConnection(currentInputConnection)

//            titleAnimals.setOnClickListener {
//                currentInputConnection.commitText("Animals", 2)
//            }
//
//            icKeyboard.setOnClickListener {
//                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.showInputMethodPicker()
//            }
        }
    }

    private fun switchInputMethod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            switchToPreviousInputMethod()
        } else {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.switchToLastInputMethod(window.window?.attributes?.token)
        }
    }
}