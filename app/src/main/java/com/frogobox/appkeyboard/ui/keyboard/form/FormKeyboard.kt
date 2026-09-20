package com.frogobox.appkeyboard.ui.keyboard.form

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.InputConnection
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.frogobox.appkeyboard.ui.theme.compose.FrogoKeyboardTheme

/**
 * Modern Jetpack Compose-based Quick Form Keyboard panel.
 */
class FormKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var currentInputConnection: InputConnection? = null
    var onBackClick: (() -> Unit)? = null

    private val composeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FrogoKeyboardTheme {
                FormKeyboardScreen(
                    onCommitText = { text ->
                        currentInputConnection?.commitText(text, 1)
                    },
                    onBackClick = {
                        onBackClick?.invoke()
                    }
                )
            }
        }
    }

    init {
        addView(composeView)
    }

    fun setInputConnection(inputConnection: InputConnection?) {
        currentInputConnection = inputConnection
    }

    fun setOnBackClickListener(listener: () -> Unit) {
        onBackClick = listener
    }

}