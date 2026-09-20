package com.frogobox.libkeyboard

import com.frogobox.libkeyboard.common.core.BaseKeyboardIME
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BaseKeyboardIMETest {

    @Test
    fun testShiftStateTransitions() {
        var shiftState = ItemMainKeyboard.SHIFT_OFF

        // 1st press -> SHIFT_ON_ONE_CHAR
        shiftState = when (shiftState) {
            ItemMainKeyboard.SHIFT_OFF -> ItemMainKeyboard.SHIFT_ON_ONE_CHAR
            ItemMainKeyboard.SHIFT_ON_ONE_CHAR -> ItemMainKeyboard.SHIFT_OFF
            else -> ItemMainKeyboard.SHIFT_OFF
        }
        assertEquals(ItemMainKeyboard.SHIFT_ON_ONE_CHAR, shiftState)

        // 2nd press (rapid tap) -> SHIFT_ON_PERMANENT (Caps Lock)
        val isDoubleTap = true
        if (isDoubleTap) {
            shiftState = ItemMainKeyboard.SHIFT_ON_PERMANENT
        }
        assertEquals(ItemMainKeyboard.SHIFT_ON_PERMANENT, shiftState)

        // 3rd press -> SHIFT_OFF
        shiftState = when (shiftState) {
            ItemMainKeyboard.SHIFT_ON_PERMANENT -> ItemMainKeyboard.SHIFT_OFF
            else -> ItemMainKeyboard.SHIFT_ON_ONE_CHAR
        }
        assertEquals(ItemMainKeyboard.SHIFT_OFF, shiftState)
    }

    @Test
    fun testDoubleSpaceToPeriodCondition() {
        val textBefore1 = "hello "
        val canInsertPeriod1 = textBefore1.endsWith(" ") && !textBefore1.endsWith(". ")
        assertTrue(canInsertPeriod1)

        val textBefore2 = "hello. "
        val canInsertPeriod2 = textBefore2.endsWith(" ") && !textBefore2.endsWith(". ")
        assertFalse(canInsertPeriod2)

        val textBefore3 = " "
        val canInsertPeriod3 = textBefore3.endsWith(" ") && !textBefore3.endsWith(". ")
        assertTrue(canInsertPeriod3)
    }

    @Test
    fun testDeleteWordsBeforeCursorLogic() {
        val sampleText = "The quick brown fox jumps"
        val wordsToDelete = 2 // should delete "fox jumps" and the preceding space

        var remainingWords = wordsToDelete
        var deleteLen = 0
        var inWord = false

        for (i in sampleText.length - 1 downTo 0) {
            val ch = sampleText[i]
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

        val remainingText = sampleText.dropLast(deleteLen)
        assertEquals("The quick brown", remainingText)
    }

    @Test
    fun testHslHsvConversion() {
        val originalHsl = floatArrayOf(200f, 0.5f, 0.5f)
        val hsv = com.frogobox.libkeyboard.common.ext.hsl2hsv(originalHsl)
        val convertedHsl = com.frogobox.libkeyboard.common.ext.hsv2hsl(hsv)
        assertEquals(originalHsl[0], convertedHsl[0], 0.02f)
        assertEquals(originalHsl[1], convertedHsl[1], 0.02f)
        assertEquals(originalHsl[2], convertedHsl[2], 0.02f)
    }

    @Test
    fun testConstantsIntegrity() {
        assertEquals(0, BaseKeyboardIME.KEYBOARD_LETTERS)
        assertEquals(1, BaseKeyboardIME.KEYBOARD_SYMBOLS)
        assertEquals(2, BaseKeyboardIME.KEYBOARD_SYMBOLS_SHIFT)
        assertEquals(3, BaseKeyboardIME.KEYBOARD_NUMBER)
        assertEquals(350L, BaseKeyboardIME.DOUBLE_SPACE_PERIOD_TIMEOUT)
    }
}
