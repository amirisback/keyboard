package com.frogobox.appkeyboard.services

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.emoji2.text.EmojiCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.frogobox.api.movie.ConsumeMovieApi
import com.frogobox.api.news.ConsumeNewsApi
import com.frogobox.appkeyboard.R
import com.frogobox.appkeyboard.common.core.Resource
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.ClipboardItem
import com.frogobox.appkeyboard.model.KeyboardFeatureModel
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.model.ThemeType
import com.frogobox.appkeyboard.repository.autotext.AutoTextRepository
import com.frogobox.appkeyboard.repository.clipboard.ClipboardRepository
import com.frogobox.appkeyboard.repository.data.DataApiRepository
import com.frogobox.appkeyboard.repository.productremote.ProductRemoteRepository
import com.frogobox.appkeyboard.suggestion.SuggestionResult
import com.frogobox.appkeyboard.suggestion.WordSuggestionEngine
import com.frogobox.appkeyboard.ui.autotext.AutoTextActivity
import com.frogobox.appkeyboard.ui.keyboard.textedit.TextEditAction
import com.frogobox.appkeyboard.ui.productremote.ProductRemoteActivity
import com.frogobox.appkeyboard.ui.keyboard.root.KeyboardImeRootScreen
import com.frogobox.appkeyboard.ui.keyboard.root.KeyboardPanelState
import com.frogobox.appkeyboard.ui.main.MainActivity
import com.frogobox.appkeyboard.ui.theme.compose.FrogoKeyboardTheme
import com.frogobox.coreapi.ConsumeApiResponse
import com.frogobox.coresdk.response.FrogoDataResponse
import com.frogobox.coreutil.movie.MovieUrl
import com.frogobox.coreutil.movie.model.TrendingMovie
import com.frogobox.coreutil.movie.response.Trending
import com.frogobox.coreutil.news.NewsConstant.CATEGORY_HEALTH
import com.frogobox.coreutil.news.NewsConstant.COUNTRY_ID
import com.frogobox.coreutil.news.NewsUrl
import com.frogobox.coreutil.news.model.Article
import com.frogobox.coreutil.news.response.ArticleResponse
import com.frogobox.libkeyboard.common.core.BaseKeyboardIME
import com.frogobox.libkeyboard.common.sound.MechanicalSoundManager
import com.frogobox.libkeyboard.common.sound.MechanicalSoundType
import com.frogobox.libkeyboard.ui.emoji.EmojiCategoryType
import com.frogobox.libkeyboard.ui.emoji.getEmojiCategory
import com.frogobox.libkeyboard.ui.emoji.parseRawEmojiSpecsFile
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard
import com.frogobox.libkeyboard.ui.main.MainKeyboard
import com.frogobox.sdk.delegate.preference.PreferenceDelegates
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.core.graphics.toColorInt

/**
 * Modern 100% Full Pure Jetpack Compose InputMethodService.
 * Uses a unified ComposeView, reactive panel navigation, and direct Compose feature integrations.
 */
@AndroidEntryPoint
class KeyboardIME : BaseKeyboardIME() {

    private val imeLifecycleOwner = ImeLifecycleOwner()

    @Inject
    lateinit var pref: PreferenceDelegates

    @Inject
    lateinit var keyboardUtil: KeyboardUtil

    @Inject
    lateinit var suggestionEngine: WordSuggestionEngine

    @Inject
    lateinit var dataApiRepository: DataApiRepository

    @Inject
    lateinit var productRemoteRepository: ProductRemoteRepository

    @Inject
    lateinit var autoTextRepository: AutoTextRepository

    @Inject
    lateinit var clipboardRepository: ClipboardRepository

    // Reactive State Holders
    private val activePanelStateFlow = MutableStateFlow(KeyboardPanelState.MAIN)
    private val themeTypeFlow = MutableStateFlow(ThemeType.COLOR)
    private val themeBackgroundResFlow = MutableStateFlow(R.color.color_bg_keyboard_default)
    private val featuresFlow = MutableStateFlow<List<KeyboardFeatureModel>>(emptyList())
    private val isSuggestionVisibleFlow = MutableStateFlow(false)
    private val suggestionResultFlow = MutableStateFlow(SuggestionResult.EMPTY)
    private val alwaysShowFeatureFlow = MutableStateFlow<String?>(null)

    // Sub-Screen Reactive Data Flows
    private val autoTextListFlow = MutableStateFlow<List<AutoTextEntity>>(emptyList())
    private val productRemoteItemsFlow = MutableStateFlow<List<DataItemResponse>>(emptyList())
    private val isProductRemoteLoadingFlow = MutableStateFlow(false)
    private val productRemoteErrorFlow = MutableStateFlow<String?>(null)
    private val newsArticlesFlow = MutableStateFlow<List<Article>>(emptyList())
    private val isNewsLoadingFlow = MutableStateFlow(false)
    private val movieListFlow = MutableStateFlow<List<TrendingMovie>>(emptyList())
    private val isMovieLoadingFlow = MutableStateFlow(false)

    // Clipboard & Text Editing State Flows
    private val clipboardItemsFlow = MutableStateFlow<List<ClipboardItem>>(emptyList())
    private val recentClipFlow = MutableStateFlow<String?>(null)
    private val isSelectionModeFlow = MutableStateFlow(false)
    private var selectionAnchor = -1
    private var clipboardManager: ClipboardManager? = null
    private val clipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
        checkAndCaptureClipboard()
    }

    // Emoji State Flows
    private val emojisFlow = MutableStateFlow<List<String>>(emptyList())
    private val selectedEmojiCategoryFlow = MutableStateFlow(EmojiCategoryType.GENERAL)
    private val isEmojiLoadingFlow = MutableStateFlow(false)
    private val emojiCache = mutableMapOf<String, List<String>>()
    private val systemFontPaint = Paint().apply { typeface = Typeface.DEFAULT }
    private val emojiCompatMetadataVersion = 0

    // Coroutine Scope & Managed Jobs
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var productRemoteJob: Job? = null
    private var autoTextJob: Job? = null
    private var suggestionJob: Job? = null
    private var clipboardJob: Job? = null

    // Interop MainKeyboard Instance
    private var mainKeyboardInstance: MainKeyboard? = null
    private val currentKeyboardFlow = MutableStateFlow<ItemMainKeyboard?>(null)
    private var keyConsumer: ((Int, Boolean) -> Boolean)? = null

    override fun onCreate() {
        super.onCreate()
        imeLifecycleOwner.onCreate()
        clipboardManager = getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
    }

    override fun onWindowShown() {
        super.onWindowShown()
        imeLifecycleOwner.onStart()
        imeLifecycleOwner.onResume()
        applySoundAndHapticSettings()
        setupTheme()
        setupFeatureKeyboard()
        loadAutoText()
        loadProductRemote()

        val isClipboardActive = keyboardUtil.isClipboardEnabled()
        if (isClipboardActive) {
            loadClipboardItems()
            try {
                clipboardManager?.addPrimaryClipChangedListener(clipChangedListener)
            } catch (_: Exception) {}
            checkAndCaptureClipboard()
        } else {
            recentClipFlow.value = null
            clipboardItemsFlow.value = emptyList()
            try {
                clipboardManager?.removePrimaryClipChangedListener(clipChangedListener)
            } catch (_: Exception) {}
        }

        val alwaysShowId = keyboardUtil.getAlwaysShowFeature()
        alwaysShowFeatureFlow.value = alwaysShowId

        val activeFeatures = keyboardUtil.menuKeyboard()
        val defaultFeature = if (!alwaysShowId.isNullOrEmpty()) {
            activeFeatures.firstOrNull { it.id == alwaysShowId }
        } else null

        if (defaultFeature != null) {
            handleFeatureClick(defaultFeature)
        } else {
            showMainKeyboard()
        }
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        imeLifecycleOwner.onPause()
        imeLifecycleOwner.onStop()
        productRemoteJob?.cancel()
        autoTextJob?.cancel()
        suggestionJob?.cancel()
        clipboardJob?.cancel()
        try {
            clipboardManager?.removePrimaryClipChangedListener(clipChangedListener)
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        productRemoteJob?.cancel()
        autoTextJob?.cancel()
        suggestionJob?.cancel()
        clipboardJob?.cancel()
        try {
            clipboardManager?.removePrimaryClipChangedListener(clipChangedListener)
        } catch (_: Exception) {}
        serviceJob.cancelChildren()
        imeLifecycleOwner.onDestroy()
        mainKeyboardInstance = null
        emojiCache.clear()
        MechanicalSoundManager.getInstance(this).release()
    }

    override fun onCreateInputView(): View {
        setupBinding()
        initCurrentInputConnection()
        initView()

        val composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setViewTreeLifecycleOwner(imeLifecycleOwner)
            setViewTreeViewModelStoreOwner(imeLifecycleOwner)
            setViewTreeSavedStateRegistryOwner(imeLifecycleOwner)
            setContent {
                FrogoKeyboardTheme {
                    val activePanel by activePanelStateFlow.collectAsState()
                    val themeType by themeTypeFlow.collectAsState()
                    val themeBg by themeBackgroundResFlow.collectAsState()
                    val features by featuresFlow.collectAsState()
                    val isSuggestionVisible by isSuggestionVisibleFlow.collectAsState()
                    val suggestionResult by suggestionResultFlow.collectAsState()
                    val currentKeyboard by currentKeyboardFlow.collectAsState()

                    val autoTextList by autoTextListFlow.collectAsState()
                    val productRemoteItems by productRemoteItemsFlow.collectAsState()
                    val isProductRemoteLoading by isProductRemoteLoadingFlow.collectAsState()
                    val productRemoteError by productRemoteErrorFlow.collectAsState()
                    val newsArticles by newsArticlesFlow.collectAsState()
                    val isNewsLoading by isNewsLoadingFlow.collectAsState()
                    val movieList by movieListFlow.collectAsState()
                    val isMovieLoading by isMovieLoadingFlow.collectAsState()

                    val emojis by emojisFlow.collectAsState()
                    val selectedEmojiCategory by selectedEmojiCategoryFlow.collectAsState()
                    val isEmojiLoading by isEmojiLoadingFlow.collectAsState()

                    val clipboardItems by clipboardItemsFlow.collectAsState()
                    val recentClip by recentClipFlow.collectAsState()
                    val isSelectionMode by isSelectionModeFlow.collectAsState()
                    val alwaysShowFeature by alwaysShowFeatureFlow.collectAsState()

                    KeyboardImeRootScreen(
                        activePanelState = activePanel,
                        themeType = themeType,
                        themeBackgroundRes = themeBg,
                        features = features,
                        onFeatureClick = { feature -> handleFeatureClick(feature) },
                        isSuggestionVisible = isSuggestionVisible,
                        suggestionResult = suggestionResult,
                        onCandidateSelected = { word, _ -> handleCandidateSelected(word) },
                        onSwitchSuggestionMenu = {
                            isSuggestionVisibleFlow.value = false
                        },
                        onCloseSuggestion = {
                            suggestionResultFlow.value = SuggestionResult.EMPTY
                            isSuggestionVisibleFlow.value = false
                            recentClipFlow.value = null
                        },
                        currentKeyboard = currentKeyboard,
                        onKeyboardActionListener = this@KeyboardIME,
                        onMainKeyboardInit = { view ->
                            mainKeyboardInstance = view
                            currentKeyboard?.let { view.setKeyboard(it) }
                        },
                        autoTextList = autoTextList,
                        onManageAutoText = {
                            val intent = Intent(this@KeyboardIME, AutoTextActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(intent)
                        },
                        productRemoteItems = productRemoteItems,
                        isProductRemoteLoading = isProductRemoteLoading,
                        productRemoteError = productRemoteError,
                        onRefreshProductRemote = {
                            fetchProductRemote()
                        },
                        onManageProductRemote = {
                            val intent = Intent(this@KeyboardIME, ProductRemoteActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(intent)
                        },
                        newsArticles = newsArticles,
                        isNewsLoading = isNewsLoading,
                        movieList = movieList,
                        isMovieLoading = isMovieLoading,
                        emojis = emojis,
                        selectedEmojiCategory = selectedEmojiCategory,
                        emojiCategories = getEmojiCategory(),
                        isEmojiLoading = isEmojiLoading,
                        onSelectEmojiCategory = { cat ->
                            selectedEmojiCategoryFlow.value = cat
                            loadEmojis(cat.path)
                        },
                        onEmojiClicked = { emoji ->
                            getActiveInputConnection()?.commitText(emoji, 1)
                            recordRecentEmoji(emoji)
                            this@KeyboardIME.onActionUp()
                        },
                        onCommitText = { text ->
                            getActiveInputConnection()?.commitText(text, 1)
                        },
                        onBackToMain = {
                            showMainKeyboard()
                        },
                        onRegisterKeyConsumer = { consumer ->
                            keyConsumer = consumer
                        },
                        clipboardItems = clipboardItems,
                        onTogglePinClipboardItem = { id ->
                            serviceScope.launch { clipboardRepository.togglePin(id) }
                        },
                        onDeleteClipboardItem = { id ->
                            serviceScope.launch { clipboardRepository.deleteClip(id) }
                        },
                        onClearClipboardHistory = {
                            serviceScope.launch { clipboardRepository.clearHistory(keepPinned = true) }
                        },
                        recentClip = recentClip,
                        onQuickPaste = { text ->
                            getActiveInputConnection()?.commitText(text, 1)
                            recentClipFlow.value = null
                        },
                        isSelectionMode = isSelectionMode,
                        onTextEditAction = { action ->
                            handleTextEditAction(action)
                        },
                        alwaysShowFeatureId = alwaysShowFeature,
                        onToggleAlwaysShowFeature = { featureId ->
                            handleToggleAlwaysShow(featureId)
                        },
                        onDeleteEmoji = {
                            onKey(ItemMainKeyboard.KEYCODE_DELETE)
                        }
                    )
                }
            }
        }

        window?.window?.decorView?.let { decor ->
            decor.setViewTreeLifecycleOwner(imeLifecycleOwner)
            decor.setViewTreeViewModelStoreOwner(imeLifecycleOwner)
            decor.setViewTreeSavedStateRegistryOwner(imeLifecycleOwner)
        }

        return composeView
    }

    override fun initialSetupKeyboard() {
        applySoundAndHapticSettings()
        currentKeyboardFlow.value = keyboard
        mainKeyboardInstance?.let { view ->
            keyboard?.let { view.setKeyboard(it) }
        }
    }

    override fun invalidateAllKeys() {
        mainKeyboardInstance?.invalidateAllKeys()
    }

    override fun showMainKeyboard() {
        keyConsumer = null
        activePanelStateFlow.value = KeyboardPanelState.MAIN
        isSuggestionVisibleFlow.value = false
        suggestionResultFlow.value = SuggestionResult.EMPTY
    }

    override fun hideMainKeyboard() {
        // Managed declaratively via activePanelStateFlow transitions
    }

    override fun showOnlyKeyboard() {
        activePanelStateFlow.value = KeyboardPanelState.MAIN
    }

    override fun hideOnlyKeyboard() {
        // Declarative state management
    }

    override fun runEmojiBoard() {
        activePanelStateFlow.value = KeyboardPanelState.EMOJI
        openEmojiPalette()
    }

    override fun setupTheme() {
        val background = pref.getPrefInt(
            KeyboardUtil.KEYBOARD_COLOR,
            R.color.color_bg_keyboard_default
        )

        val typeString = pref.getPrefString(
            KeyboardUtil.KEYBOARD_COLOR_TYPE,
            ThemeType.COLOR.name
        )

        val backgroundType = runCatching {
            ThemeType.valueOf(typeString)
        }.getOrDefault(ThemeType.COLOR)

        themeTypeFlow.value = backgroundType
        themeBackgroundResFlow.value = background

        val isDark = if (backgroundType == ThemeType.IMAGE) {
            true
        } else {
            try {
                val colorInt = ContextCompat.getColor(this, background)
                ColorUtils.calculateLuminance(colorInt) < 0.45
            } catch (_: Exception) {
                false
            }
        }
        val textColor = if (isDark) android.graphics.Color.WHITE else "#0F172A".toColorInt()
        mainKeyboardInstance?.setKeyboardTheme(
            textColor = textColor,
            actionTextColor = textColor,
            isDark = isDark
        )
    }

    override fun setupFeatureKeyboard() {
        featuresFlow.value = keyboardUtil.menuKeyboard()
    }

    override fun invalidateKeyboard() {
        setupTheme()
        setupFeatureKeyboard()
        loadAutoText()
        loadProductRemote()
        if (!keyboardUtil.isClipboardEnabled()) {
            recentClipFlow.value = null
            clipboardItemsFlow.value = emptyList()
            try {
                clipboardManager?.removePrimaryClipChangedListener(clipChangedListener)
            } catch (_: Exception) {}
        }
        alwaysShowFeatureFlow.value = keyboardUtil.getAlwaysShowFeature()
    }

    override fun initView() {
        suggestionEngine.loadDictionaryFromAsset(this)
        setupTheme()
        setupFeatureKeyboard()
        loadAutoText()
        loadProductRemote()
        openEmojiPalette()
    }

    override fun onKey(code: Int) {
        val kb = keyboard
        val isShifted = kb != null && kb.mShiftState > ItemMainKeyboard.SHIFT_OFF
        if (keyConsumer?.invoke(code, isShifted) == true) {
            if (kb?.mShiftState == ItemMainKeyboard.SHIFT_ON_ONE_CHAR) {
                kb.mShiftState = ItemMainKeyboard.SHIFT_OFF
                invalidateAllKeys()
            }
            return
        }

        val ic = getActiveInputConnection() ?: return
        onKeyExt(code, ic)

        if (keyboardUtil.isSuggestionEnabled()) {
            val word = getWordBeforeCursor(ic)
            if (word.isNotEmpty()) {
                suggestionJob?.cancel()
                suggestionJob = serviceScope.launch(Dispatchers.Default) {
                    val suggestions = suggestionEngine.getSuggestions(word)
                    withContext(Dispatchers.Main) {
                        suggestionResultFlow.value = suggestions
                        isSuggestionVisibleFlow.value = true
                    }
                }
            } else {
                suggestionJob?.cancel()
                suggestionResultFlow.value = SuggestionResult.EMPTY
                if (code == ItemMainKeyboard.KEYCODE_SPACE ||
                    code == ItemMainKeyboard.KEYCODE_ENTER ||
                    code == ItemMainKeyboard.KEYCODE_DELETE) {
                    isSuggestionVisibleFlow.value = false
                }
            }
        }
    }

    override fun deleteWordsBeforeCursor(count: Int) {
        val ic = getActiveInputConnection() ?: return
        if (count <= 0) return
        val textBefore = ic.getTextBeforeCursor(120, 0)?.toString() ?: return
        if (textBefore.isEmpty()) return

        var remainingWords = count
        var deleteLen = 0
        var inWord = false

        for (i in textBefore.length - 1 downTo 0) {
            val ch = textBefore[i]
            if (ch.isWhitespace() || !ch.isLetterOrDigit()) {
                if (inWord) {
                    remainingWords--
                    if (remainingWords <= 0) {
                        deleteLen++
                        break
                    }
                    inWord = false
                }
            } else {
                inWord = true
            }
            deleteLen++
        }

        if (deleteLen > 0) {
            ic.deleteSurroundingText(deleteLen, 0)
        }
    }

    override fun getKeyboardLayoutXML(): Int {
        return pref.getPrefInt(
            KeyboardUtil.KEYBOARD_TYPE, com.frogobox.libkeyboard.R.xml.keys_letters_qwerty
        )
    }

    override fun EditText.showKeyboardExt() {
        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showOnlyKeyboard()
            }
        }
        setOnClickListener {
            showOnlyKeyboard()
        }
    }

    private fun getActiveInputConnection(): InputConnection? {
        return currentInputConnection
    }

    private fun getWordBeforeCursor(ic: InputConnection): String {
        val text = ic.getTextBeforeCursor(40, 0)?.toString() ?: ""
        return text.takeLastWhile { it.isLetterOrDigit() || it == '\'' }
    }

    private fun handleCandidateSelected(selectedWord: String) {
        val ic = getActiveInputConnection()
        if (ic != null) {
            val currentWord = getWordBeforeCursor(ic)
            if (currentWord.isNotEmpty()) {
                ic.deleteSurroundingText(currentWord.length, 0)
            }
            ic.commitText("$selectedWord ", 1)
            suggestionResultFlow.value = SuggestionResult.EMPTY
            isSuggestionVisibleFlow.value = false
        }
    }

    private fun handleFeatureClick(feature: KeyboardFeatureModel) {
        val featureType = KeyboardFeatureType.from(feature.id)
        when (featureType) {
            KeyboardFeatureType.AUTO_TEXT -> {
                loadAutoText()
                activePanelStateFlow.value = KeyboardPanelState.AUTO_TEXT
            }

            KeyboardFeatureType.PRODUCT_REMOTE -> {
                loadProductRemote()
                activePanelStateFlow.value = KeyboardPanelState.PRODUCT_REMOTE
            }

            KeyboardFeatureType.TEMPLATE_TEXT_GAME,
            KeyboardFeatureType.TEMPLATE_TEXT_APP,
            KeyboardFeatureType.TEMPLATE_TEXT_SALE,
            KeyboardFeatureType.TEMPLATE_TEXT_LOVE,
            KeyboardFeatureType.TEMPLATE_TEXT_GREETING,
                -> {
                KeyboardPanelState.fromFeature(featureType)?.let { panel ->
                    activePanelStateFlow.value = panel
                }
            }

            KeyboardFeatureType.NEWS -> {
                fetchNews()
                activePanelStateFlow.value = KeyboardPanelState.NEWS
            }

            KeyboardFeatureType.MOVIE -> {
                fetchMovies()
                activePanelStateFlow.value = KeyboardPanelState.MOVIE
            }

            KeyboardFeatureType.WEB -> {
                activePanelStateFlow.value = KeyboardPanelState.WEBVIEW
            }

            KeyboardFeatureType.FORM -> {
                activePanelStateFlow.value = KeyboardPanelState.FORM
            }

            KeyboardFeatureType.CLIPBOARD -> {
                activePanelStateFlow.value = KeyboardPanelState.CLIPBOARD
            }

            KeyboardFeatureType.TEXT_EDIT -> {
                activePanelStateFlow.value = KeyboardPanelState.TEXT_EDIT
            }

            KeyboardFeatureType.SUGGESTION -> {
                isSuggestionVisibleFlow.value = !isSuggestionVisibleFlow.value
            }

            KeyboardFeatureType.CHANGE_KEYBOARD -> {
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
            }

            KeyboardFeatureType.SETTING -> {
                startActivity(Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }
    }

    private fun loadAutoText() {
        autoTextJob?.cancel()
        autoTextJob = serviceScope.launch {
            autoTextRepository.getAutoText().collect { items ->
                autoTextListFlow.value = items
            }
        }
    }

    private fun loadProductRemote() {
        productRemoteJob?.cancel()
        productRemoteJob = serviceScope.launch {
            productRemoteRepository.getSavedProductsStream().collect { savedList ->
                productRemoteItemsFlow.value = savedList.map { it.toDataItemResponse() }
                isProductRemoteLoadingFlow.value = false
            }
        }
    }

    private fun fetchProductRemote() {
        isProductRemoteLoadingFlow.value = true
        productRemoteErrorFlow.value = null
        serviceScope.launch {
            productRemoteRepository.syncAllFromRemote().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        isProductRemoteLoadingFlow.value = true
                    }
                    is Resource.Success -> {
                        isProductRemoteLoadingFlow.value = false
                        productRemoteErrorFlow.value = null
                    }
                    is Resource.Error -> {
                        isProductRemoteLoadingFlow.value = false
                        productRemoteErrorFlow.value = resource.message
                    }
                }
            }
        }
    }

    private fun fetchNews() {
        isNewsLoadingFlow.value = true
        val consumeNewsApi = ConsumeNewsApi(NewsUrl.API_KEY)
        consumeNewsApi.getTopHeadline(
            null,
            null,
            CATEGORY_HEALTH,
            COUNTRY_ID,
            null,
            null,
            object : ConsumeApiResponse<ArticleResponse> {
                override fun onSuccess(data: ArticleResponse) {
                    isNewsLoadingFlow.value = false
                    data.articles?.let { newsArticlesFlow.value = it }
                }

                override fun onFailed(statusCode: Int, errorMessage: String) {
                    isNewsLoadingFlow.value = false
                }

                override fun onFinish() {
                    isNewsLoadingFlow.value = false
                }

                override fun onShowProgress() {
                    isNewsLoadingFlow.value = true
                }

                override fun onHideProgress() {
                    isNewsLoadingFlow.value = false
                }
            }
        )
    }

    private fun fetchMovies() {
        isMovieLoadingFlow.value = true
        val consumeMovieApi = ConsumeMovieApi(MovieUrl.API_KEY)
        consumeMovieApi.getTrendingMovieDay(object : FrogoDataResponse<Trending<TrendingMovie>> {
            override fun onSuccess(data: Trending<TrendingMovie>) {
                isMovieLoadingFlow.value = false
                data.results?.let { movieListFlow.value = it }
            }

            override fun onFailed(statusCode: Int, errorMessage: String) {
                isMovieLoadingFlow.value = false
            }

            override fun onFinish() {
                isMovieLoadingFlow.value = false
            }

            override fun onShowProgress() {
                isMovieLoadingFlow.value = true
            }

            override fun onHideProgress() {
                isMovieLoadingFlow.value = false
            }
        })
    }

    companion object {
        const val PREF_RECENT_EMOJIS = "PREF_RECENT_EMOJIS"
    }

    private fun getRecentEmojis(): List<String> {
        val raw = pref.getPrefString(PREF_RECENT_EMOJIS, "")
        if (raw.isBlank()) return emptyList()
        return raw.split(",").filter { it.isNotBlank() }
    }

    private fun recordRecentEmoji(emoji: String) {
        val current = getRecentEmojis().toMutableList()
        current.remove(emoji)
        current.add(0, emoji)
        val limited = current.take(30)
        pref.savePrefString(PREF_RECENT_EMOJIS, limited.joinToString(","))
        if (selectedEmojiCategoryFlow.value == EmojiCategoryType.RECENT) {
            emojisFlow.value = limited
        }
    }

    private fun checkAndCaptureClipboard() {
        if (!keyboardUtil.isClipboardEnabled()) {
            recentClipFlow.value = null
            return
        }
        val cm = clipboardManager ?: return
        val clip = cm.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0)?.text?.toString()?.trim()
            if (!text.isNullOrEmpty()) {
                recentClipFlow.value = text
                serviceScope.launch {
                    clipboardRepository.addClip(text)
                }
            }
        }
    }

    private fun handleToggleAlwaysShow(featureId: String) {
        val current = keyboardUtil.getAlwaysShowFeature()
        val next = if (current == featureId) null else featureId
        keyboardUtil.setAlwaysShowFeature(next)
        alwaysShowFeatureFlow.value = next
    }

    private fun loadClipboardItems() {
        clipboardJob?.cancel()
        clipboardJob = serviceScope.launch {
            clipboardRepository.getClipboardItems().collect { items ->
                clipboardItemsFlow.value = items
            }
        }
    }

    private fun handleTextEditAction(action: TextEditAction) {
        val ic = getActiveInputConnection() ?: return
        when (action) {
            TextEditAction.MOVE_LEFT -> {
                if (isSelectionModeFlow.value) {
                    val extracted = ic.getExtractedText(ExtractedTextRequest(), 0)
                    val end = extracted?.selectionEnd ?: 0
                    if (selectionAnchor == -1) selectionAnchor = extracted?.selectionStart ?: end
                    val newEnd = (end - 1).coerceAtLeast(0)
                    ic.setSelection(selectionAnchor, newEnd)
                } else {
                    moveCursor(false)
                }
            }

            TextEditAction.MOVE_RIGHT -> {
                if (isSelectionModeFlow.value) {
                    val extracted = ic.getExtractedText(ExtractedTextRequest(), 0)
                    val end = extracted?.selectionEnd ?: 0
                    val totalLen = extracted?.text?.length ?: (end + 1)
                    if (selectionAnchor == -1) selectionAnchor = extracted?.selectionStart ?: end
                    val newEnd = (end + 1).coerceAtMost(totalLen)
                    ic.setSelection(selectionAnchor, newEnd)
                } else {
                    moveCursor(true)
                }
            }

            TextEditAction.MOVE_UP -> {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP))
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP))
            }

            TextEditAction.MOVE_DOWN -> {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN))
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN))
            }

            TextEditAction.MOVE_HOME -> {
                val textBefore = ic.getTextBeforeCursor(1000, 0)?.toString() ?: ""
                val lastNewline = textBefore.lastIndexOf('\n')
                val distToStart = if (lastNewline == -1) textBefore.length else textBefore.length - (lastNewline + 1)
                val extracted = ic.getExtractedText(ExtractedTextRequest(), 0)
                val currentPos = extracted?.selectionStart ?: 0
                val newPos = (currentPos - distToStart).coerceAtLeast(0)
                if (isSelectionModeFlow.value) {
                    if (selectionAnchor == -1) selectionAnchor = currentPos
                    ic.setSelection(selectionAnchor, newPos)
                } else {
                    ic.setSelection(newPos, newPos)
                }
            }

            TextEditAction.MOVE_END -> {
                val textAfter = ic.getTextAfterCursor(1000, 0)?.toString() ?: ""
                val nextNewline = textAfter.indexOf('\n')
                val distToEnd = if (nextNewline == -1) textAfter.length else nextNewline
                val extracted = ic.getExtractedText(ExtractedTextRequest(), 0)
                val currentPos = extracted?.selectionEnd ?: 0
                val newPos = currentPos + distToEnd
                if (isSelectionModeFlow.value) {
                    if (selectionAnchor == -1) selectionAnchor = extracted?.selectionStart ?: currentPos
                    ic.setSelection(selectionAnchor, newPos)
                } else {
                    ic.setSelection(newPos, newPos)
                }
            }

            TextEditAction.TOGGLE_SELECT -> {
                val newMode = !isSelectionModeFlow.value
                isSelectionModeFlow.value = newMode
                if (newMode) {
                    val extracted = ic.getExtractedText(ExtractedTextRequest(), 0)
                    selectionAnchor = extracted?.selectionStart ?: 0
                } else {
                    selectionAnchor = -1
                }
            }

            TextEditAction.SELECT_ALL -> {
                val extracted = ic.getExtractedText(ExtractedTextRequest(), 0)
                val totalLength = extracted?.text?.length ?: 0
                if (totalLength > 0) {
                    ic.setSelection(0, totalLength)
                    isSelectionModeFlow.value = true
                    selectionAnchor = 0
                } else {
                    ic.performContextMenuAction(android.R.id.selectAll)
                }
            }

            TextEditAction.CUT -> {
                val selectedText = ic.getSelectedText(0)?.toString() ?: ""
                if (selectedText.isNotEmpty()) {
                    try {
                        clipboardManager?.setPrimaryClip(ClipData.newPlainText("Cut", selectedText))
                    } catch (_: Exception) {}
                    ic.commitText("", 1)
                    serviceScope.launch { clipboardRepository.addClip(selectedText) }
                    isSelectionModeFlow.value = false
                    selectionAnchor = -1
                }
            }

            TextEditAction.COPY -> {
                val selectedText = ic.getSelectedText(0)?.toString() ?: ""
                if (selectedText.isNotEmpty()) {
                    try {
                        clipboardManager?.setPrimaryClip(ClipData.newPlainText("Copy", selectedText))
                    } catch (_: Exception) {}
                    serviceScope.launch { clipboardRepository.addClip(selectedText) }
                }
            }

            TextEditAction.PASTE -> {
                try {
                    val clipData = clipboardManager?.primaryClip
                    if (clipData != null && clipData.itemCount > 0) {
                        val clip = clipData.getItemAt(0)?.text?.toString() ?: ""
                        if (clip.isNotEmpty()) {
                            ic.commitText(clip, 1)
                        }
                    }
                } catch (_: Exception) {}
            }

            TextEditAction.DELETE -> {
                val selected = ic.getSelectedText(0)
                if (!selected.isNullOrEmpty()) {
                    ic.commitText("", 1)
                    isSelectionModeFlow.value = false
                    selectionAnchor = -1
                } else {
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
                }
            }

            TextEditAction.ENTER -> {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
            }
        }
    }

    private fun openEmojiPalette() {
        val recents = getRecentEmojis()
        if (recents.isNotEmpty()) {
            selectedEmojiCategoryFlow.value = EmojiCategoryType.RECENT
            loadEmojis(EmojiCategoryType.RECENT.path)
        } else {
            selectedEmojiCategoryFlow.value = EmojiCategoryType.GENERAL
            loadEmojis(EmojiCategoryType.GENERAL.path)
        }
    }

    private fun loadEmojis(path: String) {
        if (path == EmojiCategoryType.RECENT.path) {
            emojisFlow.value = getRecentEmojis()
            isEmojiLoadingFlow.value = false
            return
        }
        val cached = emojiCache[path]
        if (cached != null) {
            emojisFlow.value = cached
            return
        }

        isEmojiLoadingFlow.value = true
        serviceScope.launch(Dispatchers.IO) {
            val fullEmojiList = parseRawEmojiSpecsFile(this@KeyboardIME, path)

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
                emojisFlow.value = emojis
                isEmojiLoadingFlow.value = false
            }
        }
    }

    private fun applySoundAndHapticSettings() {
        val soundEnabled = pref.getPrefBoolean(MechanicalSoundManager.PREF_KEYBOARD_SOUND_ENABLED, true)
        val soundType = pref.getPrefString(
            MechanicalSoundManager.PREF_KEYBOARD_SOUND_TYPE,
            MechanicalSoundType.CHERRY_MX_BLUE.id
        )
        val soundVolumeInt = pref.getPrefInt(MechanicalSoundManager.PREF_KEYBOARD_SOUND_VOLUME, 80)
        val vibrateEnabled = pref.getPrefBoolean(MechanicalSoundManager.PREF_KEYBOARD_VIBRATE_ENABLED, true)

        ItemMainKeyboard.SOUND_ON_KEYPRESS = soundEnabled
        ItemMainKeyboard.MECHANICAL_SOUND_TYPE = soundType
        ItemMainKeyboard.SOUND_VOLUME = (soundVolumeInt / 100f).coerceIn(0.05f, 1.0f)
        ItemMainKeyboard.VIBRATE_ON_KEYPRESS = vibrateEnabled
    }

}