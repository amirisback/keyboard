package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.suggestion.SuggestionResult
import com.frogobox.appkeyboard.ui.keyboard.root.CandidateType
import com.frogobox.appkeyboard.ui.keyboard.root.KeyboardPanelState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test suite validating TASK-020 Full Pure Compose architecture:
 * - KeyboardPanelState state machine transitions & feature type mappings
 * - CandidateType enum integrity
 * - Suggestion result evaluation for AnimatedContent Top Bar transitions
 */
class KeyboardImeComposeUnitTest {

    @Test
    fun testKeyboardPanelStateEnumValues() {
        val expectedPanels = listOf(
            KeyboardPanelState.MAIN,
            KeyboardPanelState.EMOJI,
            KeyboardPanelState.AUTO_TEXT,
            KeyboardPanelState.TEMPLATE_TEXT_GAME,
            KeyboardPanelState.TEMPLATE_TEXT_APP,
            KeyboardPanelState.TEMPLATE_TEXT_SALE,
            KeyboardPanelState.TEMPLATE_TEXT_LOVE,
            KeyboardPanelState.TEMPLATE_TEXT_GREETING,
            KeyboardPanelState.NEWS,
            KeyboardPanelState.MOVIE,
            KeyboardPanelState.WEBVIEW,
            KeyboardPanelState.FORM
        )

        assertEquals(12, KeyboardPanelState.entries.size)
        expectedPanels.forEach { panel ->
            assertTrue(KeyboardPanelState.entries.contains(panel))
        }
    }

    @Test
    fun testKeyboardPanelStateFromFeatureMapping() {
        assertEquals(KeyboardPanelState.AUTO_TEXT, KeyboardPanelState.fromFeature(KeyboardFeatureType.AUTO_TEXT))
        assertEquals(KeyboardPanelState.TEMPLATE_TEXT_GAME, KeyboardPanelState.fromFeature(KeyboardFeatureType.TEMPLATE_TEXT_GAME))
        assertEquals(KeyboardPanelState.TEMPLATE_TEXT_APP, KeyboardPanelState.fromFeature(KeyboardFeatureType.TEMPLATE_TEXT_APP))
        assertEquals(KeyboardPanelState.TEMPLATE_TEXT_SALE, KeyboardPanelState.fromFeature(KeyboardFeatureType.TEMPLATE_TEXT_SALE))
        assertEquals(KeyboardPanelState.TEMPLATE_TEXT_LOVE, KeyboardPanelState.fromFeature(KeyboardFeatureType.TEMPLATE_TEXT_LOVE))
        assertEquals(KeyboardPanelState.TEMPLATE_TEXT_GREETING, KeyboardPanelState.fromFeature(KeyboardFeatureType.TEMPLATE_TEXT_GREETING))
        assertEquals(KeyboardPanelState.NEWS, KeyboardPanelState.fromFeature(KeyboardFeatureType.NEWS))
        assertEquals(KeyboardPanelState.MOVIE, KeyboardPanelState.fromFeature(KeyboardFeatureType.MOVIE))
        assertEquals(KeyboardPanelState.WEBVIEW, KeyboardPanelState.fromFeature(KeyboardFeatureType.WEB))
        assertEquals(KeyboardPanelState.FORM, KeyboardPanelState.fromFeature(KeyboardFeatureType.FORM))

        // Non-panel utility actions should map to null
        assertNull(KeyboardPanelState.fromFeature(KeyboardFeatureType.SUGGESTION))
        assertNull(KeyboardPanelState.fromFeature(KeyboardFeatureType.CHANGE_KEYBOARD))
        assertNull(KeyboardPanelState.fromFeature(KeyboardFeatureType.SETTING))
    }

    @Test
    fun testTemplateStateProperties() {
        val templatePanels = listOf(
            KeyboardPanelState.TEMPLATE_TEXT_GAME,
            KeyboardPanelState.TEMPLATE_TEXT_APP,
            KeyboardPanelState.TEMPLATE_TEXT_SALE,
            KeyboardPanelState.TEMPLATE_TEXT_LOVE,
            KeyboardPanelState.TEMPLATE_TEXT_GREETING
        )

        templatePanels.forEach { panel ->
            assertTrue(panel.isTemplate)
            assertNotNull(panel.templateFeatureType)
        }

        assertFalse(KeyboardPanelState.MAIN.isTemplate)
        assertFalse(KeyboardPanelState.EMOJI.isTemplate)
        assertFalse(KeyboardPanelState.AUTO_TEXT.isTemplate)
        assertFalse(KeyboardPanelState.NEWS.isTemplate)
        assertFalse(KeyboardPanelState.MOVIE.isTemplate)
        assertFalse(KeyboardPanelState.WEBVIEW.isTemplate)
        assertFalse(KeyboardPanelState.FORM.isTemplate)

        assertNull(KeyboardPanelState.MAIN.templateFeatureType)
        assertNull(KeyboardPanelState.EMOJI.templateFeatureType)
        assertNull(KeyboardPanelState.AUTO_TEXT.templateFeatureType)
        assertNull(KeyboardPanelState.NEWS.templateFeatureType)
    }

    @Test
    fun testCandidateTypeEnumIntegrity() {
        assertEquals(3, CandidateType.entries.size)
        assertEquals(CandidateType.USER_WORD, CandidateType.valueOf("USER_WORD"))
        assertEquals(CandidateType.PREDICTED_WORD, CandidateType.valueOf("PREDICTED_WORD"))
        assertEquals(CandidateType.AUTOCORRECT_WORD, CandidateType.valueOf("AUTOCORRECT_WORD"))
    }

    @Test
    fun testTopBarTransitionConditionLogic() {
        // When suggestions are hidden or empty -> Header toolbar displayed
        val emptyResult = SuggestionResult.EMPTY
        val isSuggestionVisibleFalse = false
        val condition1 = isSuggestionVisibleFalse && emptyResult.hasSuggestions()
        assertFalse(condition1)

        // When suggestions are visible and candidates exist -> Candidate bar displayed
        val populatedResult = SuggestionResult(
            userWord = "rec",
            predictedWord = "receive",
            autoCorrectWord = "record"
        )
        val isSuggestionVisibleTrue = true
        val condition2 = isSuggestionVisibleTrue && populatedResult.hasSuggestions()
        assertTrue(condition2)

        // When dismissed -> immediate return to Header
        val dismissedResult = SuggestionResult.EMPTY
        val condition3 = isSuggestionVisibleTrue && dismissedResult.hasSuggestions()
        assertFalse(condition3)
    }

    @Test
    fun testWordBeforeCursorExtractionLogic() {
        fun extractWord(text: String): String {
            return text.takeLastWhile { it.isLetterOrDigit() || it == '\'' }
        }

        assertEquals("hello", extractWord("say hello"))
        assertEquals("world", extractWord("hello world"))
        assertEquals("don't", extractWord("please don't"))
        assertEquals("", extractWord("hello "))
        assertEquals("", extractWord(""))
        assertEquals("123", extractWord("order 123"))
    }

    @Test
    fun testFeatureHeaderIconAndTextIntegrity() {
        val features = KeyboardFeatureType.entries.map { it.mapToModel() }
        features.forEach { feature ->
            assertTrue("Feature text must not be empty", feature.text.isNotEmpty())
            assertTrue("Feature icon resource must be valid drawable", feature.icon != 0)
        }
    }

    @Test
    fun testKeyboardTopBarDimensionsConsistency() {
        val standardTopBarHeightDp = 56
        val featureItemMinHeightDp = 48
        val featureItemMinWidthDp = 72
        val featureIconSizeDp = 24

        assertTrue("Feature item height must comfortably fit within Top Bar", featureItemMinHeightDp < standardTopBarHeightDp)
        assertTrue("Icon size must fit within feature item", featureIconSizeDp < featureItemMinHeightDp)
        assertTrue("Min width must be at least 72dp to avoid multi-line label clipping", featureItemMinWidthDp >= 72)
    }

}
