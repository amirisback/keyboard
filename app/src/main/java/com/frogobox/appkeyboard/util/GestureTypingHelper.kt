package com.frogobox.appkeyboard.util

import kotlin.math.hypot

/**
 * Glide / Gesture Typing helper.
 * Tracks continuous swipe points, extracts key trajectories, and matches against vocabulary words.
 */
object GestureTypingHelper {

    data class PathPoint(val x: Float, val y: Float, val timestamp: Long = System.currentTimeMillis())

    data class KeyBoundingBox(
        val char: Char,
        val left: Float,
        val top: Float,
        val right: Float,
        val bottom: Float
    ) {
        val centerX: Float get() = (left + right) / 2f
        val centerY: Float get() = (top + bottom) / 2f

        fun contains(x: Float, y: Float): Boolean {
            return x in left..right && y in top..bottom
        }

        fun distanceTo(x: Float, y: Float): Float {
            return hypot(x - centerX, y - centerY)
        }
    }

    val DEFAULT_VOCABULARY = listOf(
        "halo", "hello", "keyboard", "terima", "kasih", "selamat", "pagi", "siang",
        "malam", "bagus", "cepat", "bisa", "siap", "oke", "mantap", "sudah", "kemarin",
        "besok", "kamu", "saya", "kita", "mereka", "dengan", "untuk", "akan", "dalam",
        "yang", "dan", "dari", "ini", "itu", "ada", "tidak", "bukan", "hanya", "juga",
        "good", "great", "thanks", "please", "yes", "no", "work", "play", "time"
    )

    /**
     * Resolves continuous trajectory points to a sequence of hit characters.
     */
    fun resolveTrajectoryToChars(points: List<PathPoint>, keyBoxes: List<KeyBoundingBox>): String {
        if (points.isEmpty() || keyBoxes.isEmpty()) return ""

        val rawChars = mutableListOf<Char>()
        for (pt in points) {
            val hitKey = keyBoxes.firstOrNull { it.contains(pt.x, pt.y) }
                ?: keyBoxes.minByOrNull { it.distanceTo(pt.x, pt.y) }?.takeIf { it.distanceTo(pt.x, pt.y) < 60f }

            if (hitKey != null) {
                if (rawChars.isEmpty() || rawChars.last() != hitKey.char) {
                    rawChars.add(hitKey.char)
                }
            }
        }
        return rawChars.joinToString("")
    }

    /**
     * Matches raw traversed characters against a vocabulary list.
     * Matches if word starts with first letter, ends with last letter, and contains intermediate letters in order.
     */
    fun matchGesture(traversedChars: String, vocabulary: List<String> = DEFAULT_VOCABULARY): List<String> {
        if (traversedChars.length < 2) return emptyList()
        val lowerTraversed = traversedChars.lowercase()
        val firstChar = lowerTraversed.first()
        val lastChar = lowerTraversed.last()

        return vocabulary.filter { word ->
            val lowerWord = word.lowercase()
            if (lowerWord.first() != firstChar || lowerWord.last() != lastChar) {
                false
            } else {
                isSubsequenceInOrder(lowerWord, lowerTraversed)
            }
        }.sortedBy { it.length }
    }

    private fun isSubsequenceInOrder(word: String, traversed: String): Boolean {
        var tIdx = 0
        for (wChar in word) {
            val foundIdx = traversed.indexOf(wChar, tIdx)
            if (foundIdx == -1) return false
            tIdx = foundIdx
        }
        return true
    }
}
