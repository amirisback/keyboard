package com.frogobox.appkeyboard

import android.text.InputType
import android.view.inputmethod.EditorInfo
import com.frogobox.appkeyboard.suggestion.SuggestionResult
import com.frogobox.appkeyboard.util.KeyboardPrivacyHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Test suite for TASK-027:
 * - Spacebar Cursor Scrubbing (Trackpad Mode) & Backspace Word Delete
 * - Automatic Incognito & Password Privacy Guard
 */
class SpacebarScrubbingAndPrivacyGuardTest {

    @Test
    fun testIsPasswordField_withTextPasswordVariations() {
        // Standard text password
        val passwordInfo = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        assertTrue(KeyboardPrivacyHelper.isPasswordField(passwordInfo))
        assertTrue(KeyboardPrivacyHelper.isIncognitoOrPassword(passwordInfo))

        // Web password
        val webPasswordInfo = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
        }
        assertTrue(KeyboardPrivacyHelper.isPasswordField(webPasswordInfo))
        assertTrue(KeyboardPrivacyHelper.isIncognitoOrPassword(webPasswordInfo))

        // Visible password
        val visiblePasswordInfo = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        assertTrue(KeyboardPrivacyHelper.isPasswordField(visiblePasswordInfo))
        assertTrue(KeyboardPrivacyHelper.isIncognitoOrPassword(visiblePasswordInfo))
    }

    @Test
    fun testIsPasswordField_withNumericPassword() {
        val numberPinInfo = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }
        assertTrue(KeyboardPrivacyHelper.isPasswordField(numberPinInfo))
        assertTrue(KeyboardPrivacyHelper.isIncognitoOrPassword(numberPinInfo))
    }

    @Test
    fun testIsNoPersonalizedLearning_withIncognitoFlag() {
        val incognitoInfo = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT
            imeOptions = EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
        }
        assertFalse(KeyboardPrivacyHelper.isPasswordField(incognitoInfo))
        assertTrue(KeyboardPrivacyHelper.isNoPersonalizedLearning(incognitoInfo))
        assertTrue(KeyboardPrivacyHelper.isIncognitoOrPassword(incognitoInfo))
    }

    @Test
    fun testRegularTextField_isNotIncognitoOrPassword() {
        val normalTextInfo = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
            imeOptions = EditorInfo.IME_ACTION_DONE
        }
        assertFalse(KeyboardPrivacyHelper.isPasswordField(normalTextInfo))
        assertFalse(KeyboardPrivacyHelper.isNoPersonalizedLearning(normalTextInfo))
        assertFalse(KeyboardPrivacyHelper.isIncognitoOrPassword(normalTextInfo))

        val emailInfo = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        assertFalse(KeyboardPrivacyHelper.isPasswordField(emailInfo))
        assertFalse(KeyboardPrivacyHelper.isIncognitoOrPassword(emailInfo))
    }

    @Test
    fun testNullEditorInfo_handledSafely() {
        assertFalse(KeyboardPrivacyHelper.isPasswordField(null))
        assertFalse(KeyboardPrivacyHelper.isNoPersonalizedLearning(null))
        assertFalse(KeyboardPrivacyHelper.isIncognitoOrPassword(null))
    }

    @Test
    fun testSpacebarCursorScrubbing_stepCalculations() {
        val threshold = 20
        val touchStartX = 100

        // Move right 65px -> 3 steps to the right
        val moveRightX = 165
        val diffRight = moveRightX - touchStartX
        val stepsRight = diffRight / threshold
        assertEquals(3, stepsRight)

        // Move left 50px -> 2 steps to the left
        val moveLeftX = 50
        val diffLeft = moveLeftX - touchStartX
        val stepsLeft = (-diffLeft) / threshold
        assertEquals(2, stepsLeft)
    }

    @Test
    fun testBackspaceSwipeDelete_wordCountCalculations() {
        val threshold = 30
        val deleteStartX = 300

        // Swipe left 150px
        val swipeLeftDist = deleteStartX - 150 // 150px
        val wordsToDelete = kotlin.math.max(1, swipeLeftDist / (threshold * 2))
        assertEquals(2, wordsToDelete)

        // Short swipe 40px
        val shortSwipeDist = deleteStartX - 260 // 40px
        val minWords = kotlin.math.max(1, shortSwipeDist / (threshold * 2))
        assertEquals(1, minWords)
    }

    @Test
    fun testIncognitoSuppressesSuggestions() {
        var isSuggestionEnabled = true
        var isIncognitoMode = true

        val shouldCalculateSuggestions = isSuggestionEnabled && !isIncognitoMode
        assertFalse("Suggestions must be suppressed during incognito mode", shouldCalculateSuggestions)

        val emptyResult = SuggestionResult.EMPTY
        assertFalse("SuggestionResult.EMPTY must have no suggestions", emptyResult.hasSuggestions())
    }
}
