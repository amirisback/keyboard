package com.frogobox.research.ui.keyboard.webview

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.webkit.WebView

/**
 * Created by Faisal Amir on 08/11/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

class MiniWebviewKeyboard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : WebView(context, attrs) {

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        val ic = BaseInputConnection(this, true)
        outAttrs!!.inputType = InputType.TYPE_CLASS_NUMBER
        return ic
    }

}