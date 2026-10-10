package com.frogobox.appkeyboard.util

import android.view.KeyEvent
import android.view.inputmethod.InputConnection

/**
 * Handles physical/Bluetooth keyboard interoperability when an external keyboard
 * (USB, Bluetooth, Folio cover, or Chromebook) is connected to the Android device.
 * Translates standard desktop productivity shortcuts (Ctrl+C, Ctrl+V, Ctrl+A, Ctrl+X, Ctrl+Z)
 * into InputConnection actions without requiring software keyboard interaction.
 */
object HardwareKeyboardHelper {

    /**
     * Checks if the given key code and ctrl state map to a supported desktop shortcut.
     */
    fun isShortcutAction(keyCode: Int, isCtrl: Boolean): Boolean {
        if (!isCtrl) return false
        return when (keyCode) {
            KeyEvent.KEYCODE_C,
            KeyEvent.KEYCODE_V,
            KeyEvent.KEYCODE_X,
            KeyEvent.KEYCODE_A,
            KeyEvent.KEYCODE_Z -> true
            else -> false
        }
    }

    /**
     * Checks if the key code corresponds to a hardware navigation key.
     */
    fun isNavigationKey(keyCode: Int): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_FORWARD_DEL -> true
            else -> false
        }
    }

    /**
     * Intercepts and executes hardware key events.
     *
     * @param event The [KeyEvent] received by the InputMethodService.
     * @param inputConnection The active [InputConnection] bound to the focused field.
     * @return True if the key event was intercepted and handled; false to allow default OS handling.
     */
    fun handleHardwareKeyEvent(event: KeyEvent, inputConnection: InputConnection?): Boolean {
        if (inputConnection == null) return false
        if (event.action != KeyEvent.ACTION_DOWN) return false

        // 1. Desktop Ctrl+Key combinations
        if (event.isCtrlPressed) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_C -> {
                    return inputConnection.performContextMenuAction(android.R.id.copy)
                }
                KeyEvent.KEYCODE_V -> {
                    return inputConnection.performContextMenuAction(android.R.id.paste)
                }
                KeyEvent.KEYCODE_X -> {
                    return inputConnection.performContextMenuAction(android.R.id.cut)
                }
                KeyEvent.KEYCODE_A -> {
                    return inputConnection.performContextMenuAction(android.R.id.selectAll)
                }
                KeyEvent.KEYCODE_Z -> {
                    return inputConnection.performContextMenuAction(android.R.id.undo)
                }
            }
        }

        // 2. Navigation & Arrow key handling
        when (event.keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT))
                inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT))
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT))
                inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT))
                return true
            }
            KeyEvent.KEYCODE_FORWARD_DEL -> {
                // Physical Delete key (delete character ahead of cursor)
                return inputConnection.deleteSurroundingText(0, 1)
            }
            KeyEvent.KEYCODE_ESCAPE -> {
                // Physical Escape key closes soft keyboard if visible
                return false
            }
        }

        return false
    }
}
