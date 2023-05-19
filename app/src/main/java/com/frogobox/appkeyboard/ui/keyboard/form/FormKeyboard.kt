package com.frogobox.appkeyboard.ui.keyboard.form

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import com.frogobox.appkeyboard.databinding.KeyboardFormBinding
import com.frogobox.libkeyboard.common.core.BaseKeyboard

/**
 * Created by Faisal Amir on 07/11/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

class FormKeyboard(
    context: Context,
    attrs: AttributeSet?,
) : BaseKeyboard<KeyboardFormBinding>(context, attrs) {

    override fun setupViewBinding(): KeyboardFormBinding {
        return KeyboardFormBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun onCreate() {
        initView()
    }

    private fun initView() {
        binding?.apply {
            Log.d("FormKeyboard", "initView: ${etText.onCreateInputConnection(EditorInfo())}")
        }
    }

}