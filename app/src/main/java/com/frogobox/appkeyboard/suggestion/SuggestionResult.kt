package com.frogobox.appkeyboard.suggestion

/**
 * Data class representing the 3-section candidate suggestion model
 * satisfying GitHub Issue #50:
 * - Section 1: userWord (literal verbatim word typed by user)
 * - Section 2: predictedWord (highest frequency completion or contextual prediction)
 * - Section 3: autoCorrectWord (best typographical correction or alternative)
 */
data class SuggestionResult(
    val userWord: String = "",
    val predictedWord: String = "",
    val autoCorrectWord: String = ""
) {
    fun hasSuggestions(): Boolean =
        userWord.isNotEmpty() || predictedWord.isNotEmpty() || autoCorrectWord.isNotEmpty()

    companion object {
        val EMPTY = SuggestionResult("", "", "")
    }
}
