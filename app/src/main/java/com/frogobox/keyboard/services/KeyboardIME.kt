package com.frogobox.keyboard.services

import android.inputmethodservice.InputMethodService
import android.os.Build
import android.text.InputType
import android.text.InputType.TYPE_CLASS_DATETIME
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.InputType.TYPE_CLASS_PHONE
import android.text.InputType.TYPE_MASK_CLASS
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.EditorInfo.IME_ACTION_NONE
import android.view.inputmethod.EditorInfo.IME_FLAG_NO_ENTER_ACTION
import android.view.inputmethod.EditorInfo.IME_MASK_ACTION
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.frogobox.keyboard.R
import com.frogobox.keyboard.common.ext.getProperBackgroundColor
import com.frogobox.keyboard.common.ext.getProperTextColor
import com.frogobox.keyboard.databinding.ItemKeyboardHeaderBinding
import com.frogobox.keyboard.databinding.KeyboardImeBinding
import com.frogobox.keyboard.model.KeyboardHeaderData
import com.frogobox.keyboard.model.KeyboardHeaderType
import com.frogobox.keyboard.ui.keyboard.main.ItemMainKeyboard
import com.frogobox.keyboard.ui.keyboard.main.ItemMainKeyboard.Companion.SHIFT_OFF
import com.frogobox.keyboard.ui.keyboard.main.ItemMainKeyboard.Companion.SHIFT_ON_ONE_CHAR
import com.frogobox.keyboard.ui.keyboard.main.ItemMainKeyboard.Companion.SHIFT_ON_PERMANENT
import com.frogobox.keyboard.ui.keyboard.main.OnKeyboardActionListener
import com.frogobox.keyboard.ui.keyboard.playstore.PlayStoreType
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding

// based on https://www.androidauthority.com/lets-build-custom-keyboard-android-832362/
class KeyboardIME : InputMethodService(), OnKeyboardActionListener {

    // how quickly do we have to doubletap shift to enable permanent caps lock
    private var SHIFT_PERM_TOGGLE_SPEED = 500
    private val KEYBOARD_LETTERS = 0
    private val KEYBOARD_SYMBOLS = 1
    private val KEYBOARD_SYMBOLS_SHIFT = 2
    private val KEYBOARD_NUMBER = 3
    private val KEYCODE_EMOJI = -6

    private var keyboard: ItemMainKeyboard? = null

    private var lastShiftPressTS = 0L
    private var keyboardMode = KEYBOARD_LETTERS
    private var inputTypeClass = InputType.TYPE_CLASS_TEXT
    private var enterKeyType = IME_ACTION_NONE
    private var switchToLetters = false

    private var binding: KeyboardImeBinding? = null

    override fun onCreate() {
        setTheme(R.style.Theme_Research)
        super.onCreate()
    }

    override fun onWindowShown() {
        super.onWindowShown()
        binding?.keyboardAutotext?.setupData()
        showMainKeyboard()
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        binding?.keyboardAutotext?.setupData()
    }

    override fun onInitializeInterface() {
        super.onInitializeInterface()
        keyboard = ItemMainKeyboard(this, getKeyboardLayoutXML(), enterKeyType)
    }

    private fun initCurrentInputConnection() {
        binding?.apply {
            keyboardAutotext.setInputConnection(currentInputConnection)
            keyboardNews.setInputConnection(currentInputConnection)
            keyboardMoview.setInputConnection(currentInputConnection)
            keyboardWebview.setInputConnection(currentInputConnection)
            keyboardForm.setInputConnection(currentInputConnection)
            keyboardEmoji.setInputConnection(currentInputConnection)
            keyboardPlaystore.setInputConnection(currentInputConnection)
        }
    }

    override fun onCreateInputView(): View {
        binding = KeyboardImeBinding.inflate(LayoutInflater.from(this), null, false)
        binding?.keyboardMain?.setKeyboard(keyboard!!)
        binding?.mockMeasureHeightKeyboardMain?.setKeyboard(keyboard!!)
        binding?.keyboardMain?.mOnKeyboardActionListener = this
        binding?.keyboardEmoji?.mOnKeyboardActionListener = this
        initCurrentInputConnection()
        initView()
        return binding!!.root
    }

    override fun onPress(primaryCode: Int) {
        if (primaryCode != 0) {
            binding?.keyboardMain?.vibrateIfNeeded()
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        inputTypeClass = attribute!!.inputType and TYPE_MASK_CLASS
        enterKeyType = attribute.imeOptions and (IME_MASK_ACTION or IME_FLAG_NO_ENTER_ACTION)

        val keyboardXml = when (inputTypeClass) {
            TYPE_CLASS_NUMBER -> {
                keyboardMode = KEYBOARD_NUMBER
                R.xml.keys_number
            }

            TYPE_CLASS_DATETIME, TYPE_CLASS_PHONE -> {
                keyboardMode = KEYBOARD_SYMBOLS
                R.xml.keys_symbols
            }
            else -> {
                keyboardMode = KEYBOARD_LETTERS
                getKeyboardLayoutXML()
            }
        }
        keyboard = ItemMainKeyboard(this, keyboardXml, enterKeyType)
        binding?.keyboardMain?.setKeyboard(keyboard!!)
        binding?.mockMeasureHeightKeyboardMain?.setKeyboard(keyboard!!)
        initCurrentInputConnection()
        updateShiftKeyState()
    }

    private fun hideMainKeyboard() {
        binding?.keyboardMain?.visibility = View.GONE
        binding?.keyboardHeader?.visibility = View.GONE
        binding?.mockMeasureHeightKeyboard?.visibility = View.INVISIBLE
    }

    private fun showMainKeyboard() {
        binding?.keyboardMain?.visibility = View.VISIBLE
        binding?.keyboardHeader?.visibility = View.VISIBLE
        binding?.mockMeasureHeightKeyboard?.visibility = View.GONE
    }

    private fun showOnlyKeyboard() {
        binding?.keyboardMain?.visibility = View.VISIBLE
    }

    private fun hideOnlyKeyboard() {
        binding?.keyboardMain?.visibility = View.GONE
    }

    private fun EditText.showKeyboardExt() {
        setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showOnlyKeyboard()
            }
        }
        setOnClickListener {
            showOnlyKeyboard()
        }
    }

    private fun initBackToMainKeyboard() {
        binding?.apply {
            keyboardAutotext.binding?.toolbarBack?.setOnClickListener {
                keyboardAutotext.visibility = View.GONE
                showMainKeyboard()
            }

            keyboardNews.binding?.toolbarBack?.setOnClickListener {
                keyboardNews.visibility = View.GONE
                showMainKeyboard()
            }

            keyboardMoview.binding?.toolbarBack?.setOnClickListener {
                keyboardMoview.visibility = View.GONE
                showMainKeyboard()
            }

            keyboardWebview.binding?.toolbarBack?.setOnClickListener {
                keyboardWebview.visibility = View.GONE
                showMainKeyboard()
            }

            keyboardForm.binding?.toolbarBack?.setOnClickListener {
                keyboardForm.visibility = View.GONE
                showMainKeyboard()
            }

            keyboardEmoji.binding?.emojiPaletteClose?.setOnClickListener {
                keyboardEmoji.visibility = View.GONE
                keyboardEmoji.binding?.emojisList?.scrollToPosition(0)
                showMainKeyboard()
            }

            keyboardPlaystore.binding?.toolbarBack?.setOnClickListener {
                keyboardPlaystore.visibility = View.GONE
                showMainKeyboard()
            }

        }
    }

    private fun initView() {

        binding?.apply {
            keyboardHeader.injectorBinding<KeyboardHeaderData, ItemKeyboardHeaderBinding>()
                .addData(KeyboardUtil.menuKeyboard())
                .addCallback(object :
                    IFrogoBindingAdapter<KeyboardHeaderData, ItemKeyboardHeaderBinding> {

                    override fun setViewBinding(parent: ViewGroup): ItemKeyboardHeaderBinding {
                        return ItemKeyboardHeaderBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    }

                    override fun setupInitComponent(
                        binding: ItemKeyboardHeaderBinding,
                        data: KeyboardHeaderData,
                        position: Int,
                        notifyListener: FrogoRecyclerNotifyListener<KeyboardHeaderData>,
                    ) {
                        binding.ivIcon.setImageResource(data.icon)
                        binding.tvTitle.text = data.type.title
                    }

                    override fun onItemClicked(
                        binding: ItemKeyboardHeaderBinding,
                        data: KeyboardHeaderData,
                        position: Int,
                        notifyListener: FrogoRecyclerNotifyListener<KeyboardHeaderData>,
                    ) {

                        when (data.type) {
                            KeyboardHeaderType.NEWS -> {
                                hideMainKeyboard()
                                keyboardNews.visibility = View.VISIBLE
                            }

                            KeyboardHeaderType.MOVIE -> {
                                hideMainKeyboard()
                                keyboardMoview.visibility = View.VISIBLE
                            }

                            KeyboardHeaderType.WEB -> {
                                mockMeasureHeightKeyboard.visibility = View.INVISIBLE
                                keyboardHeader.visibility = View.GONE
                                keyboardWebview.visibility = View.VISIBLE
                            }

                            KeyboardHeaderType.FORM -> {
                                hideMainKeyboard()

                                keyboardForm.visibility = View.VISIBLE
                                keyboardForm.binding?.etText?.showKeyboardExt()
                                keyboardForm.binding?.etText2?.showKeyboardExt()
                                keyboardForm.binding?.etText3?.showKeyboardExt()

                                keyboardForm.setOnClickListener {
                                    hideOnlyKeyboard()
                                }
                            }

                            KeyboardHeaderType.AUTO_TEXT -> {
                                hideMainKeyboard()
                                keyboardAutotext.visibility = View.VISIBLE
                            }
                            KeyboardHeaderType.PLAY_STORE_APP -> {
                                hideMainKeyboard()
                                keyboardPlaystore.setupTypePlayStore(PlayStoreType.APP)
                                keyboardPlaystore.visibility = View.VISIBLE
                            }
                            KeyboardHeaderType.PLAY_STORE_GAME -> {
                                hideMainKeyboard()
                                keyboardPlaystore.setupTypePlayStore(PlayStoreType.GAME)
                                keyboardPlaystore.visibility = View.VISIBLE
                            }
                        }

                    }

                    override fun onItemLongClicked(
                        binding: ItemKeyboardHeaderBinding,
                        data: KeyboardHeaderData,
                        position: Int,
                        notifyListener: FrogoRecyclerNotifyListener<KeyboardHeaderData>,
                    ) {
                    }


                })
                .createLayoutGrid(KeyboardUtil.menuKeyboard().size)
                .build()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding?.keyboardEmoji?.setupEmojiPalette(
                toolbarColor = ContextCompat.getColor(
                    binding?.keyboardEmoji?.context!!,
                    R.color.keyboard_toolbar_emoji_color
                ),
                backgroundColor = binding?.keyboardEmoji?.context!!.getProperBackgroundColor(),
                textColor = binding?.keyboardEmoji?.context!!.getProperTextColor()
            )
        }

        initBackToMainKeyboard()

    }

    private fun updateShiftKeyState() {
        if (keyboardMode == KEYBOARD_LETTERS) {
            val editorInfo = currentInputEditorInfo
            if (editorInfo != null && editorInfo.inputType != InputType.TYPE_NULL && keyboard?.mShiftState != SHIFT_ON_PERMANENT) {
                if (currentInputConnection.getCursorCapsMode(editorInfo.inputType) != 0) {
                    keyboard?.setShifted(SHIFT_ON_ONE_CHAR)
                    binding?.keyboardMain?.invalidateAllKeys()
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onKey(code: Int) {

        val formView = binding?.keyboardForm
        var inputConnection = currentInputConnection

        if (formView?.visibility == View.VISIBLE) {
            val et1 = formView.binding?.etText
            val et1Connection = et1?.onCreateInputConnection(EditorInfo())

            val et2 = formView.binding?.etText2
            val et2Connection = et2?.onCreateInputConnection(EditorInfo())

            val et3 = formView.binding?.etText3
            val et3Connection = et3?.onCreateInputConnection(EditorInfo())

            if (et1?.isFocused == true) {
                inputConnection = et1Connection

            } else if (et2?.isFocused == true) {
                inputConnection = et2Connection
            } else if (et3?.isFocused == true) {
                inputConnection = et3Connection
            }

        } else if (binding?.keyboardWebview?.visibility == View.VISIBLE) {
            inputConnection =
                binding?.keyboardWebview?.binding?.webview?.onCreateInputConnection(EditorInfo())
        } else {
            inputConnection = currentInputConnection
        }
        onKeyExt(code, inputConnection)
    }

    override fun onActionUp() {
        if (switchToLetters) {
            keyboardMode = KEYBOARD_LETTERS
            keyboard = ItemMainKeyboard(this, getKeyboardLayoutXML(), enterKeyType)

            val editorInfo = currentInputEditorInfo
            if (editorInfo != null && editorInfo.inputType != InputType.TYPE_NULL && keyboard?.mShiftState != SHIFT_ON_PERMANENT) {
                if (currentInputConnection.getCursorCapsMode(editorInfo.inputType) != 0) {
                    keyboard?.setShifted(SHIFT_ON_ONE_CHAR)
                }
            }

            binding?.keyboardMain?.setKeyboard(keyboard!!)
            switchToLetters = false
        }
    }

    override fun moveCursorLeft() {
        moveCursor(false)
    }

    override fun moveCursorRight() {
        moveCursor(true)
    }

    override fun onText(text: String) {
        currentInputConnection?.commitText(text, 0)
    }

    override fun onUpdateSelection(
        oldSelStart: Int,
        oldSelEnd: Int,
        newSelStart: Int,
        newSelEnd: Int,
        candidatesStart: Int,
        candidatesEnd: Int,
    ) {
        super.onUpdateSelection(
            oldSelStart,
            oldSelEnd,
            newSelStart,
            newSelEnd,
            candidatesStart,
            candidatesEnd
        )

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun onKeyExt(code: Int, inputConnection: InputConnection) {
        if (keyboard == null || inputConnection == null) {
            return
        }

        if (code != ItemMainKeyboard.KEYCODE_SHIFT) {
            lastShiftPressTS = 0
        }

        when (code) {
            ItemMainKeyboard.KEYCODE_DELETE -> {
                if (keyboard!!.mShiftState == SHIFT_ON_ONE_CHAR) {
                    keyboard!!.mShiftState = SHIFT_OFF
                }

                val selectedText = inputConnection.getSelectedText(0)
                if (TextUtils.isEmpty(selectedText)) {
                    inputConnection.sendKeyEvent(
                        KeyEvent(
                            KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_DEL
                        )
                    )
                    inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
                } else {
                    inputConnection.commitText("", 1)
                }

                if (inputConnection != currentInputConnection) {
                    inputConnection.deleteSurroundingText(1, 0)
                }

                binding?.keyboardMain?.invalidateAllKeys()
            }
            ItemMainKeyboard.KEYCODE_SHIFT -> {
                if (keyboardMode == KEYBOARD_LETTERS) {
                    when {
                        keyboard!!.mShiftState == SHIFT_ON_PERMANENT -> keyboard!!.mShiftState =
                            SHIFT_OFF
                        System.currentTimeMillis() - lastShiftPressTS < SHIFT_PERM_TOGGLE_SPEED -> keyboard!!.mShiftState =
                            SHIFT_ON_PERMANENT
                        keyboard!!.mShiftState == SHIFT_ON_ONE_CHAR -> keyboard!!.mShiftState =
                            SHIFT_OFF
                        keyboard!!.mShiftState == SHIFT_OFF -> keyboard!!.mShiftState =
                            SHIFT_ON_ONE_CHAR
                    }

                    lastShiftPressTS = System.currentTimeMillis()
                } else {
                    val keyboardXml = if (keyboardMode == KEYBOARD_SYMBOLS) {
                        keyboardMode = KEYBOARD_SYMBOLS_SHIFT
                        R.xml.keys_symbols_shift
                    } else {
                        keyboardMode = KEYBOARD_SYMBOLS
                        R.xml.keys_symbols
                    }
                    keyboard = ItemMainKeyboard(this, keyboardXml, enterKeyType)
                    binding?.keyboardMain?.setKeyboard(keyboard!!)
                }
                binding?.keyboardMain?.invalidateAllKeys()
            }
            ItemMainKeyboard.KEYCODE_ENTER -> {
                val imeOptionsActionId = getImeOptionsActionId()
                if (imeOptionsActionId != IME_ACTION_NONE) {
                    inputConnection.performEditorAction(imeOptionsActionId)
                } else {
                    inputConnection.sendKeyEvent(
                        KeyEvent(
                            KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_ENTER
                        )
                    )
                    inputConnection.sendKeyEvent(
                        KeyEvent(
                            KeyEvent.ACTION_UP,
                            KeyEvent.KEYCODE_ENTER
                        )
                    )
                }

                if (inputConnection != currentInputConnection) {
                    inputConnection.commitText("\n", 1)
                }

            }
            ItemMainKeyboard.KEYCODE_MODE_CHANGE -> {
                val keyboardXml = if (keyboardMode == KEYBOARD_LETTERS) {
                    keyboardMode = KEYBOARD_SYMBOLS
                    R.xml.keys_symbols
                } else {
                    keyboardMode = KEYBOARD_LETTERS
                    getKeyboardLayoutXML()
                }
                keyboard = ItemMainKeyboard(this, keyboardXml, enterKeyType)
                binding?.keyboardMain?.setKeyboard(keyboard!!)
            }
            ItemMainKeyboard.KEYCODE_EMOJI -> {
                binding?.keyboardEmoji?.visibility = View.VISIBLE
                hideMainKeyboard()
                binding?.keyboardEmoji?.openEmojiPalette()
            }
            else -> {
                var codeChar = code.toChar()
                if (Character.isLetter(codeChar) && keyboard!!.mShiftState > SHIFT_OFF) {
                    codeChar = Character.toUpperCase(codeChar)
                }

                // If the keyboard is set to symbols and the user presses space, we usually should switch back to the letters keyboard.
                // However, avoid doing that in cases when the EditText for example requires numbers as the input.
                // We can detect that by the text not changing on pressing Space.
                if (keyboardMode != KEYBOARD_LETTERS && code == ItemMainKeyboard.KEYCODE_SPACE) {
                    val originalText =
                        inputConnection.getExtractedText(ExtractedTextRequest(), 0)?.text ?: return
                    inputConnection.commitText(codeChar.toString(), 1)
                    val newText = inputConnection.getExtractedText(ExtractedTextRequest(), 0).text
                    switchToLetters = originalText != newText
                } else {
                    inputConnection.commitText(codeChar.toString(), 1)
                }

                if (keyboard!!.mShiftState == SHIFT_ON_ONE_CHAR && keyboardMode == KEYBOARD_LETTERS) {
                    keyboard!!.mShiftState = SHIFT_OFF
                    binding?.keyboardMain?.invalidateAllKeys()
                }
            }
        }

        if (code != ItemMainKeyboard.KEYCODE_SHIFT) {
            updateShiftKeyState()
        }
    }

    private fun moveCursor(moveRight: Boolean) {
        val extractedText =
            currentInputConnection?.getExtractedText(ExtractedTextRequest(), 0) ?: return
        var newCursorPosition = extractedText.selectionStart
        newCursorPosition = if (moveRight) {
            newCursorPosition + 1
        } else {
            newCursorPosition - 1
        }

        currentInputConnection?.setSelection(newCursorPosition, newCursorPosition)
    }

    private fun getImeOptionsActionId(): Int {
        return if (currentInputEditorInfo.imeOptions and IME_FLAG_NO_ENTER_ACTION != 0) {
            IME_ACTION_NONE
        } else {
            currentInputEditorInfo.imeOptions and IME_MASK_ACTION
        }
    }

    private fun getKeyboardLayoutXML(): Int {
        return R.xml.keys_letters_qwerty
    }

}