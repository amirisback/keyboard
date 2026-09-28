package com.frogobox.appkeyboard.ui.keyboard.root

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.ClipboardItem
import com.frogobox.appkeyboard.model.KeyboardFeatureModel
import com.frogobox.appkeyboard.model.ThemeType
import com.frogobox.appkeyboard.suggestion.SuggestionResult
import com.frogobox.appkeyboard.ui.keyboard.ai.KeyboardAiAssistantScreen
import com.frogobox.appkeyboard.ui.keyboard.autotext.AutoTextKeyboardScreen
import com.frogobox.appkeyboard.ui.keyboard.clipboard.ClipboardKeyboardScreen
import com.frogobox.appkeyboard.ui.keyboard.form.FormKeyboardScreen
import com.frogobox.appkeyboard.ui.keyboard.movie.MovieKeyboardScreen
import com.frogobox.appkeyboard.ui.keyboard.news.NewsKeyboardScreen
import com.frogobox.appkeyboard.ui.keyboard.productremote.ProductRemoteKeyboardScreen
import com.frogobox.appkeyboard.ui.keyboard.textedit.TextEditAction
import com.frogobox.appkeyboard.ui.keyboard.textedit.TextEditKeyboardScreen
import com.frogobox.appkeyboard.ui.keyboard.webview.WebviewKeyboardScreen
import com.frogobox.coreutil.movie.model.TrendingMovie
import com.frogobox.coreutil.news.model.Article
import com.frogobox.libkeyboard.ui.emoji.EmojiCategory
import com.frogobox.libkeyboard.ui.emoji.EmojiCategoryType
import com.frogobox.libkeyboard.ui.emoji.EmojiKeyboardScreen
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard
import com.frogobox.libkeyboard.ui.main.MainKeyboard
import com.frogobox.libkeyboard.ui.main.MainKeyboardView
import com.frogobox.libkeyboard.ui.main.OnKeyboardActionListener

/**
 * Root composable for KeyboardIME unifying the entire keyboard window:
 * - Dynamic background rendering (Solid color vs Scrimmed wallpaper image)
 * - Animated Top Bar transitioning between [KeyboardFeatureHeader] and [KeyboardSuggestionBar] (180ms)
 * - Smooth [Crossfade] (180ms) panel navigation across QWERTY and feature overlay screens
 */
@Composable
fun KeyboardImeRootScreen(
    activePanelState: KeyboardPanelState,
    themeType: ThemeType,
    themeBackgroundRes: Int,
    features: List<KeyboardFeatureModel>,
    onFeatureClick: (KeyboardFeatureModel) -> Unit,
    isSuggestionVisible: Boolean,
    suggestionResult: SuggestionResult,
    onCandidateSelected: (String, CandidateType) -> Unit,
    onSwitchSuggestionMenu: () -> Unit,
    onCloseSuggestion: () -> Unit,
    currentKeyboard: ItemMainKeyboard?,
    onKeyboardActionListener: OnKeyboardActionListener?,
    onMainKeyboardInit: (MainKeyboard) -> Unit,
    autoTextList: List<AutoTextEntity>,
    onManageAutoText: () -> Unit,
    productRemoteItems: List<DataItemResponse> = emptyList(),
    isProductRemoteLoading: Boolean = false,
    productRemoteError: String? = null,
    onRefreshProductRemote: () -> Unit = {},
    onManageProductRemote: () -> Unit = {},
    newsArticles: List<Article>,
    isNewsLoading: Boolean,
    movieList: List<TrendingMovie>,
    isMovieLoading: Boolean,
    emojis: List<String>,
    selectedEmojiCategory: EmojiCategoryType,
    emojiCategories: List<EmojiCategory>,
    isEmojiLoading: Boolean,
    onSelectEmojiCategory: (EmojiCategoryType) -> Unit,
    onEmojiClicked: (String) -> Unit,
    onCommitText: (String) -> Unit,
    onBackToMain: () -> Unit,
    onRegisterKeyConsumer: (((Int, Boolean) -> Boolean)?) -> Unit = {},
    clipboardItems: List<ClipboardItem> = emptyList(),
    onTogglePinClipboardItem: (String) -> Unit = {},
    onDeleteClipboardItem: (String) -> Unit = {},
    onClearClipboardHistory: () -> Unit = {},
    recentClip: String? = null,
    onQuickPaste: (String) -> Unit = {},
    isSelectionMode: Boolean = false,
    onTextEditAction: (TextEditAction) -> Unit = {},
    alwaysShowFeatureId: String? = null,
    onToggleAlwaysShowFeature: ((String) -> Unit)? = null,
    onDeleteEmoji: (() -> Unit)? = null,
    isIncognitoMode: Boolean = false,
    isNumberRowEnabled: Boolean = false,
    onToggleNumberRow: () -> Unit = {},
    oneHandedMode: String = "OFF",
    onChangeOneHandedMode: (String) -> Unit = {},
    isDynamicThemeEnabled: Boolean = false,
    onNumberRowClick: ((Int) -> Unit)? = null,
    aiAssistantInitialText: String = "",
    onAiApplyText: (String) -> Unit = {},
    onAiCopyText: (String) -> Unit = {},
    isVoiceTypingActive: Boolean = false,
    isVoiceListening: Boolean = false,
    voiceAmplitude: Float = 0f,
    voiceStatusText: String = "",
    voiceErrorMessage: String? = null,
    onStopVoiceTyping: () -> Unit = {},
    onCloseVoiceTyping: () -> Unit = {},
    textExpansionMatch: com.frogobox.appkeyboard.util.InlineTextExpanderHelper.TextExpansionMatch? = null,
    onExpansionSelected: ((com.frogobox.appkeyboard.util.InlineTextExpanderHelper.TextExpansionMatch) -> Unit)? = null,
    mathCalculationResult: com.frogobox.appkeyboard.util.SmartCalculatorHelper.MathResult? = null,
    onMathResultSelected: ((com.frogobox.appkeyboard.util.SmartCalculatorHelper.MathResult) -> Unit)? = null,
    isSplitModeEnabled: Boolean = false,
    onToggleSplitMode: () -> Unit = {},
    activeLanguage: String = "ID",
    onToggleLanguage: () -> Unit = {},
    isFloatingMode: Boolean = false,
    onDockFloatingKeyboard: () -> Unit = {},
    bottomChinOffsetDp: Int = 0,
    hasImeError: Boolean = false,
    imeErrorMessage: String? = null,
    onResetImeError: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDarkTheme = remember(themeType, themeBackgroundRes) {
        if (themeType == ThemeType.IMAGE) {
            true
        } else {
            try {
                val colorInt = ContextCompat.getColor(context, themeBackgroundRes)
                ColorUtils.calculateLuminance(colorInt) < 0.45
            } catch (_: Exception) {
                false
            }
        }
    }
    val themeTextColor = remember(isDarkTheme) {
        if (isDarkTheme) android.graphics.Color.WHITE else android.graphics.Color.parseColor("#0F172A")
    }

    var isProductRemoteSearchActive by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(activePanelState) {
        if (activePanelState != KeyboardPanelState.PRODUCT_REMOTE) {
            isProductRemoteSearchActive = false
        }
    }

    ImeCrashGuard(
        hasError = hasImeError,
        errorMessage = imeErrorMessage,
        onReset = onResetImeError,
        modifier = modifier
    ) {
        KeyboardFloatingContainer(
            isFloating = isFloatingMode,
            onDockKeyboard = onDockFloatingKeyboard
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                // 1. Background Layer (Adaptive Color or Image with Scrim)
                if (themeType == ThemeType.IMAGE) {
                    Image(
                        painter = painterResource(id = themeBackgroundRes),
                        contentDescription = null,
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.25f))
                    )
                } else {
                    val backgroundColor = remember(themeBackgroundRes) {
                        try {
                            Color(ContextCompat.getColor(context, themeBackgroundRes))
                        } catch (_: Exception) {
                            Color(themeBackgroundRes)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(backgroundColor)
                    )
                }

                // 2. Content Column Layer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(bottom = bottomChinOffsetDp.dp)
                ) {
                    // Top Bar Area: Animated transition between Feature Header and Candidate Strip
            // Rendered when in MAIN keyboard mode
            if (activePanelState == KeyboardPanelState.MAIN) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (isIncognitoMode) {
                        KeyboardIncognitoBanner(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        )
                    } else {
                        val isClipboardActive = features.any { it.id == com.frogobox.appkeyboard.model.KeyboardFeatureType.CLIPBOARD.id }
                        val shouldShowRecentClip = !recentClip.isNullOrBlank() && isClipboardActive
                        val showSuggestionsOrClip = (isSuggestionVisible && suggestionResult.hasSuggestions()) || shouldShowRecentClip || (textExpansionMatch != null) || (mathCalculationResult != null)

                        AnimatedContent(
                            targetState = showSuggestionsOrClip,
                            transitionSpec = {
                                fadeIn(
                                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
                                ) togetherWith fadeOut(
                                    animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                                )
                            },
                            label = "TopBarTransition"
                        ) { shouldShow ->
                            if (shouldShow) {
                                KeyboardSuggestionBar(
                                    result = suggestionResult,
                                    onCandidateSelected = onCandidateSelected,
                                    onSwitchMenu = onSwitchSuggestionMenu,
                                    onClose = onCloseSuggestion,
                                    recentClip = if (isClipboardActive) recentClip else null,
                                    onQuickPaste = onQuickPaste,
                                    textExpansionMatch = textExpansionMatch,
                                    onExpansionSelected = onExpansionSelected,
                                    mathCalculationResult = mathCalculationResult,
                                    onMathResultSelected = onMathResultSelected
                                )
                            } else if (features.isNotEmpty()) {
                                KeyboardFeatureHeader(
                                    features = features,
                                    onFeatureClick = onFeatureClick
                                )
                            }
                        }
                    }
                }
            }

            // Main Content Area: Crossfade transition across active keyboard panels
            Crossfade(
                targetState = activePanelState,
                animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                label = "PanelCrossfade"
            ) { panel ->
                when (panel) {
                    KeyboardPanelState.MAIN -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            if (isNumberRowEnabled) {
                                KeyboardNumberRow(
                                    onNumberClick = { code ->
                                        onNumberRowClick?.invoke(code) ?: onKeyboardActionListener?.onKey(code)
                                    },
                                    isDarkTheme = isDarkTheme,
                                    textColor = Color(themeTextColor)
                                )
                            }

                            if (oneHandedMode == "OFF") {
                                val keyboardModifier = if (isSplitModeEnabled) {
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                } else {
                                    Modifier.fillMaxWidth()
                                }
                                MainKeyboardView(
                                    keyboard = currentKeyboard,
                                    onActionListener = onKeyboardActionListener,
                                    textColor = themeTextColor,
                                    actionTextColor = themeTextColor,
                                    isDarkTheme = isDarkTheme,
                                    onInit = onMainKeyboardInit,
                                    modifier = keyboardModifier
                                )
                            } else if (oneHandedMode == "LEFT") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.weight(0.84f)) {
                                        MainKeyboardView(
                                            keyboard = currentKeyboard,
                                            onActionListener = onKeyboardActionListener,
                                            textColor = themeTextColor,
                                            actionTextColor = themeTextColor,
                                            isDarkTheme = isDarkTheme,
                                            onInit = onMainKeyboardInit
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(0.16f)
                                            .fillMaxHeight()
                                    ) {
                                        KeyboardOneHandedSideRail(
                                            isDockedLeft = true,
                                            onSwapSide = { onChangeOneHandedMode("RIGHT") },
                                            onRestoreFullWidth = { onChangeOneHandedMode("OFF") },
                                            onToggleNumberRow = onToggleNumberRow
                                        )
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(0.16f)
                                            .fillMaxHeight()
                                    ) {
                                        KeyboardOneHandedSideRail(
                                            isDockedLeft = false,
                                            onSwapSide = { onChangeOneHandedMode("LEFT") },
                                            onRestoreFullWidth = { onChangeOneHandedMode("OFF") },
                                            onToggleNumberRow = onToggleNumberRow
                                        )
                                    }
                                    Box(modifier = Modifier.weight(0.84f)) {
                                        MainKeyboardView(
                                            keyboard = currentKeyboard,
                                            onActionListener = onKeyboardActionListener,
                                            textColor = themeTextColor,
                                            actionTextColor = themeTextColor,
                                            isDarkTheme = isDarkTheme,
                                            onInit = onMainKeyboardInit
                                        )
                                    }
                                }
                            }
                        }
                    }

                    KeyboardPanelState.EMOJI -> {
                        EmojiKeyboardScreen(
                            emojis = emojis,
                            selectedCategory = selectedEmojiCategory,
                            categories = emojiCategories,
                            onCategorySelected = onSelectEmojiCategory,
                            onEmojiClicked = onEmojiClicked,
                            onBackClicked = onBackToMain,
                            isLoading = isEmojiLoading,
                            onDeleteClicked = onDeleteEmoji ?: {}
                        )
                    }

                    KeyboardPanelState.AUTO_TEXT -> {
                        Box(modifier = Modifier.fillMaxWidth().height(270.dp)) {
                            AutoTextKeyboardScreen(
                                autoTextList = autoTextList,
                                onCommitText = onCommitText,
                                onBackClick = onBackToMain,
                                onManageClick = onManageAutoText,
                                isAlwaysShow = (alwaysShowFeatureId == com.frogobox.appkeyboard.model.KeyboardFeatureType.AUTO_TEXT.id),
                                onToggleAlwaysShow = onToggleAlwaysShowFeature?.let { cb -> { cb(com.frogobox.appkeyboard.model.KeyboardFeatureType.AUTO_TEXT.id) } }
                            )
                        }
                    }

                    KeyboardPanelState.PRODUCT_REMOTE -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            val panelHeight = if (isProductRemoteSearchActive) 260.dp else 540.dp
                            val animatedHeight by animateDpAsState(
                                targetValue = panelHeight,
                                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                                label = "ProductRemoteHeight"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(animatedHeight)
                            ) {
                                ProductRemoteKeyboardScreen(
                                    items = productRemoteItems,
                                    isLoading = isProductRemoteLoading,
                                    errorMessage = productRemoteError,
                                    onCommitText = onCommitText,
                                    onBackClick = {
                                        if (isProductRemoteSearchActive) {
                                            isProductRemoteSearchActive = false
                                        } else {
                                            onBackToMain()
                                        }
                                    },
                                    onRefresh = onRefreshProductRemote,
                                    onManageClick = onManageProductRemote,
                                    isAlwaysShow = (alwaysShowFeatureId == com.frogobox.appkeyboard.model.KeyboardFeatureType.PRODUCT_REMOTE.id),
                                    onToggleAlwaysShow = onToggleAlwaysShowFeature?.let { cb -> { cb(com.frogobox.appkeyboard.model.KeyboardFeatureType.PRODUCT_REMOTE.id) } },
                                    isSearchActive = isProductRemoteSearchActive,
                                    onSearchActiveChange = { isProductRemoteSearchActive = it },
                                    onRegisterKeyHandler = onRegisterKeyConsumer,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            AnimatedVisibility(
                                visible = isProductRemoteSearchActive,
                                enter = slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
                                ) + fadeIn(animationSpec = tween(150)),
                                exit = slideOutVertically(
                                    targetOffsetY = { it },
                                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
                                ) + fadeOut(animationSpec = tween(150))
                            ) {
                                MainKeyboardView(
                                    keyboard = currentKeyboard,
                                    onActionListener = onKeyboardActionListener,
                                    textColor = themeTextColor,
                                    actionTextColor = themeTextColor,
                                    isDarkTheme = isDarkTheme,
                                    onInit = onMainKeyboardInit
                                )
                            }
                        }
                    }

                    KeyboardPanelState.TEMPLATE_TEXT_GAME,
                    KeyboardPanelState.TEMPLATE_TEXT_APP,
                    KeyboardPanelState.TEMPLATE_TEXT_SALE,
                    KeyboardPanelState.TEMPLATE_TEXT_LOVE,
                    KeyboardPanelState.TEMPLATE_TEXT_GREETING -> {
                        val category = panel.templateFeatureType?.let {
                            com.frogobox.appkeyboard.ui.keyboard.autotext.AutoTextCategory.fromFeatureType(it)
                        } ?: com.frogobox.appkeyboard.ui.keyboard.autotext.AutoTextCategory.GAME
                        Box(modifier = Modifier.fillMaxWidth().height(270.dp)) {
                            AutoTextKeyboardScreen(
                                autoTextList = autoTextList,
                                onCommitText = onCommitText,
                                onBackClick = onBackToMain,
                                onManageClick = onManageAutoText,
                                initialCategory = category
                            )
                        }
                    }

                    KeyboardPanelState.NEWS -> {
                        Box(modifier = Modifier.fillMaxWidth().height(270.dp)) {
                            NewsKeyboardScreen(
                                articles = newsArticles,
                                isLoading = isNewsLoading,
                                onCommitText = onCommitText,
                                onBackClick = onBackToMain,
                                isAlwaysShow = (alwaysShowFeatureId == com.frogobox.appkeyboard.model.KeyboardFeatureType.NEWS.id),
                                onToggleAlwaysShow = onToggleAlwaysShowFeature?.let { cb -> { cb(com.frogobox.appkeyboard.model.KeyboardFeatureType.NEWS.id) } }
                            )
                        }
                    }

                    KeyboardPanelState.MOVIE -> {
                        Box(modifier = Modifier.fillMaxWidth().height(270.dp)) {
                            MovieKeyboardScreen(
                                movieList = movieList,
                                isLoading = isMovieLoading,
                                onCommitText = onCommitText,
                                onBackClick = onBackToMain,
                                isAlwaysShow = (alwaysShowFeatureId == com.frogobox.appkeyboard.model.KeyboardFeatureType.MOVIE.id),
                                onToggleAlwaysShow = onToggleAlwaysShowFeature?.let { cb -> { cb(com.frogobox.appkeyboard.model.KeyboardFeatureType.MOVIE.id) } }
                            )
                        }
                    }

                    KeyboardPanelState.WEBVIEW -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                            ) {
                                WebviewKeyboardScreen(
                                    onCommitText = onCommitText,
                                    onBackClick = onBackToMain,
                                    onRegisterKeyHandler = onRegisterKeyConsumer,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            MainKeyboardView(
                                keyboard = currentKeyboard,
                                onActionListener = onKeyboardActionListener,
                                onInit = onMainKeyboardInit
                            )
                        }
                    }

                    KeyboardPanelState.FORM -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                            ) {
                                FormKeyboardScreen(
                                    onCommitText = onCommitText,
                                    onBackClick = onBackToMain,
                                    onRegisterKeyHandler = onRegisterKeyConsumer,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            MainKeyboardView(
                                keyboard = currentKeyboard,
                                onActionListener = onKeyboardActionListener,
                                onInit = onMainKeyboardInit
                            )
                        }
                    }

                    KeyboardPanelState.CLIPBOARD -> {
                        ClipboardKeyboardScreen(
                            items = clipboardItems,
                            onCommitText = onCommitText,
                            onTogglePin = onTogglePinClipboardItem,
                            onDeleteClip = onDeleteClipboardItem,
                            onClearHistory = onClearClipboardHistory,
                            onBackClick = onBackToMain,
                            isAlwaysShow = (alwaysShowFeatureId == com.frogobox.appkeyboard.model.KeyboardFeatureType.CLIPBOARD.id),
                            onToggleAlwaysShow = onToggleAlwaysShowFeature?.let { cb -> { cb(com.frogobox.appkeyboard.model.KeyboardFeatureType.CLIPBOARD.id) } }
                        )
                    }

                    KeyboardPanelState.TEXT_EDIT -> {
                        TextEditKeyboardScreen(
                            isSelectionMode = isSelectionMode,
                            onAction = onTextEditAction,
                            onBackClick = onBackToMain,
                            isAlwaysShow = (alwaysShowFeatureId == com.frogobox.appkeyboard.model.KeyboardFeatureType.TEXT_EDIT.id),
                            onToggleAlwaysShow = onToggleAlwaysShowFeature?.let { cb -> { cb(com.frogobox.appkeyboard.model.KeyboardFeatureType.TEXT_EDIT.id) } }
                        )
                    }

                    KeyboardPanelState.AI_ASSISTANT -> {
                        KeyboardAiAssistantScreen(
                            initialText = aiAssistantInitialText,
                            onApplyText = onAiApplyText,
                            onCopyText = onAiCopyText,
                            onBackClick = onBackToMain,
                            isIncognito = isIncognitoMode
                        )
                    }
                }
            }
        }

        // 3. Floating Voice Typing Listening Banner Overlay
        AnimatedVisibility(
            visible = isVoiceTypingActive,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp)
        ) {
            KeyboardVoiceListeningBanner(
                isListening = isVoiceListening,
                amplitude = voiceAmplitude,
                statusText = voiceStatusText,
                onStopClick = onStopVoiceTyping,
                onCloseClick = onCloseVoiceTyping,
                errorMessage = voiceErrorMessage
            )
        }
    }
}
}
}

/**
 * Reassuring Incognito / Privacy Guard banner displayed in the top bar when the active
 * editor is a password field or in incognito / no-personalized-learning mode.
 */
@Composable
fun KeyboardIncognitoBanner(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "Mode Pribadi (Incognito)",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Mode Pribadi Aktif • Saran & Riwayat Dimatikan",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
