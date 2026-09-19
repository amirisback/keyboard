package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.ui.test.TypingMetricsCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for TypingMetricsCalculator domain logic
 * Verifies character counting, word counting, line counting, and WPM calculation safety guards.
 */
class TypingMetricsTest {

    @Test
    fun testEmptyTextMetrics() {
        val metrics = TypingMetricsCalculator.calculate("", 5000L)
        assertEquals(0, metrics.characterCount)
        assertEquals(0, metrics.wordCount)
        assertEquals(0, metrics.lineCount)
        assertEquals(0, metrics.wordsPerMinute)
        assertEquals(5L, metrics.elapsedSeconds)
    }

    @Test
    fun testWhitespaceOnlyMetrics() {
        val metrics = TypingMetricsCalculator.calculate("   \n\t  ", 10000L)
        assertEquals(7, metrics.characterCount)
        assertEquals(0, metrics.wordCount)
        assertEquals(2, metrics.lineCount)
        assertEquals(0, metrics.wordsPerMinute)
    }

    @Test
    fun testEnglishPangramWordCount() {
        val pangram = "The quick brown fox jumps over the lazy dog"
        val metrics = TypingMetricsCalculator.calculate(pangram, 12000L)
        assertEquals(43, metrics.characterCount)
        assertEquals(9, metrics.wordCount)
        assertEquals(1, metrics.lineCount)
        // 9 words in 12s = 9 / (12/60) = 45 WPM
        assertEquals(45, metrics.wordsPerMinute)
    }

    @Test
    fun testIndonesianPangramMetrics() {
        val pangram = "Keluarga Jefri bernomor fax tujuh belas"
        val metrics = TypingMetricsCalculator.calculate(pangram, 6000L)
        assertEquals(39, metrics.characterCount)
        assertEquals(6, metrics.wordCount)
        assertEquals(1, metrics.lineCount)
        // 6 words in 6s = 6 / 0.1 = 60 WPM
        assertEquals(60, metrics.wordsPerMinute)
    }

    @Test
    fun testMultilineCount() {
        val text = "Line 1\nLine 2\nLine 3\nLine 4"
        val metrics = TypingMetricsCalculator.calculate(text, 20000L)
        assertEquals(4, metrics.lineCount)
        assertEquals(8, metrics.wordCount)
    }

    @Test
    fun testWpmShortDurationReturnsZero() {
        val text = "Hello world quick test"
        val metrics = TypingMetricsCalculator.calculate(text, 1500L) // 1.5s < 2s threshold
        assertEquals(4, metrics.wordCount)
        assertEquals(0, metrics.wordsPerMinute)
    }

    @Test
    fun testWpmClampedToMax300() {
        // 500 words pasted in 3 seconds -> extreme raw WPM
        val manyWords = (1..500).joinToString(" ") { "word$it" }
        val metrics = TypingMetricsCalculator.calculate(manyWords, 3000L)
        assertEquals(500, metrics.wordCount)
        assertTrue(metrics.wordsPerMinute <= 300)
        assertEquals(300, metrics.wordsPerMinute)
    }
}
