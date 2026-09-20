package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.suggestion.WordSuggestionEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit test suite for WordSuggestionEngine
 * Validates three-section candidates (Issue #50):
 * - Section 1: user's literal word
 * - Section 2: predicted word
 * - Section 3: auto-correct word
 */
class WordSuggestionEngineTest {

    private lateinit var engine: WordSuggestionEngine

    @Before
    fun setup() {
        engine = WordSuggestionEngine()
    }

    @Test
    fun testThreeSectionIntegrity() {
        val result = engine.getSuggestions("th")
        assertNotNull(result)
        assertEquals("th", result.userWord)
        assertTrue(result.predictedWord.isNotEmpty())
        assertTrue(result.autoCorrectWord.isNotEmpty())
        assertTrue(result.hasSuggestions())
    }

    @Test
    fun testPrefixPrediction() {
        val result = engine.getSuggestions("appl")
        assertEquals("appl", result.userWord)
        assertEquals("apple", result.predictedWord.lowercase())
    }

    @Test
    fun testCommonTypoAutoCorrection() {
        // Test "teh" -> "the"
        val resultThe = engine.getSuggestions("teh")
        assertEquals("teh", resultThe.userWord)
        assertEquals("the", resultThe.predictedWord.lowercase())

        // Test "wht" -> "what"
        val resultWhat = engine.getSuggestions("wht")
        assertEquals("wht", resultWhat.userWord)
        assertEquals("what", resultWhat.predictedWord.lowercase())

        // Test "helo" -> "hello"
        val resultHello = engine.getSuggestions("helo")
        assertEquals("helo", resultHello.userWord)
        assertEquals("hello", resultHello.predictedWord.lowercase())
    }

    @Test
    fun testCasingPreservationLowercase() {
        val result = engine.getSuggestions("appl")
        assertEquals("appl", result.userWord)
        assertEquals("apple", result.predictedWord)
    }

    @Test
    fun testCasingPreservationTitleCase() {
        val result = engine.getSuggestions("Appl")
        assertEquals("Appl", result.userWord)
        assertEquals("Apple", result.predictedWord)
    }

    @Test
    fun testCasingPreservationUppercase() {
        val result = engine.getSuggestions("APPL")
        assertEquals("APPL", result.userWord)
        assertEquals("APPLE", result.predictedWord)
    }

    @Test
    fun testLevenshteinDistanceCalculation() {
        assertEquals(0, engine.calculateLevenshteinDistance("test", "test"))
        assertEquals(1, engine.calculateLevenshteinDistance("hello", "helo"))
        assertEquals(1, engine.calculateLevenshteinDistance("apply", "apple"))
        assertEquals(3, engine.calculateLevenshteinDistance("kitten", "sitting"))
    }

    @Test
    fun testUserCustomWordLearning() {
        engine.addUserWord("FrogoDeveloper")
        val result = engine.getSuggestions("frogodev")
        assertEquals("frogodev", result.userWord)
        assertEquals("frogodeveloper", result.predictedWord.lowercase())
    }

    @Test
    fun testEmptyInputHandling() {
        val result = engine.getSuggestions("")
        assertNotNull(result)
        assertEquals("", result.userWord)
        assertTrue(result.predictedWord.isNotEmpty())
    }

    @Test
    fun testBlankWhitespaceInputHandling() {
        val result = engine.getSuggestions("   ")
        assertNotNull(result)
        assertEquals("", result.userWord)
    }

}
