package com.frogobox.appkeyboard.ui.keyboard.dummy

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.frogobox.appkeyboard.databinding.KeyboardDummyBinding
import com.frogobox.libkeyboard.common.core.BaseKeyboard

class DummyKeyboard(
    context: Context,
    attrs: AttributeSet?,
) : BaseKeyboard<KeyboardDummyBinding>(context, attrs) {

    override fun setupViewBinding(): KeyboardDummyBinding {
        return KeyboardDummyBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun onCreate() {
        initView()
    }

    private fun initView() {
        binding?.apply {

        }
    }


}