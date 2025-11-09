package com.frogobox.appkeyboard.ui.keyboard.webview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.frogobox.appkeyboard.databinding.KeyboardWebviewBinding
import com.frogobox.libkeyboard.common.core.BaseKeyboard
import com.frogobox.sdk.ext.loadUrlFrogoExt

/**
 * Created by Faisal Amir on 07/11/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

class WebiewKeyboard(
    context: Context,
    attrs: AttributeSet?,
) : BaseKeyboard<KeyboardWebviewBinding>(context, attrs) {

    override fun setupViewBinding(inflater: LayoutInflater, parent: LinearLayout): KeyboardWebviewBinding {
        return KeyboardWebviewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun onCreate() {
        initView()
    }

    private fun initView() {
        binding.apply {
            webview.loadUrlFrogoExt("https://www.google.com")
        }
    }

}