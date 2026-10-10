package com.frogobox.libkeyboard.util

import kotlin.math.abs
import kotlin.math.hypot

/**
 * Glide / Gesture Typing helper.
 * Tracks continuous swipe points, extracts key trajectories, and matches against vocabulary words.
 */
object GestureTypingHelper {

    data class PathPoint(
        val x: Float,
        val y: Float,
        val timestamp: Long = System.currentTimeMillis()
    )

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

    private val dynamicVocabulary = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

    val DEFAULT_VOCABULARY = listOf(
        // Indonesian Common Words
        "halo", "hello", "keyboard", "terima", "kasih", "selamat", "pagi", "siang", "sore",
        "malam", "bagus", "cepat", "bisa", "siap", "oke", "mantap", "sudah", "sedang", "kemarin",
        "besok", "kamu", "saya", "kita", "kami", "mereka", "dengan", "untuk", "akan", "dalam",
        "yang", "dan", "dari", "ini", "itu", "ada", "tidak", "bukan", "hanya", "juga",
        "apa", "siapa", "mana", "kapan", "kenapa", "bagaimana", "rumah", "jalan", "kerja",
        "makan", "minum", "suka", "mau", "ingin", "tahu", "lihat", "baca", "tulis", "kirim",
        "pesan", "kabar", "hari", "waktu", "tempat", "orang", "teman", "sahabat", "keluarga",
        "cinta", "senang", "sehat", "uang", "aplikasi", "tombol", "usap", "ketik", "layar",
        "fitur", "pintar", "hebat", "luar", "biasa", "buku", "pintu", "buka", "tutup",
        // English Common Words
        "good", "great", "thanks", "thank", "please", "yes", "no", "work", "play", "time",
        "swipe", "gesture", "type", "typing", "quick", "fast", "speed", "smooth", "happy",
        "today", "tomorrow", "yesterday", "where", "what", "when", "why", "which", "how",
        "friend", "family", "cool", "nice", "sure", "fine", "need", "want", "know", "see",
        "look", "think", "come", "make", "take", "like", "have", "would", "could", "should",
        "there", "their", "about", "after", "before", "with", "from", "this", "that", "here",
        "well", "done", "best", "android", "mobile", "smart", "write", "read", "book", "door"
    )

    /**
     * Add custom or learned words to the dynamic vocabulary.
     */
    fun addVocabulary(words: Collection<String>) {
        words.forEach { word ->
            val clean = word.lowercase().trim()
            if (clean.isNotBlank()) {
                dynamicVocabulary.add(clean)
            }
        }
    }

    /**
     * Register a single custom or learned word to dynamic vocabulary.
     */
    fun registerWord(word: String) {
        val clean = word.lowercase().trim()
        if (clean.isNotBlank()) {
            dynamicVocabulary.add(clean)
        }
    }

    /**
     * Clear dynamic vocabulary.
     */
    fun clearDynamicVocabulary() {
        dynamicVocabulary.clear()
    }

    /**
     * Gets combined vocabulary (built-in + dynamically added).
     */
    fun getAllVocabulary(): List<String> {
        val set = LinkedHashSet<String>(DEFAULT_VOCABULARY)
        set.addAll(dynamicVocabulary)
        return set.toList()
    }

    /**
     * Compresses consecutive duplicate characters (e.g. "hello" -> "helo", "good" -> "god").
     */
    fun compressConsecutiveDuplicates(str: String): String {
        if (str.length <= 1) return str
        val sb = java.lang.StringBuilder()
        for (i in str.indices) {
            if (i == 0 || str[i] != str[i - 1]) {
                sb.append(str[i])
            }
        }
        return sb.toString()
    }

    /**
     * Resolves continuous trajectory points to a sequence of hit characters.
     * Includes linear interpolation between distant touch points so fast swipes don't skip keys.
     */
    fun resolveTrajectoryToChars(points: List<PathPoint>, keyBoxes: List<KeyBoundingBox>): String {
        if (points.isEmpty() || keyBoxes.isEmpty()) return ""

        val letterBoxes = keyBoxes.filter { it.char.isLetter() }
        if (letterBoxes.isEmpty()) return ""

        // Adaptive hit distance based on average key width
        val avgKeyWidth = letterBoxes.map { it.right - it.left }.average().toFloat().takeIf { it > 0f } ?: 80f
        val maxDist = avgKeyWidth * 0.95f

        // Interpolate points between distant samples on fast swipes
        val interpolatedPoints = mutableListOf<PathPoint>()
        for (i in points.indices) {
            val curr = points[i]
            if (i > 0) {
                val prev = points[i - 1]
                val d = hypot(curr.x - prev.x, curr.y - prev.y)
                if (d > 35f) {
                    val steps = (d / 25f).toInt()
                    for (step in 1 until steps) {
                        val fraction = step.toFloat() / steps
                        interpolatedPoints.add(
                            PathPoint(
                                x = prev.x + (curr.x - prev.x) * fraction,
                                y = prev.y + (curr.y - prev.y) * fraction
                            )
                        )
                    }
                }
            }
            interpolatedPoints.add(curr)
        }

        val rawChars = mutableListOf<Char>()
        for (pt in interpolatedPoints) {
            val hitKey = letterBoxes.firstOrNull { it.contains(pt.x, pt.y) }
                ?: letterBoxes.minByOrNull { it.distanceTo(pt.x, pt.y) }?.takeIf { it.distanceTo(pt.x, pt.y) < maxDist }

            if (hitKey != null) {
                if (rawChars.isEmpty() || rawChars.last() != hitKey.char) {
                    rawChars.add(hitKey.char)
                }
            }
        }
        return rawChars.joinToString("")
    }

    /**
     * Checks if [wordSequence] characters appear in order within [traversedChars].
     */
    fun isSubsequenceInOrder(wordSequence: String, traversedChars: String): Boolean {
        var tIdx = 0
        for (wChar in wordSequence) {
            var found = false
            while (tIdx < traversedChars.length) {
                if (traversedChars[tIdx] == wChar) {
                    found = true
                    tIdx++
                    break
                }
                tIdx++
            }
            if (!found) return false
        }
        return true
    }

    /**
     * Matches raw traversed characters against a vocabulary list.
     * Supports duplicate-character resilience (e.g., matching "hello" from "helo")
     * and edge-hit tolerance for beginning and ending key strokes.
     */
    fun matchGesture(
        traversedChars: String,
        vocabulary: List<String> = getAllVocabulary(),
        maxResults: Int = 10
    ): List<String> {
        val filteredTraversed = traversedChars.filter { it.isLetter() }.lowercase()
        if (filteredTraversed.length < 2) return emptyList()
        val compressedTraversed = compressConsecutiveDuplicates(filteredTraversed)

        // Starting candidate characters: primary is first char, fallback to second if length >= 3
        val startCandidates = if (compressedTraversed.length >= 3) {
            listOf(compressedTraversed[0], compressedTraversed[1])
        } else {
            listOf(compressedTraversed.first())
        }

        // Ending candidate characters: primary is last char, fallback to second-to-last if length >= 3
        val endCandidates = if (compressedTraversed.length >= 3) {
            listOf(compressedTraversed.last(), compressedTraversed[compressedTraversed.length - 2])
        } else {
            listOf(compressedTraversed.last())
        }

        val matches = mutableListOf<Pair<String, Int>>() // Pair of (word, score penalty)

        for (word in vocabulary) {
            val lowerWord = word.lowercase()
            if (lowerWord.length < 2 || !lowerWord.all { it.isLetter() }) continue

            val wordFirst = lowerWord.first()
            val wordLast = lowerWord.last()

            val startIdx = startCandidates.indexOf(wordFirst)
            val endIdx = endCandidates.indexOf(wordLast)

            if (startIdx == -1 || endIdx == -1) continue

            val penalty = startIdx * 2 + endIdx * 2
            val compressedWord = compressConsecutiveDuplicates(lowerWord)

            val isMatch = isSubsequenceInOrder(compressedWord, compressedTraversed) ||
                    isSubsequenceInOrder(compressedWord, filteredTraversed) ||
                    isSubsequenceInOrder(lowerWord, filteredTraversed)

            if (isMatch) {
                matches.add(word to penalty)
            }
        }

        // Rank matches: exact start/end match (penalty 0) first, then exact compressed match, then closest length
        return matches.distinctBy { it.first }.sortedWith(
            compareBy<Pair<String, Int>> { it.second }
                .thenBy { (word, _) ->
                    val comp = compressConsecutiveDuplicates(word.lowercase())
                    if (comp == compressedTraversed) 0 else 1
                }
                .thenBy { (word, _) -> abs(word.length - compressedTraversed.length) }
                .thenBy { it.first.length }
        ).map { it.first }.take(maxResults)
    }

    /**
     * Complete pipeline: resolves points to chars and matches against vocabulary.
     */
    fun resolveAndMatch(
        points: List<PathPoint>,
        keyBoxes: List<KeyBoundingBox>,
        vocabulary: List<String> = getAllVocabulary()
    ): List<String> {
        val chars = resolveTrajectoryToChars(points, keyBoxes)
        if (chars.length < 2) return emptyList()
        return matchGesture(chars, vocabulary)
    }
}
