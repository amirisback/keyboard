package com.frogobox.appkeyboard.ui.test

import kotlin.math.roundToInt

data class TypingMetrics(
    val characterCount: Int = 0,
    val wordCount: Int = 0,
    val lineCount: Int = 0,
    val wordsPerMinute: Int = 0,
    val elapsedSeconds: Long = 0L
)

object TypingMetricsCalculator {

    fun calculate(text: String, elapsedMillis: Long): TypingMetrics {
        val charCount = text.length
        val trimmed = text.trim()
        val wordCount = if (trimmed.isEmpty()) {
            0
        } else {
            trimmed.split("\\s+".toRegex()).count { it.isNotEmpty() }
        }

        val lineCount = if (text.isEmpty()) {
            0
        } else {
            text.count { it == '\n' } + 1
        }

        val elapsedSeconds = elapsedMillis / 1000L
        val wpm = if (elapsedSeconds < 2L || wordCount == 0) {
            0
        } else {
            val minutes = elapsedSeconds / 60.0
            val rawWpm = (wordCount / minutes).roundToInt()
            rawWpm.coerceIn(0, 300)
        }

        return TypingMetrics(
            characterCount = charCount,
            wordCount = wordCount,
            lineCount = lineCount,
            wordsPerMinute = wpm,
            elapsedSeconds = elapsedSeconds
        )
    }
}
