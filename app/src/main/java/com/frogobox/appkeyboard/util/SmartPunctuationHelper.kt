package com.frogobox.appkeyboard.util

/**
 * Intelligent helper managing smart punctuation behaviors:
 * 1. Double-Space Period insertion with auto-capitalization triggering.
 * 2. Sentence boundary detection for auto-capitalization.
 * 3. Smart spacing after punctuation.
 */
object SmartPunctuationHelper {

    const val DOUBLE_SPACE_TIMEOUT_MS = 500L

    data class DoubleSpaceResult(
        val isHandled: Boolean,
        val textToDeleteLength: Int = 0,
        val replacementText: String = "",
        val shouldAutoShift: Boolean = false
    )

    /**
     * Determines whether a spacebar keypress qualifies as a double-space period shortcut.
     *
     * @param lastSpaceTime Timestamp of the previous space keypress in milliseconds.
     * @param currentTime Current system time in milliseconds.
     * @param textBeforeCursor The text immediately preceding the cursor (typically 2-4 characters).
     * @return [DoubleSpaceResult] containing replacement details and shift state.
     */
    fun checkDoubleSpacePeriod(
        lastSpaceTime: Long,
        currentTime: Long,
        textBeforeCursor: CharSequence?
    ): DoubleSpaceResult {
        if (lastSpaceTime <= 0L) return DoubleSpaceResult(isHandled = false)
        val elapsed = currentTime - lastSpaceTime
        if (elapsed > DOUBLE_SPACE_TIMEOUT_MS) return DoubleSpaceResult(isHandled = false)

        val text = textBeforeCursor?.toString() ?: return DoubleSpaceResult(isHandled = false)
        if (text.isEmpty()) return DoubleSpaceResult(isHandled = false)

        // Only convert if cursor is immediately preceded by a single space that is not already part of ". "
        if (text.endsWith(" ") && !text.endsWith(". ") && !text.endsWith("? ") && !text.endsWith("! ") && !text.endsWith("\n ")) {
            return DoubleSpaceResult(
                isHandled = true,
                textToDeleteLength = 1,
                replacementText = ". ",
                shouldAutoShift = true
            )
        }

        return DoubleSpaceResult(isHandled = false)
    }

    /**
     * Evaluates whether the cursor is situated at a sentence boundary where the next typed letter
     * should automatically be capitalized.
     *
     * @param textBeforeCursor Text preceding the cursor (recommended 20-30 characters).
     * @return True if next letter should be capitalized, false otherwise.
     */
    fun shouldAutoCapitalize(textBeforeCursor: CharSequence?): Boolean {
        if (textBeforeCursor.isNullOrEmpty()) return true // Beginning of field / empty text

        val text = textBeforeCursor.toString()
        val trimmedEnd = text.trimEnd { it == ' ' || it == '\t' }

        if (trimmedEnd.isEmpty()) return true // Only whitespace before cursor
        if (text.endsWith("\n") || text.endsWith("\r\n")) return true // Newline boundary

        // Check if preceding non-whitespace character is a sentence terminator
        val lastChar = trimmedEnd.last()
        if (lastChar == '.' || lastChar == '?' || lastChar == '!') {
            // Must have at least one whitespace between the punctuation and the cursor position
            return text.length > trimmedEnd.length
        }

        return false
    }

    /**
     * Inspects whether a typed punctuation mark should trigger automatic single-space formatting.
     *
     * @param punctuationChar The character being typed (e.g., ',', '.', '!', '?').
     * @param textBeforeCursor Text before cursor.
     * @return True if a following space should automatically be appended.
     */
    fun shouldAutoSpaceAfterPunctuation(punctuationChar: Char, textBeforeCursor: CharSequence?): Boolean {
        if (punctuationChar != ',' && punctuationChar != '.' && punctuationChar != '!' && punctuationChar != '?' && punctuationChar != ';' && punctuationChar != ':') {
            return false
        }
        val text = textBeforeCursor?.toString() ?: ""
        // Do not auto-space if inside digits (e.g. 3.14 or 10,000) or URL
        if (text.isNotEmpty() && text.last().isDigit()) {
            return false
        }
        return true
    }
}
