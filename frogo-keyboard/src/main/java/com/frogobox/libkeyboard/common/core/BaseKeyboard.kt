package com.frogobox.libkeyboard.common.core

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import androidx.viewbinding.ViewBinding

/**
 * Base class for all custom keyboard layouts.
 *
 * Provides:
 * - Automatic [ViewBinding] inflation
 * - Safe initialization through [onCreate]
 * - Controlled [InputConnection] management
 * - Optional lifecycle-safe cleanup via [onDestroy]
 *
 * @param VB The [ViewBinding] type for this keyboard.
 *
 * Created by Faisal Amir on 07/11/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 *
 */
abstract class BaseKeyboard<VB : ViewBinding> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    // Backing property for view binding
    private var _binding: VB? = null
    val binding: VB
        get() = _binding ?: throw IllegalStateException("Binding is accessed before setup or after onDestroy()")

    // Current input connection to communicate with the EditText / InputMethod
    var currentInputConnection: InputConnection? = null
        private set

    /**
     * Inflate and return the [ViewBinding] for this keyboard.
     * Typically use: `MyKeyboardBinding.inflate(LayoutInflater.from(context), this, true)`
     */
    protected abstract fun setupViewBinding(inflater: LayoutInflater, parent: LinearLayout): VB

    /**
     * Called once after the binding has been created and attached.
     * Use this to initialize your views and listeners.
     */
    protected open fun onCreate() {
        initUI()
        initData()
    }

    /**
     * Called when the keyboard is being destroyed or detached.
     * Override to release resources or remove callbacks.
     */
    protected open fun onDestroy() {}

    open fun initUI() {}
    open fun initData() {}

    init {
        orientation = VERTICAL
        initialize()
    }

    private fun initialize() {
        _binding = setupViewBinding(LayoutInflater.from(context), this)
        onCreate()
    }

    /**
     * Assigns the active [InputConnection].
     * This allows sending key events or text input to the connected field.
     */
    fun setInputConnection(inputConnection: InputConnection?) {
        currentInputConnection = inputConnection
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onDestroy()
        _binding = null
        currentInputConnection = null
    }
}
