package com.frogobox.appkeyboard.ui.keyboard.form

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.frogobox.appkeyboard.databinding.KeyboardFormBinding
import com.frogobox.appkeyboard.ui.keyboard.autotext.AutoTextKeyboardViewModel
import com.frogobox.appkeyboard.ui.keyboard.movie.MovieKeyboard
import com.frogobox.appkeyboard.util.KeyboardNavigationHelper
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

    private val destinationKeyboard = MovieKeyboard(context, attrs)

    override fun setupViewBinding(inflater: LayoutInflater, parent: LinearLayout): KeyboardFormBinding {
        return KeyboardFormBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun initUI() {
        super.initUI()
        binding.apply {
            btnSubmit.setOnClickListener {
                KeyboardNavigationHelper.navigateTo(this@FormKeyboard, destinationKeyboard)
            }
        }
    }

}