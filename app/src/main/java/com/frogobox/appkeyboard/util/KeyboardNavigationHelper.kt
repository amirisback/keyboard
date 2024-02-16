package com.frogobox.appkeyboard.util

import android.view.ViewGroup
import com.frogobox.libkeyboard.common.core.BaseKeyboard

class KeyboardNavigationHelper {
    companion object {
        fun navigateTo(baseKeyboard: BaseKeyboard<*>, destinationKeyboard: BaseKeyboard<*>) {
            baseKeyboard.parent?.let {
                if (it is ViewGroup) {
                    it.removeView(baseKeyboard)
                }
            }

            destinationKeyboard.parent?.let {
                if (it is ViewGroup) {
                    it.addView(destinationKeyboard)
                }
            }
        }
    }
}