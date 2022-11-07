package com.frogobox.research.core

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.InputConnection
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding

/**
 * Created by Faisal Amir on 07/11/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

abstract class BaseKeyboard<T : ViewBinding>(
    context: Context,
    attrs: AttributeSet?,
) : ConstraintLayout(context, attrs) {

    var binding: T? = null

    protected lateinit var currentInputConnection : InputConnection

    abstract fun setupViewBinding(): T

    abstract fun onCreate()

    init {
        binding = setupViewBinding()
        onCreate()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs) {
        binding = setupViewBinding()
        onCreate()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
    ) : this(context, attrs, defStyleAttr) {
        binding = setupViewBinding()
        onCreate()
    }

    fun setInputConnection(inputConnection: InputConnection) {
        currentInputConnection = inputConnection
    }

}