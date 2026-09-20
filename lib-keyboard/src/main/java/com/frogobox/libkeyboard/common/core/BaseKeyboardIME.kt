package com.frogobox.libkeyboard.common.core

import android.inputmethodservice.InputMethodService
import android.text.InputType
import android.text.InputType.TYPE_CLASS_DATETIME
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.InputType.TYPE_CLASS_PHONE
import android.text.InputType.TYPE_MASK_CLASS
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.EditorInfo.IME_ACTION_NONE
import android.view.inputmethod.EditorInfo.IME_FLAG_NO_ENTER_ACTION
import android.view.inputmethod.EditorInfo.IME_MASK_ACTION
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.widget.EditText
import androidx.viewbinding.ViewBinding
import com.frogobox.libkeyboard.R
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard.Companion.SHIFT_OFF
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard.Companion.SHIFT_ON_ONE_CHAR
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard.Companion.SHIFT_ON_PERMANENT
import com.frogobox.libkeyboard.ui.main.OnKeyboardActionListener

// based on https://www.androidauthority.com/lets-build-custom-keyboard-android-832362/
abstract class BaseKeyboardIME<VB : ViewBinding> : InputMethodService(), OnKeyboardActionListener, IKeyboardIME {

    companion object {
        // How quickly do we have to doubletap shift to enable permanent caps lock
        const val SHIFT_PERM_TOGGLE_SPEED = 500L
        const val DOUBLE_SPACE_PERIOD_TIMEOUT = 350L

        const val KEYBOARD_LETTERS = 0
        const val KEYBOARD_SYMBOLS = 1
        const val KEYBOARD_SYMBOLS_SHIFT = 2
        const val KEYBOARD_NUMBER = 3
    }

    var keyboard: ItemMainKeyboard? = null

    var lastShiftPressTS = 0L
    var lastSpacePressTS = 0L
    var keyboardMode = KEYBOARD_LETTERS
    var inputTypeClass = InputType.TYPE_CLASS_TEXT
    var enterKeyType = IME_ACTION_NONE
    var switchToLetters = false

    var binding: VB? = null

    abstract fun setupViewBinding() : VB

    override fun onCreate() {
        setTheme(R.style.Theme_Research)
        super.onCreate()
    }

    override fun onWindowShown() {
        super.onWindowShown()
        invalidateKeyboard()
        showMainKeyboard()
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        invalidateKeyboard()
    }

    override fun onInitializeInterface() {
        super.onInitializeInterface()
        keyboard = ItemMainKeyboard(this, getKeyboardLayoutXML(), enterKeyType)
    }

    override fun onCreateInputView(): View {
        binding = setupViewBinding()
        setupBinding()
        initCurrentInputConnection()
        initView()
        return binding!!.root
    }

    override fun onPress(primaryCode: Int) {
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        if (attribute == null) return
        inputTypeClass = attribute.inputType and TYPE_MASK_CLASS
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
        initialSetupKeyboard()

        initCurrentInputConnection()
        updateShiftKeyState()
    }

    override fun onKey(code: Int) {
        val inputConnection = currentInputConnection ?: return
        onKeyExt(code, inputConnection)
    }

    override fun onActionUp() {
        if (switchToLetters) {
            keyboardMode = KEYBOARD_LETTERS
            keyboard = ItemMainKeyboard(this, getKeyboardLayoutXML(), enterKeyType)

            val editorInfo = currentInputEditorInfo
            if (editorInfo != null && editorInfo.inputType != InputType.TYPE_NULL && keyboard?.mShiftState != SHIFT_ON_PERMANENT) {
                if (currentInputConnection?.getCursorCapsMode(editorInfo.inputType) != 0) {
                    keyboard?.setShifted(SHIFT_ON_ONE_CHAR)
                }
            }

            initialSetupKeyboard()
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
        currentInputConnection?.commitText(text, 1)
    }

    override fun initialSetupKeyboard() {}

    override fun setupTheme() {}

    override fun setupBinding() {
        initialSetupKeyboard()
        setupTheme()
    }

    override fun invalidateKeyboard() {
        setupTheme()
        setupFeatureKeyboard()
    }

    override fun initCurrentInputConnection() {}

    override fun hideMainKeyboard() {}

    override fun showMainKeyboard() {}

    override fun showOnlyKeyboard() {}

    override fun hideOnlyKeyboard() {}

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

    override fun initBackToMainKeyboard() {}

    override fun setupFeatureKeyboard() {}

    override fun initView() {
        setupFeatureKeyboard()
        initBackToMainKeyboard()
    }

    override fun invalidateAllKeys() {}
    override fun runEmojiBoard() {}

    override fun updateShiftKeyState() {
        if (keyboardMode == KEYBOARD_LETTERS) {
            val editorInfo = currentInputEditorInfo
            if (editorInfo != null && editorInfo.inputType != InputType.TYPE_NULL && keyboard?.mShiftState != SHIFT_ON_PERMANENT) {
                if (currentInputConnection?.getCursorCapsMode(editorInfo.inputType) != 0) {
                    keyboard?.setShifted(SHIFT_ON_ONE_CHAR)
                    invalidateAllKeys()
                }
            }
        }
    }

    override fun onKeyExt(code: Int, inputConnection: InputConnection) {
        val kb = keyboard ?: return

        if (code != ItemMainKeyboard.KEYCODE_SHIFT) {
            lastShiftPressTS = 0
        }

        when (code) {
            ItemMainKeyboard.KEYCODE_DELETE -> {
                lastSpacePressTS = 0L
                if (kb.mShiftState == SHIFT_ON_ONE_CHAR) {
                    kb.mShiftState = SHIFT_OFF
                }

                val selectedText = inputConnection.getSelectedText(0)
                if (TextUtils.isEmpty(selectedText)) {
                    inputConnection.sendKeyEvent(
                        KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)
                    )
                    inputConnection.sendKeyEvent(
                        KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL)
                    )
                } else {
                    inputConnection.commitText("", 1)
                }

                invalidateAllKeys()
            }
            ItemMainKeyboard.KEYCODE_SHIFT -> {
                lastSpacePressTS = 0L
                if (keyboardMode == KEYBOARD_LETTERS) {
                    when {
                        kb.mShiftState == SHIFT_ON_PERMANENT -> kb.mShiftState = SHIFT_OFF
                        System.currentTimeMillis() - lastShiftPressTS < SHIFT_PERM_TOGGLE_SPEED -> kb.mShiftState = SHIFT_ON_PERMANENT
                        kb.mShiftState == SHIFT_ON_ONE_CHAR -> kb.mShiftState = SHIFT_OFF
                        kb.mShiftState == SHIFT_OFF -> kb.mShiftState = SHIFT_ON_ONE_CHAR
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
                    initialSetupKeyboard()
                }
                invalidateAllKeys()
            }
            ItemMainKeyboard.KEYCODE_ENTER -> {
                lastSpacePressTS = 0L
                val imeOptionsActionId = getImeOptionsActionId()
                if (imeOptionsActionId != IME_ACTION_NONE) {
                    inputConnection.performEditorAction(imeOptionsActionId)
                } else {
                    inputConnection.sendKeyEvent(
                        KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
                    )
                    inputConnection.sendKeyEvent(
                        KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER)
                    )
                }
            }
            ItemMainKeyboard.KEYCODE_MODE_CHANGE -> {
                lastSpacePressTS = 0L
                val keyboardXml = if (keyboardMode == KEYBOARD_LETTERS) {
                    keyboardMode = KEYBOARD_SYMBOLS
                    R.xml.keys_symbols
                } else {
                    keyboardMode = KEYBOARD_LETTERS
                    getKeyboardLayoutXML()
                }
                keyboard = ItemMainKeyboard(this, keyboardXml, enterKeyType)
                initialSetupKeyboard()
            }
            ItemMainKeyboard.KEYCODE_EMOJI -> {
                lastSpacePressTS = 0L
                runEmojiBoard()
            }
            ItemMainKeyboard.KEYCODE_TAB -> {
                lastSpacePressTS = 0L
                val isMultiline = (currentInputEditorInfo?.inputType ?: 0) and InputType.TYPE_TEXT_FLAG_MULTI_LINE != 0
                if (isMultiline) {
                    inputConnection.commitText("\t", 1)
                } else {
                    inputConnection.sendKeyEvent(
                        KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_TAB)
                    )
                    inputConnection.sendKeyEvent(
                        KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_TAB)
                    )
                }
            }
            ItemMainKeyboard.KEYCODE_ARROW_LEFT -> {
                lastSpacePressTS = 0L
                moveCursor(false)
            }
            ItemMainKeyboard.KEYCODE_ARROW_RIGHT -> {
                lastSpacePressTS = 0L
                moveCursor(true)
            }
            ItemMainKeyboard.KEYCODE_ARROW_UP -> {
                lastSpacePressTS = 0L
                inputConnection.sendKeyEvent(
                    KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP)
                )
                inputConnection.sendKeyEvent(
                    KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP)
                )
            }
            ItemMainKeyboard.KEYCODE_ARROW_DOWN -> {
                lastSpacePressTS = 0L
                inputConnection.sendKeyEvent(
                    KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN)
                )
                inputConnection.sendKeyEvent(
                    KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN)
                )
            }
            else -> {
                if (code <= 0) return
                var codeChar = code.toChar()
                if (Character.isLetter(codeChar) && kb.mShiftState > SHIFT_OFF) {
                    codeChar = Character.toUpperCase(codeChar)
                }

                if (code == ItemMainKeyboard.KEYCODE_SPACE) {
                    val now = System.currentTimeMillis()
                    // Gboard behavior: Double tap space -> insert ". " and auto-shift
                    if (keyboardMode == KEYBOARD_LETTERS && (now - lastSpacePressTS) < DOUBLE_SPACE_PERIOD_TIMEOUT) {
                        val textBefore = inputConnection.getTextBeforeCursor(2, 0)
                        if (textBefore != null && textBefore.length >= 1 && textBefore.endsWith(" ") && !textBefore.endsWith(". ")) {
                            inputConnection.deleteSurroundingText(1, 0)
                            inputConnection.commitText(". ", 1)
                            lastSpacePressTS = 0L
                            if (kb.mShiftState == SHIFT_OFF) {
                                kb.mShiftState = SHIFT_ON_ONE_CHAR
                                invalidateAllKeys()
                            }
                            updateShiftKeyState()
                            return
                        }
                    }
                    lastSpacePressTS = now

                    inputConnection.commitText(" ", 1)

                    if (keyboardMode != KEYBOARD_LETTERS &&
                        inputTypeClass != TYPE_CLASS_NUMBER &&
                        inputTypeClass != TYPE_CLASS_PHONE &&
                        inputTypeClass != TYPE_CLASS_DATETIME) {
                        switchToLetters = true
                    }
                } else {
                    lastSpacePressTS = 0L
                    inputConnection.commitText(codeChar.toString(), 1)
                }

                if (kb.mShiftState == SHIFT_ON_ONE_CHAR && keyboardMode == KEYBOARD_LETTERS) {
                    kb.mShiftState = SHIFT_OFF
                    invalidateAllKeys()
                }
            }
        }

        if (code != ItemMainKeyboard.KEYCODE_SHIFT) {
            updateShiftKeyState()
        }
    }

    override fun moveCursor(moveRight: Boolean) {
        val ic = currentInputConnection ?: return
        val extractedText = ic.getExtractedText(ExtractedTextRequest(), 0)
        if (extractedText != null && extractedText.text != null) {
            var newCursorPosition = extractedText.selectionStart
            val textLength = extractedText.text.length
            newCursorPosition = if (moveRight) {
                (newCursorPosition + 1).coerceAtMost(textLength)
            } else {
                (newCursorPosition - 1).coerceAtLeast(0)
            }
            ic.setSelection(newCursorPosition, newCursorPosition)
        } else {
            // Fallback for custom editors or WebViews that don't support ExtractedText
            val keyCode = if (moveRight) KeyEvent.KEYCODE_DPAD_RIGHT else KeyEvent.KEYCODE_DPAD_LEFT
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
        }
    }

    override fun deleteWordsBeforeCursor(count: Int) {
        val ic = currentInputConnection ?: return
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

    override fun getImeOptionsActionId(): Int {
        val editorInfo = currentInputEditorInfo ?: return IME_ACTION_NONE
        return if (editorInfo.imeOptions and IME_FLAG_NO_ENTER_ACTION != 0) {
            IME_ACTION_NONE
        } else {
            editorInfo.imeOptions and IME_MASK_ACTION
        }
    }

    override fun getKeyboardLayoutXML(): Int {
        return R.xml.keys_letters_qwerty
    }

}