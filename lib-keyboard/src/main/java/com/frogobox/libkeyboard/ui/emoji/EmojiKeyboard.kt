package com.frogobox.libkeyboard.ui.emoji

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.inputmethod.InputConnection
import android.widget.FrameLayout
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.emoji2.text.EmojiCompat
import com.frogobox.libkeyboard.ui.main.OnKeyboardActionListener
import com.frogobox.libkeyboard.ui.theme.FrogoLibKeyboardTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Modern Jetpack Compose-based Emoji Keyboard panel.
 *
 * Hosts [EmojiKeyboardScreen] within a ComposeView, supporting
 * responsive emoji grids, category tabs, and input connection commits.
 */
class EmojiKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val emojiCache = mutableMapOf<String, List<String>>()
    private val systemFontPaint = Paint().apply {
        typeface = Typeface.DEFAULT
    }
    private var emojiCompatMetadataVersion = 0

    private val emojisState = MutableStateFlow<List<String>>(emptyList())
    private val selectedCategoryState = MutableStateFlow(EmojiCategoryType.GENERAL)
    private val isLoadingState = MutableStateFlow(false)
    private var resetScrollTrigger by mutableIntStateOf(0)

    var currentInputConnection: InputConnection? = null
    var mOnKeyboardActionListener: OnKeyboardActionListener? = null
    var onBackClick: (() -> Unit)? = null

    private val composeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FrogoLibKeyboardTheme {
                val emojis by emojisState.collectAsState()
                val selectedCategory by selectedCategoryState.collectAsState()
                val isLoading by isLoadingState.collectAsState()
                val gridState = rememberLazyGridState()

                LaunchedEffect(resetScrollTrigger) {
                    if (resetScrollTrigger > 0) {
                        gridState.scrollToItem(0)
                    }
                }

                EmojiKeyboardScreen(
                    emojis = emojis,
                    selectedCategory = selectedCategory,
                    categories = getEmojiCategory(),
                    isLoading = isLoading,
                    lazyGridState = gridState,
                    onCategorySelected = { category ->
                        selectedCategoryState.value = category
                        loadEmojis(category.path)
                        resetScroll()
                    },
                    onEmojiClicked = { emoji ->
                        currentInputConnection?.commitText(emoji, 1)
                        mOnKeyboardActionListener?.onText(emoji)
                        mOnKeyboardActionListener?.onActionUp()
                    },
                    onBackClicked = {
                        onBackClick?.invoke()
                    }
                )
            }
        }
    }

    init {
        addView(composeView)
        openEmojiPalette()
    }

    /**
     * Assigns the active [InputConnection] for committing typed emojis.
     */
    fun setInputConnection(inputConnection: InputConnection?) {
        currentInputConnection = inputConnection
    }

    /**
     * Registers a listener for back navigation clicks from the emoji header.
     */
    fun setOnBackClickListener(listener: () -> Unit) {
        onBackClick = listener
    }

    /**
     * Opens the emoji palette at the default category.
     */
    fun openEmojiPalette() {
        selectedCategoryState.value = EmojiCategoryType.GENERAL
        loadEmojis(EmojiCategoryType.GENERAL.path)
    }

    /**
     * Clears cached emoji lists from memory.
     */
    fun clearEmojiCache() {
        emojiCache.clear()
    }

    /**
     * Smoothly or instantly scrolls the emoji grid back to item 0.
     */
    fun resetScroll() {
        resetScrollTrigger++
    }

    private fun loadEmojis(path: String) {
        val cached = emojiCache[path]
        if (cached != null) {
            emojisState.value = cached
            return
        }

        isLoadingState.value = true
        scope.launch(Dispatchers.IO) {
            val fullEmojiList = parseRawEmojiSpecsFile(context, path)

            val isEmojiCompatReady = EmojiCompat.isConfigured() &&
                    EmojiCompat.get().loadState == EmojiCompat.LOAD_STATE_SUCCEEDED

            val emojis = fullEmojiList.filter { emoji ->
                systemFontPaint.hasGlyph(emoji) || (isEmojiCompatReady &&
                        EmojiCompat.get().getEmojiMatch(
                            emoji,
                            emojiCompatMetadataVersion
                        ) == EmojiCompat.EMOJI_SUPPORTED)
            }

            emojiCache[path] = emojis

            withContext(Dispatchers.Main) {
                emojisState.value = emojis
                isLoadingState.value = false
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job.cancelChildren()
        currentInputConnection = null
    }

    fun onDestroy() {
        job.cancelChildren()
        currentInputConnection = null
        clearEmojiCache()
    }

}