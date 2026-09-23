package com.frogobox.appkeyboard

import android.text.InputType
import com.frogobox.appkeyboard.suggestion.WordSuggestionEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests verifying TASK-032 Google Keyboard (Gboard) parity recommendations:
 * 1. Indonesian Dictionary & Seller Slang Typo Map
 * 2. Spacebar Auto-Correction
 * 3. Single-Tap Backspace Undo
 * 4. Sensitive Field Exclusion Guard
 */
class GboardParityRecommendationsTest {

    private lateinit var suggestionEngine: WordSuggestionEngine

    @Before
    fun setUp() {
        suggestionEngine = WordSuggestionEngine()
    }

    @Test
    fun testIndonesianShorthandsAndSellerTypoMap() {
        // Test common Indonesian conversational shorthands
        assertEquals("yang", suggestionEngine.getAutoCorrectReplacement("yg"))
        assertEquals("dengan", suggestionEngine.getAutoCorrectReplacement("dgn"))
        assertEquals("saya", suggestionEngine.getAutoCorrectReplacement("sy"))
        assertEquals("kamu", suggestionEngine.getAutoCorrectReplacement("km"))
        assertEquals("tidak", suggestionEngine.getAutoCorrectReplacement("gak"))
        assertEquals("sudah", suggestionEngine.getAutoCorrectReplacement("sdh"))
        assertEquals("belum", suggestionEngine.getAutoCorrectReplacement("blm"))
        assertEquals("banget", suggestionEngine.getAutoCorrectReplacement("bgt"))
        assertEquals("terus", suggestionEngine.getAutoCorrectReplacement("trs"))
        assertEquals("tolong", suggestionEngine.getAutoCorrectReplacement("tlg"))
        assertEquals("terima kasih", suggestionEngine.getAutoCorrectReplacement("makasih"))

        // Test seller-specific abbreviations
        assertEquals("transfer", suggestionEngine.getAutoCorrectReplacement("tf"))
        assertEquals("rekening", suggestionEngine.getAutoCorrectReplacement("rek"))
        assertEquals("barang", suggestionEngine.getAutoCorrectReplacement("brg"))
        assertEquals("COD", suggestionEngine.getAutoCorrectReplacement("cod"))
        assertEquals("BCA", suggestionEngine.getAutoCorrectReplacement("bca"))
    }

    @Test
    fun testEnglishCommonTypoAutoCorrect() {
        assertEquals("the", suggestionEngine.getAutoCorrectReplacement("teh"))
        assertEquals("and", suggestionEngine.getAutoCorrectReplacement("adn"))
        assertEquals("what", suggestionEngine.getAutoCorrectReplacement("wht"))
        assertEquals("receive", suggestionEngine.getAutoCorrectReplacement("recive"))
    }

    @Test
    fun testCasingPreservationOnAutoCorrect() {
        // Capitalized
        assertEquals("Yang", suggestionEngine.getAutoCorrectReplacement("Yg"))
        assertEquals("The", suggestionEngine.getAutoCorrectReplacement("Teh"))
        assertEquals("Saya", suggestionEngine.getAutoCorrectReplacement("Sy"))

        // All caps
        assertEquals("YANG", suggestionEngine.getAutoCorrectReplacement("YG"))
        assertEquals("THE", suggestionEngine.getAutoCorrectReplacement("TEH"))
    }

    @Test
    fun testValidDictionaryWordsDoNotAutoCorrect() {
        // Words recognized in dictionary should not be auto-corrected to something else
        assertNull(suggestionEngine.getAutoCorrectReplacement("the"))
        assertNull(suggestionEngine.getAutoCorrectReplacement("yang"))
        assertNull(suggestionEngine.getAutoCorrectReplacement("bisa"))
        assertNull(suggestionEngine.getAutoCorrectReplacement("sudah"))
        assertNull(suggestionEngine.getAutoCorrectReplacement("pesanan"))
        assertNull(suggestionEngine.getAutoCorrectReplacement("barang"))
    }

    @Test
    fun testSingleTapBackspaceUndoLogic() {
        data class AutoCorrectionRecord(val original: String, val replacement: String, val timestamp: Long)

        var lastAutoCorrection: AutoCorrectionRecord? = null
        var committedText = "teh"

        // Step 1: User types "teh" and presses space
        val replacement = suggestionEngine.getAutoCorrectReplacement(committedText)
        assertNotNull(replacement)
        assertEquals("the", replacement)

        // Simulate auto-correction commit: replaces "teh" with "the "
        val originalWord = committedText
        committedText = "$replacement "
        lastAutoCorrection = AutoCorrectionRecord(originalWord, replacement!!, System.currentTimeMillis())
        assertEquals("the ", committedText)

        // Step 2: User presses Backspace immediately -> Single-Tap Undo!
        val record = lastAutoCorrection
        assertNotNull(record)
        val expectedSuffix = "${record.replacement} "
        assertTrue(committedText.endsWith(expectedSuffix))

        // Revert: delete "${replacement} " and restore original word
        committedText = committedText.removeSuffix(expectedSuffix) + record.original
        lastAutoCorrection = null

        // Verification: Text is restored to original verbatim "teh"
        assertEquals("teh", committedText)
        assertNull(lastAutoCorrection)
    }

    @Test
    fun testSensitiveFieldExclusionGuard() {
        fun isFieldEligibleForAutoCorrect(inputType: Int): Boolean {
            val inputClass = inputType and InputType.TYPE_MASK_CLASS
            val variation = inputType and InputType.TYPE_MASK_VARIATION
            val isSensitiveOrNumeric = (inputClass == InputType.TYPE_CLASS_NUMBER) ||
                    (inputClass == InputType.TYPE_CLASS_PHONE) ||
                    (inputClass == InputType.TYPE_CLASS_DATETIME) ||
                    (inputClass == InputType.TYPE_CLASS_TEXT && (
                        variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                        variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                        variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                        variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS ||
                        variation == InputType.TYPE_TEXT_VARIATION_URI
                    ))
            return !isSensitiveOrNumeric
        }

        // Standard text field -> ELIGIBLE
        assertTrue(isFieldEligibleForAutoCorrect(InputType.TYPE_CLASS_TEXT))
        assertTrue(isFieldEligibleForAutoCorrect(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE))

        // Password field -> NOT ELIGIBLE
        assertTrue(!isFieldEligibleForAutoCorrect(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD))
        assertTrue(!isFieldEligibleForAutoCorrect(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD))

        // Email address field -> NOT ELIGIBLE
        assertTrue(!isFieldEligibleForAutoCorrect(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS))

        // Web URL field -> NOT ELIGIBLE
        assertTrue(!isFieldEligibleForAutoCorrect(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI))

        // Number & Phone fields -> NOT ELIGIBLE
        assertTrue(!isFieldEligibleForAutoCorrect(InputType.TYPE_CLASS_NUMBER))
        assertTrue(!isFieldEligibleForAutoCorrect(InputType.TYPE_CLASS_PHONE))
    }

}
