package com.frogobox.appkeyboard.ui.keyboard.templatetext

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.InputConnection
import android.widget.FrameLayout
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.ui.theme.compose.FrogoKeyboardTheme
import kotlinx.coroutines.flow.MutableStateFlow

data class TemplateCategoryItem(
    val type: KeyboardFeatureType,
    val icon: String,
    val title: String,
    val isSelected: Boolean = false
)

/**
 * Modern Jetpack Compose-based TemplateText Keyboard panel.
 */
class TemplateTextKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        val CATEGORIES = listOf(
            Pair(KeyboardFeatureType.TEMPLATE_TEXT_GAME, Pair("🎮", "Game")),
            Pair(KeyboardFeatureType.TEMPLATE_TEXT_APP, Pair("📱", "App")),
            Pair(KeyboardFeatureType.TEMPLATE_TEXT_SALE, Pair("💰", "Sale")),
            Pair(KeyboardFeatureType.TEMPLATE_TEXT_GREETING, Pair("👋", "Greeting")),
            Pair(KeyboardFeatureType.TEMPLATE_TEXT_LOVE, Pair("❤️", "Love"))
        )
    }

    private val currentCategoryState = MutableStateFlow(KeyboardFeatureType.TEMPLATE_TEXT_GAME)

    var currentInputConnection: InputConnection? = null
    var onBackClick: (() -> Unit)? = null

    private val composeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FrogoKeyboardTheme {
                val category by currentCategoryState.collectAsState()
                TemplateTextKeyboardScreen(
                    initialType = category,
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

    fun setupTemplateTextType(templateTextType: KeyboardFeatureType) {
        currentCategoryState.value = templateTextType
    }

    fun setInputConnection(inputConnection: InputConnection?) {
        currentInputConnection = inputConnection
    }

    fun setOnBackClickListener(listener: () -> Unit) {
        onBackClick = listener
    }

}