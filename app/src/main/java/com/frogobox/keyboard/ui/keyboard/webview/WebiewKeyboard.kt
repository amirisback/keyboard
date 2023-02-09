package com.frogobox.keyboard.ui.keyboard.webview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.frogobox.keyboard.common.base.BaseKeyboard
import com.frogobox.keyboard.databinding.KeyboardWebviewBinding
import com.frogobox.sdk.ext.loadUrlExt

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

    override fun setupViewBinding(): KeyboardWebviewBinding {
        return KeyboardWebviewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun onCreate() {
        initView()
    }

    private fun initView() {
        binding?.apply {
            webview.loadUrlExt("https://www.google.com")
        }
    }

}