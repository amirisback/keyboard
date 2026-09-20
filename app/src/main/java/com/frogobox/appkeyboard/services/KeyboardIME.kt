package com.frogobox.appkeyboard.services

import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.emoji2.text.EmojiCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.frogobox.api.movie.ConsumeMovieApi
import com.frogobox.api.news.ConsumeNewsApi
import com.frogobox.appkeyboard.R
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.KeyboardFeatureModel
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.model.ThemeType
import com.frogobox.appkeyboard.suggestion.SuggestionResult
import com.frogobox.appkeyboard.suggestion.WordSuggestionEngine
import com.frogobox.appkeyboard.ui.autotext.AutoTextActivity
import com.frogobox.appkeyboard.ui.keyboard.autotext.AutoTextKeyboardViewModel
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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Modern 100% Full Pure Jetpack Compose InputMethodService.
 * Replaces legacy ViewBinding (keyboard_ime.xml, item_keyboard_header.xml, layout_keyboard_suggestion.xml)
 * with a unified ComposeView, reactive panel navigation, and direct Compose feature integrations.
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

    // Reactive State Holders
    private val activePanelStateFlow = MutableStateFlow(KeyboardPanelState.MAIN)
    private val themeTypeFlow = MutableStateFlow(ThemeType.COLOR)
    private val themeBackgroundResFlow = MutableStateFlow(R.color.color_bg_keyboard_default)
    private val featuresFlow = MutableStateFlow<List<KeyboardFeatureModel>>(emptyList())
    private val isSuggestionVisibleFlow = MutableStateFlow(false)
    private val suggestionResultFlow = MutableStateFlow(SuggestionResult.EMPTY)

    // Sub-Screen Reactive Data Flows
    private val autoTextListFlow = MutableStateFlow<List<AutoTextEntity>>(emptyList())
    private val newsArticlesFlow = MutableStateFlow<List<Article>>(emptyList())
    private val isNewsLoadingFlow = MutableStateFlow(false)
    private val movieListFlow = MutableStateFlow<List<TrendingMovie>>(emptyList())
    private val isMovieLoadingFlow = MutableStateFlow(false)

    // Emoji State Flows
    private val emojisFlow = MutableStateFlow<List<String>>(emptyList())
    private val selectedEmojiCategoryFlow = MutableStateFlow(EmojiCategoryType.GENERAL)
    private val isEmojiLoadingFlow = MutableStateFlow(false)
    private val emojiCache = mutableMapOf<String, List<String>>()
    private val systemFontPaint = Paint().apply { typeface = Typeface.DEFAULT }
    private val emojiCompatMetadataVersion = 0

    // Coroutine Scope & View Models
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private val autoTextViewModel by lazy { AutoTextKeyboardViewModel(this) }

    // Interop MainKeyboard Instance
    private var mainKeyboardInstance: MainKeyboard? = null
    private val currentKeyboardFlow = MutableStateFlow<ItemMainKeyboard?>(null)

    override fun onCreate() {
        super.onCreate()
        imeLifecycleOwner.onCreate()
    }

    override fun onWindowShown() {
        super.onWindowShown()
        imeLifecycleOwner.onStart()
        imeLifecycleOwner.onResume()
        applySoundAndHapticSettings()
        setupTheme()
        setupFeatureKeyboard()
        loadAutoText()
        showMainKeyboard()
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        imeLifecycleOwner.onPause()
        imeLifecycleOwner.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
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
                    val newsArticles by newsArticlesFlow.collectAsState()
                    val isNewsLoading by isNewsLoadingFlow.collectAsState()
                    val movieList by movieListFlow.collectAsState()
                    val isMovieLoading by isMovieLoadingFlow.collectAsState()

                    val emojis by emojisFlow.collectAsState()
                    val selectedEmojiCategory by selectedEmojiCategoryFlow.collectAsState()
                    val isEmojiLoading by isEmojiLoadingFlow.collectAsState()

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
                            this@KeyboardIME.onText(emoji)
                            this@KeyboardIME.onActionUp()
                        },
                        onCommitText = { text ->
                            getActiveInputConnection()?.commitText(text, 1)
                        },
                        onBackToMain = {
                            showMainKeyboard()
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
    }

    override fun setupFeatureKeyboard() {
        featuresFlow.value = keyboardUtil.menuKeyboard()
    }

    override fun invalidateKeyboard() {
        setupTheme()
        setupFeatureKeyboard()
        loadAutoText()
    }

    override fun initView() {
        suggestionEngine.loadDictionaryFromAsset(this)
        setupTheme()
        setupFeatureKeyboard()
        loadAutoText()
        openEmojiPalette()
    }

    override fun onKey(code: Int) {
        val ic = getActiveInputConnection() ?: return
        onKeyExt(code, ic)

        if (keyboardUtil.isSuggestionEnabled()) {
            val word = getWordBeforeCursor(ic)
            if (word.isNotEmpty()) {
                val suggestions = suggestionEngine.getSuggestions(word)
                suggestionResultFlow.value = suggestions
                isSuggestionVisibleFlow.value = true
            } else {
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

            KeyboardFeatureType.TEMPLATE_TEXT_GAME,
            KeyboardFeatureType.TEMPLATE_TEXT_APP,
            KeyboardFeatureType.TEMPLATE_TEXT_SALE,
            KeyboardFeatureType.TEMPLATE_TEXT_LOVE,
            KeyboardFeatureType.TEMPLATE_TEXT_GREETING -> {
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
        autoTextViewModel.getAutoText { items ->
            autoTextListFlow.value = items
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

    private fun openEmojiPalette() {
        selectedEmojiCategoryFlow.value = EmojiCategoryType.GENERAL
        loadEmojis(EmojiCategoryType.GENERAL.path)
    }

    private fun loadEmojis(path: String) {
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