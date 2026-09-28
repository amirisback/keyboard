package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.ClipboardItem
import com.frogobox.appkeyboard.ui.keyboard.textedit.TextEditAction
import com.frogobox.appkeyboard.util.GestureTypingHelper
import com.frogobox.appkeyboard.util.HapticFeedbackHelper
import com.frogobox.appkeyboard.util.KeyboardBackupRestoreHelper
import com.frogobox.appkeyboard.util.KeyboardHeightHelper
import com.frogobox.appkeyboard.util.KeyboardSetupWizardHelper
import com.frogobox.appkeyboard.util.SmartCalculatorHelper
import com.frogobox.appkeyboard.util.UserDictionaryHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * QA verification test suite for Tier 5 Flagship Ultimate Suite (TASK-031).
 * Tests all Acceptance Criteria across Ergonomics, Onboarding, Backup/Restore,
 * Gesture Typing, On-Device Learning Dictionary, Inline Math Calculator, and Safety.
 */
class Tier5FlagshipUltimateSuiteTest {

    // --- AC-01: Keyboard Height Resizer & Bottom Chin Offset ---

    @Test
    fun testKeyboardHeightHelper_scaleAndOffsetCalculations() {
        val baseHeight = 240f

        val compactHeight = KeyboardHeightHelper.calculateEffectiveHeight(
            baseHeight,
            KeyboardHeightHelper.HeightScale.COMPACT,
            KeyboardHeightHelper.ChinOffset.NONE
        )
        assertEquals(240f * 0.85f, compactHeight, 0.01f)

        val tallWithChin = KeyboardHeightHelper.calculateEffectiveHeight(
            baseHeight,
            KeyboardHeightHelper.HeightScale.TALL,
            KeyboardHeightHelper.ChinOffset.MEDIUM
        )
        assertEquals((240f * 1.15f) + 16f, tallWithChin, 0.01f)

        val extraTallWithLargeChin = KeyboardHeightHelper.calculateEffectiveHeight(
            baseHeight,
            KeyboardHeightHelper.HeightScale.EXTRA_TALL,
            KeyboardHeightHelper.ChinOffset.LARGE
        )
        assertEquals((240f * 1.30f) + 24f, extraTallWithLargeChin, 0.01f)

        assertEquals(KeyboardHeightHelper.HeightScale.NORMAL, KeyboardHeightHelper.HeightScale.fromString("invalid"))
        assertEquals(KeyboardHeightHelper.ChinOffset.LARGE, KeyboardHeightHelper.ChinOffset.fromString("LARGE"))
        assertEquals(4, KeyboardHeightHelper.HeightScale.entries.size)
        assertEquals(4, KeyboardHeightHelper.ChinOffset.entries.size)
    }

    // --- AC-03: Text Editing Palette Undo & Redo ---

    @Test
    fun testTextEditAction_containsUndoAndRedo() {
        val actions = TextEditAction.entries
        assertTrue(actions.contains(TextEditAction.UNDO))
        assertTrue(actions.contains(TextEditAction.REDO))
        assertEquals("UNDO", TextEditAction.UNDO.name)
        assertEquals("REDO", TextEditAction.REDO.name)
    }

    // --- AC-04: Interactive 3-Step Setup Wizard ---

    @Test
    fun testKeyboardSetupWizardHelper_stepTransitions() {
        assertEquals(
            KeyboardSetupWizardHelper.SetupStep.STEP_ENABLE,
            KeyboardSetupWizardHelper.getSetupStep(isImeEnabled = false, isImeDefault = false)
        )
        assertEquals(
            KeyboardSetupWizardHelper.SetupStep.STEP_SELECT_DEFAULT,
            KeyboardSetupWizardHelper.getSetupStep(isImeEnabled = true, isImeDefault = false)
        )
        assertEquals(
            KeyboardSetupWizardHelper.SetupStep.STEP_READY,
            KeyboardSetupWizardHelper.getSetupStep(isImeEnabled = true, isImeDefault = true)
        )
        assertEquals(3, KeyboardSetupWizardHelper.SetupStep.entries.size)
    }

    // --- AC-05: Backup & Restore JSON Profile & Sanitization ---

    @Test
    fun testKeyboardBackupRestoreHelper_exportAndImportSanitization() {
        val autoTexts = listOf(
            AutoTextEntity(id = 1, title = "Alamat Rumah", body = "Jl. Merdeka No. 45")
        )

        val clips = listOf(
            ClipboardItem(id = "1", text = "Public pinned text", timestamp = 100L, isPinned = true),
            ClipboardItem(id = "2", text = "Temporary clip", timestamp = 200L, isPinned = false),
            ClipboardItem(id = "3", text = "123456 (Sensitive OTP)", timestamp = 300L, isPinned = true)
        )

        val settings = mapOf("theme" to "DARK", "language" to "ID")

        val json = KeyboardBackupRestoreHelper.exportToJson(autoTexts, clips, settings)
        assertTrue(json.contains("Alamat Rumah"))
        assertTrue(json.contains("Public pinned text"))
        // Strict security rule: sensitive or unpinned items must NOT be exported
        assertFalse(json.contains("Temporary clip"))
        assertFalse(json.contains("123456 (Sensitive OTP)"))

        val parseResult = KeyboardBackupRestoreHelper.importFromJson(json)
        assertTrue(parseResult.isSuccess)
        val data = parseResult.getOrThrow()
        assertEquals(1, data.autoTexts.size)
        assertEquals("Alamat Rumah", data.autoTexts[0].title)
        assertEquals(1, data.pinnedClips.size)
        assertEquals("Public pinned text", data.pinnedClips[0])
        assertEquals("DARK", data.settings["theme"])
    }

    @Test
    fun testKeyboardBackupRestoreHelper_corruptedJsonHandledGracefully() {
        val result = KeyboardBackupRestoreHelper.importFromJson("{ corrupted json !!")
        assertTrue(result.isFailure)
    }

    // --- AC-06: Haptic & Sound Customizer Helper ---

    @Test
    fun testHapticFeedbackHelper_entriesMigrationAndParsing() {
        assertEquals(HapticFeedbackHelper.HapticIntensity.SUBTLE, HapticFeedbackHelper.fromString("SUBTLE"))
        assertEquals(HapticFeedbackHelper.HapticIntensity.MEDIUM, HapticFeedbackHelper.fromString("UNKNOWN"))
        assertTrue(HapticFeedbackHelper.HapticIntensity.entries.isNotEmpty())
        assertEquals(4, HapticFeedbackHelper.HapticIntensity.entries.size)

        // Duration mappings
        assertEquals(15L, HapticFeedbackHelper.HapticIntensity.SUBTLE.durationMs)
        assertEquals(30L, HapticFeedbackHelper.HapticIntensity.MEDIUM.durationMs)
        assertEquals(55L, HapticFeedbackHelper.HapticIntensity.STRONG.durationMs)
        assertEquals(0L, HapticFeedbackHelper.HapticIntensity.OFF.durationMs)
    }

    // --- AC-07: Glide / Gesture Typing Trajectory & Matching ---

    @Test
    fun testGestureTypingHelper_trajectoryAndMatching() {
        val keyH = GestureTypingHelper.KeyBoundingBox('h', 0f, 0f, 50f, 50f)
        val keyE = GestureTypingHelper.KeyBoundingBox('e', 60f, 0f, 110f, 50f)
        val keyL = GestureTypingHelper.KeyBoundingBox('l', 120f, 0f, 170f, 50f)
        val keyO = GestureTypingHelper.KeyBoundingBox('o', 180f, 0f, 230f, 50f)
        val keys = listOf(keyH, keyE, keyL, keyO)

        val points = listOf(
            GestureTypingHelper.PathPoint(25f, 25f),
            GestureTypingHelper.PathPoint(85f, 25f),
            GestureTypingHelper.PathPoint(145f, 25f),
            GestureTypingHelper.PathPoint(205f, 25f)
        )

        val chars = GestureTypingHelper.resolveTrajectoryToChars(points, keys)
        assertEquals("helo", chars)

        val matches = GestureTypingHelper.matchGesture("helo", listOf("hello", "halo", "help", "world"))
        assertTrue(matches.contains("hello") || matches.contains("halo"))
    }

    // --- AC-08: On-Device Adaptive User Dictionary & Privacy Guard ---

    @Test
    fun testUserDictionaryHelper_privacyGuardAndLearning() {
        UserDictionaryHelper.inMemoryWordsForTest = mutableMapOf()
        UserDictionaryHelper.clearLearnedWords(null)

        // 1. Incognito mode MUST block learning
        val incognitoResult = UserDictionaryHelper.learnWord(null, "rahasia", isIncognito = true, isPassword = false)
        assertFalse(incognitoResult)

        // 2. Password mode MUST block learning
        val passwordResult = UserDictionaryHelper.learnWord(null, "myPassword123", isIncognito = false, isPassword = true)
        assertFalse(passwordResult)

        // 3. Hygiene check: numbers, symbols, too short/long words rejected
        assertFalse(UserDictionaryHelper.canLearnWord("a", isIncognito = false, isPassword = false))
        assertFalse(UserDictionaryHelper.canLearnWord("12345", isIncognito = false, isPassword = false))
        assertFalse(UserDictionaryHelper.canLearnWord("hello_world", isIncognito = false, isPassword = false))

        // 4. Standard valid word is learned
        val learnedResult = UserDictionaryHelper.learnWord(null, "mantap", isIncognito = false, isPassword = false)
        assertTrue(learnedResult)

        val words = UserDictionaryHelper.getLearnedWords(null)
        assertEquals(1, words["mantap"])

        // Increments frequency on repeat
        UserDictionaryHelper.learnWord(null, "mantap", isIncognito = false, isPassword = false)
        assertEquals(2, UserDictionaryHelper.getLearnedWords(null)["mantap"])

        val suggestions = UserDictionaryHelper.getSuggestions(null, "man")
        assertTrue(suggestions.contains("mantap"))

        // Reset memory test holder
        UserDictionaryHelper.inMemoryWordsForTest = null
    }

    // --- AC-09: Inline Smart Math / Calculator ---

    @Test
    fun testSmartCalculatorHelper_basicAndPrecedenceMath() {
        // Simple multiplication
        val multResult = SmartCalculatorHelper.evaluateMath("25000*4=")
        assertNotNull(multResult)
        assertEquals("100000", multResult?.evaluatedValue)
        assertEquals("= 100000", multResult?.chipLabel)

        // Simple addition
        val addResult = SmartCalculatorHelper.evaluateMath("150+350=")
        assertNotNull(addResult)
        assertEquals("500", addResult?.evaluatedValue)

        // Operator precedence (multiplication before addition)
        val precResult = SmartCalculatorHelper.evaluateMath("10+20*3=")
        assertNotNull(precResult)
        assertEquals("70", precResult?.evaluatedValue)

        // Trailing math in sentence
        val sentenceResult = SmartCalculatorHelper.evaluateMath("Total belanjaan 12500*2=")
        assertNotNull(sentenceResult)
        assertEquals("25000", sentenceResult?.evaluatedValue)

        // Floating precision formatting
        val divResult = SmartCalculatorHelper.evaluateMath("10/4=")
        assertNotNull(divResult)
        assertEquals("2.5", divResult?.evaluatedValue)

        // Invalid or non-math expressions return null
        assertNull(SmartCalculatorHelper.evaluateMath("halo dunia="))
        assertNull(SmartCalculatorHelper.evaluateMath(null))
        assertNull(SmartCalculatorHelper.evaluateMath(""))
    }
}
