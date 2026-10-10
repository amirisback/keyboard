package com.frogobox.appkeyboard.util

typealias PathPoint = com.frogobox.libkeyboard.util.GestureTypingHelper.PathPoint
typealias KeyBoundingBox = com.frogobox.libkeyboard.util.GestureTypingHelper.KeyBoundingBox

/**
 * Compatibility object delegating to [com.frogobox.libkeyboard.util.GestureTypingHelper]
 * for full backward compatibility across the app module and unit tests.
 */
object GestureTypingHelper {

    typealias PathPoint = com.frogobox.libkeyboard.util.GestureTypingHelper.PathPoint
    typealias KeyBoundingBox = com.frogobox.libkeyboard.util.GestureTypingHelper.KeyBoundingBox

    val DEFAULT_VOCABULARY = com.frogobox.libkeyboard.util.GestureTypingHelper.DEFAULT_VOCABULARY

    fun registerWord(word: String) {
        com.frogobox.libkeyboard.util.GestureTypingHelper.registerWord(word)
    }

    fun addVocabulary(words: List<String>) {
        com.frogobox.libkeyboard.util.GestureTypingHelper.addVocabulary(words)
    }

    fun resolveTrajectoryToChars(
        points: List<PathPoint>,
        keys: List<KeyBoundingBox>
    ): String {
        return com.frogobox.libkeyboard.util.GestureTypingHelper.resolveTrajectoryToChars(points, keys)
    }

    fun matchGesture(
        rawChars: String,
        candidateVocabulary: List<String> = DEFAULT_VOCABULARY,
        maxResults: Int = 3
    ): List<String> {
        return com.frogobox.libkeyboard.util.GestureTypingHelper.matchGesture(rawChars, candidateVocabulary, maxResults)
    }

    fun compressConsecutiveDuplicates(word: String): String {
        return com.frogobox.libkeyboard.util.GestureTypingHelper.compressConsecutiveDuplicates(word)
    }

    fun clearDynamicVocabulary() {
        com.frogobox.libkeyboard.util.GestureTypingHelper.clearDynamicVocabulary()
    }
}
