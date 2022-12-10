package com.frogobox.keyboard.ui.keyboard.emoji

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.frogobox.keyboard.core.BaseKeyboard
import com.frogobox.keyboard.databinding.KeyboardEmojiBinding

/**
 * Created by Faisal Amir on 11/12/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

class EmojiKeyboard(
    context: Context,
    attrs: AttributeSet?,
) : BaseKeyboard<KeyboardEmojiBinding>(context, attrs) {

    override fun setupViewBinding(): KeyboardEmojiBinding {
        return KeyboardEmojiBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun onCreate() {
    }

}