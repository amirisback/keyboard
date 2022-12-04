package com.frogobox.keyboard.ui.keyboard.form

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import com.frogobox.keyboard.MainApp
import com.frogobox.keyboard.R
import com.frogobox.keyboard.core.BaseKeyboard
import com.frogobox.keyboard.databinding.KeyboardFormBinding

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

            val dummy = listOf("Kuningan",
                "Menteng",
                "Menten213g",
                "Men123teng",
                "Mw23423",
                "Me123nteng",
                "Mente234234234ng",
                "Pegangsaan"
            )
            val adapterS =
                ArrayAdapter(MainApp.getContext(), R.layout.item_spinner, R.id.tv_text, dummy)
            etText.setAdapter(adapterS)

            etText.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) etText.showDropDown()
            }

        }
    }

}