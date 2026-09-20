package com.frogobox.appkeyboard.services

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.frogobox.appkeyboard.R
import com.frogobox.appkeyboard.databinding.ItemKeyboardHeaderBinding
import com.frogobox.appkeyboard.databinding.KeyboardImeBinding
import com.frogobox.appkeyboard.model.KeyboardFeatureModel
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.model.ThemeType
import com.frogobox.appkeyboard.ui.main.MainActivity
import com.frogobox.libkeyboard.common.core.BaseKeyboardIME
import com.frogobox.libkeyboard.common.sound.MechanicalSoundManager
import com.frogobox.libkeyboard.common.sound.MechanicalSoundType
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding
import com.frogobox.sdk.delegate.preference.PreferenceDelegates
import com.frogobox.sdk.ext.getColorExt
import com.frogobox.sdk.ext.gone
import com.frogobox.sdk.ext.invisible
import com.frogobox.appkeyboard.suggestion.WordSuggestionEngine
import com.frogobox.sdk.ext.visible
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class KeyboardIME : BaseKeyboardIME<KeyboardImeBinding>() {

    private val imeLifecycleOwner = ImeLifecycleOwner()

    @Inject
    lateinit var pref: PreferenceDelegates

    @Inject
    lateinit var keyboardUtil: KeyboardUtil

    @Inject
    lateinit var suggestionEngine: WordSuggestionEngine

    override fun setupViewBinding(): KeyboardImeBinding {
        return KeyboardImeBinding.inflate(LayoutInflater.from(this), null, false)
    }

    private val featureKeyboardCallback = object :
        IFrogoBindingAdapter<KeyboardFeatureModel, ItemKeyboardHeaderBinding> {

        override fun areContentsTheSame(
            oldItem: KeyboardFeatureModel,
            newItem: KeyboardFeatureModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(
            oldItem: KeyboardFeatureModel,
            newItem: KeyboardFeatureModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun setViewBinding(parent: ViewGroup): ItemKeyboardHeaderBinding {
            return ItemKeyboardHeaderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        }

        override fun setupInitComponent(
            binding: ItemKeyboardHeaderBinding,
            data: KeyboardFeatureModel,
            position: Int,
            notifyListener: FrogoRecyclerNotifyListener<KeyboardFeatureModel>,
        ) {
            binding.ivIcon.setImageResource(data.icon)
            binding.tvTitle.text = data.text

            if (getStateToggle(data.id)) {
                binding.root.visible()
            } else {
                binding.root.gone()
            }

        }

        override fun onItemClicked(
            binding: ItemKeyboardHeaderBinding,
            data: KeyboardFeatureModel,
            position: Int,
            notifyListener: FrogoRecyclerNotifyListener<KeyboardFeatureModel>,
        ) {

            when (KeyboardFeatureType.from(data.id)) {
                KeyboardFeatureType.NEWS -> {
                    hideMainKeyboard()
                    this@KeyboardIME.binding?.keyboardNews?.visible()
                }

                KeyboardFeatureType.MOVIE -> {
                    hideMainKeyboard()
                    this@KeyboardIME.binding?.keyboardMoview?.visible()
                }

                KeyboardFeatureType.WEB -> {
                    this@KeyboardIME.binding?.keyboardHeader?.gone()
                    this@KeyboardIME.binding?.keyboardWebview?.visible()
                }

                KeyboardFeatureType.FORM -> {
                    this@KeyboardIME.binding?.keyboardHeader?.gone()
                    this@KeyboardIME.binding?.keyboardForm?.visible()
                }

                KeyboardFeatureType.AUTO_TEXT -> {
                    hideMainKeyboard()
                    this@KeyboardIME.binding?.keyboardAutotext?.visible()
                }

                KeyboardFeatureType.TEMPLATE_TEXT_GAME -> {
                    hideMainKeyboard()
                    this@KeyboardIME.binding?.keyboardTemplateText?.setupTemplateTextType(KeyboardFeatureType.TEMPLATE_TEXT_GAME)
                    this@KeyboardIME.binding?.keyboardTemplateText?.visible()
                }

                KeyboardFeatureType.TEMPLATE_TEXT_APP -> {
                    hideMainKeyboard()
                    this@KeyboardIME.binding?.keyboardTemplateText?.setupTemplateTextType(KeyboardFeatureType.TEMPLATE_TEXT_APP)
                    this@KeyboardIME.binding?.keyboardTemplateText?.visible()
                }

                KeyboardFeatureType.TEMPLATE_TEXT_SALE -> {
                    hideMainKeyboard()
                    this@KeyboardIME.binding?.keyboardTemplateText?.setupTemplateTextType(KeyboardFeatureType.TEMPLATE_TEXT_SALE)
                    this@KeyboardIME.binding?.keyboardTemplateText?.visible()
                }

                KeyboardFeatureType.TEMPLATE_TEXT_LOVE -> {
                    hideMainKeyboard()
                    this@KeyboardIME.binding?.keyboardTemplateText?.setupTemplateTextType(KeyboardFeatureType.TEMPLATE_TEXT_LOVE)
                    this@KeyboardIME.binding?.keyboardTemplateText?.visible()
                }

                KeyboardFeatureType.TEMPLATE_TEXT_GREETING -> {
                    hideMainKeyboard()
                    this@KeyboardIME.binding?.keyboardTemplateText?.setupTemplateTextType(KeyboardFeatureType.TEMPLATE_TEXT_GREETING)
                    this@KeyboardIME.binding?.keyboardTemplateText?.visible()
                }

                KeyboardFeatureType.SUGGESTION -> {
                    this@KeyboardIME.showSuggestionBar()
                }

                KeyboardFeatureType.CHANGE_KEYBOARD -> {
                    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
                }

                KeyboardFeatureType.SETTING -> {
                    binding.root.context.startActivity(Intent(
                        binding.root.context, MainActivity::class.java
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                }

            }

        }

    }

    override fun setupTheme() {
        binding?.apply {

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

            when (backgroundType) {
                ThemeType.COLOR -> {
                    ivBackgroundKeyboard.setImageDrawable(null)
                    ivBackgroundKeyboard.setBackgroundColor(getColorExt(background))
                }
                ThemeType.IMAGE -> {
                    ivBackgroundKeyboard.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    ivBackgroundKeyboard.setImageResource(background)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        imeLifecycleOwner.onCreate()
    }

    override fun onWindowShown() {
        super.onWindowShown()
        imeLifecycleOwner.onStart()
        imeLifecycleOwner.onResume()
        applySoundAndHapticSettings()
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        imeLifecycleOwner.onPause()
        imeLifecycleOwner.onStop()
    }

    override fun initialSetupKeyboard() {
        applySoundAndHapticSettings()
        binding?.keyboardMain?.setKeyboard(keyboard!!)
    }

    override fun setupBinding() {
        super.setupBinding()
        binding?.apply {
            keyboardMain.mOnKeyboardActionListener = this@KeyboardIME
            keyboardEmoji.mOnKeyboardActionListener = this@KeyboardIME

            root.setViewTreeLifecycleOwner(imeLifecycleOwner)
            root.setViewTreeViewModelStoreOwner(imeLifecycleOwner)
            root.setViewTreeSavedStateRegistryOwner(imeLifecycleOwner)
        }
        window?.window?.decorView?.let { decor ->
            decor.setViewTreeLifecycleOwner(imeLifecycleOwner)
            decor.setViewTreeViewModelStoreOwner(imeLifecycleOwner)
            decor.setViewTreeSavedStateRegistryOwner(imeLifecycleOwner)
        }
    }

    override fun invalidateKeyboard() {
        binding?.keyboardAutotext?.initData()
        setupFeatureKeyboard()
    }

    override fun initCurrentInputConnection() {
        binding?.apply {
            keyboardAutotext.setInputConnection(currentInputConnection)
            keyboardNews.setInputConnection(currentInputConnection)
            keyboardMoview.setInputConnection(currentInputConnection)
            keyboardWebview.setInputConnection(currentInputConnection)
            keyboardForm.setInputConnection(currentInputConnection)
            keyboardEmoji.setInputConnection(currentInputConnection)
            keyboardTemplateText.setInputConnection(currentInputConnection)
        }
    }

    override fun hideMainKeyboard() {
        binding?.apply {
            keyboardMain.invisible()
            keyboardHeader.invisible()
            keyboardSuggestion.gone()
        }
    }

    override fun showMainKeyboard() {
        binding?.apply {
            keyboardMain.visible()
            keyboardSuggestion.gone()
            if (keyboardUtil.menuKeyboard().isEmpty()) {
                keyboardHeader.gone()
            } else {
                keyboardHeader.visible()
            }
            keyboardAutotext.gone()
            keyboardNews.gone()
            keyboardMoview.gone()
            keyboardWebview.gone()
            keyboardForm.gone()
            keyboardEmoji.gone()
            keyboardEmoji.resetScroll()
        }
    }

    override fun showOnlyKeyboard() {
        binding?.keyboardMain?.visible()
    }

    override fun hideOnlyKeyboard() {
        binding?.keyboardMain?.gone()
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

    override fun initBackToMainKeyboard() {
        binding?.apply {
            keyboardAutotext.setOnBackClickListener {
                keyboardAutotext.gone()
                showMainKeyboard()
            }

            keyboardNews.setOnBackClickListener {
                keyboardNews.gone()
                showMainKeyboard()
            }

            keyboardMoview.setOnBackClickListener {
                keyboardMoview.gone()
                showMainKeyboard()
            }

            keyboardWebview.setOnBackClickListener {
                keyboardWebview.gone()
                showMainKeyboard()
            }

            keyboardForm.setOnBackClickListener {
                keyboardForm.gone()
                showMainKeyboard()
            }

            keyboardEmoji.setOnBackClickListener {
                keyboardEmoji.gone()
                keyboardEmoji.resetScroll()
                showMainKeyboard()
            }

            keyboardTemplateText.setOnBackClickListener {
                keyboardTemplateText.gone()
                showMainKeyboard()
            }

        }
    }

    override fun setupFeatureKeyboard() {
        val maxMenu = 4
        val gridSize = if (keyboardUtil.menuKeyboard().size <= maxMenu) {
            keyboardUtil.menuKeyboard().size
        } else if (keyboardUtil.menuKeyboard().size.mod(maxMenu) == 0) {
            maxMenu
        } else {
            maxMenu + 1
        }

        binding?.apply {
            if (keyboardUtil.menuKeyboard().isEmpty()) {
                keyboardHeader.gone()
            } else {
                keyboardHeader.visible()
                keyboardHeader.injectorBinding<KeyboardFeatureModel, ItemKeyboardHeaderBinding>()
                    .addData(keyboardUtil.menuKeyboard())
                    .addCallback(featureKeyboardCallback)
                    .createLayoutGrid(gridSize).build()
            }
        }
    }


    private fun getActiveInputConnection(): InputConnection? {
        return currentInputConnection
    }

    private fun getWordBeforeCursor(ic: InputConnection): String {
        val text = ic.getTextBeforeCursor(40, 0)?.toString() ?: ""
        return text.takeLastWhile { it.isLetterOrDigit() || it == '\'' }
    }

    private fun setupSuggestionBar() {
        binding?.keyboardSuggestion?.apply {
            onCandidateSelected = { selectedWord, _ ->
                val ic = getActiveInputConnection()
                if (ic != null) {
                    val currentWord = getWordBeforeCursor(ic)
                    if (currentWord.isNotEmpty()) {
                        ic.deleteSurroundingText(currentWord.length, 0)
                    }
                    ic.commitText("$selectedWord ", 1)
                    clearSuggestions()
                    showFeatureHeader()
                }
            }

            onSwitchMenuClicked = {
                showFeatureHeader()
            }

            onCloseClicked = {
                clearSuggestions()
                showFeatureHeader()
            }
        }
    }

    private fun showSuggestionBar() {
        binding?.apply {
            keyboardHeader.gone()
            keyboardSuggestion.visible()
        }
    }

    private fun showFeatureHeader() {
        binding?.apply {
            keyboardSuggestion.gone()
            if (keyboardUtil.menuKeyboard().isEmpty()) {
                keyboardHeader.gone()
            } else {
                keyboardHeader.visible()
            }
        }
    }

    override fun onKey(code: Int) {
        val ic = getActiveInputConnection() ?: return
        onKeyExt(code, ic)

        if (keyboardUtil.isSuggestionEnabled()) {
            val word = getWordBeforeCursor(ic)
            if (word.isNotEmpty()) {
                val suggestions = suggestionEngine.getSuggestions(word)
                binding?.keyboardSuggestion?.setSuggestions(suggestions)
                showSuggestionBar()
            } else {
                binding?.keyboardSuggestion?.clearSuggestions()
                if (code == ItemMainKeyboard.KEYCODE_SPACE ||
                    code == ItemMainKeyboard.KEYCODE_ENTER ||
                    code == ItemMainKeyboard.KEYCODE_DELETE) {
                    showFeatureHeader()
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

    override fun initView() {
        suggestionEngine.loadDictionaryFromAsset(this)
        setupSuggestionBar()
        setupFeatureKeyboard()
        initBackToMainKeyboard()
    }

    override fun invalidateAllKeys() {
        binding?.keyboardMain?.invalidateAllKeys()
    }

    
    override fun runEmojiBoard() {
        binding?.keyboardEmoji?.visible()
        binding?.keyboardMain?.invisible()
        binding?.keyboardHeader?.gone()
        binding?.keyboardEmoji?.openEmojiPalette()
    }

    override fun getKeyboardLayoutXML(): Int {
        return pref.getPrefInt(
            KeyboardUtil.KEYBOARD_TYPE, com.frogobox.libkeyboard.R.xml.keys_letters_qwerty
        )
    }

    private fun getStateToggle(key: String): Boolean {
        return pref.getPrefBoolean(key, true)
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

    override fun onDestroy() {
        super.onDestroy()
        imeLifecycleOwner.onDestroy()
        MechanicalSoundManager.getInstance(this).release()
    }

}