package com.frogobox.appkeyboard.suggestion

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.min

/**
 * Intelligent on-device Word Suggestion & Auto-Correction Engine
 * Provides sub-millisecond candidate computation satisfying GitHub Issue #50:
 * - Section 1: User's typed word (verbatim)
 * - Section 2: Predicted word (prefix match / completion)
 * - Section 3: Auto-correct word (edit distance / typo correction)
 */
@Singleton
class WordSuggestionEngine @Inject constructor() {

    // Common instant typo and shorthand substitution mapping
    private val commonTypoMap = mapOf(
        "teh" to "the",
        "wht" to "what",
        "adn" to "and",
        "dont" to "don't",
        "cant" to "can't",
        "wont" to "won't",
        "im" to "I'm",
        "ive" to "I've",
        "youre" to "you're",
        "theyre" to "they're",
        "alot" to "a lot",
        "helo" to "hello",
        "hw" to "how",
        "pls" to "please",
        "thx" to "thanks",
        "ty" to "thank you",
        "u" to "you",
        "r" to "are",
        "ur" to "your",
        "recive" to "receive",
        "recieve" to "receive",
        "becuase" to "because",
        "definately" to "definitely",
        "seperate" to "separate",
        "goverment" to "government",
        "occured" to "occurred",
        "untill" to "until",
        "beleive" to "believe",
        "frogo" to "Frogo"
    )

    // In-memory frequency-ranked dictionary
    private val dictionaryList = CopyOnWriteArrayList<String>()
    private val dictionarySet = ConcurrentHashMap.newKeySet<String>()
    private val userCustomWords = ConcurrentHashMap.newKeySet<String>()

    @Volatile
    private var isDictionaryLoaded = false

    init {
        loadBuiltinFallbackVocabulary()
    }

    /**
     * Fallback high-frequency words loaded synchronously so engine works immediately
     */
    private fun loadBuiltinFallbackVocabulary() {
        val fallbackWords = listOf(
            "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
            "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
            "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
            "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
            "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
            "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
            "people", "into", "year", "your", "good", "some", "could", "them", "see", "other",
            "than", "then", "now", "look", "only", "come", "its", "over", "think", "also",
            "back", "after", "use", "two", "how", "our", "work", "first", "well", "way",
            "even", "new", "want", "because", "any", "these", "give", "day", "most", "us",
            "hello", "thanks", "please", "sorry", "today", "tomorrow", "keyboard", "android",
            "apple", "application", "screen", "mobile", "message", "email", "happy", "great",
            "help", "home", "call", "start", "stop", "test", "ready", "super", "cool", "fine"
        )
        for (w in fallbackWords) {
            addWordInternal(w)
        }
    }

    /**
     * Loads extended dictionary from assets/text/dictionary_en.txt
     */
    fun loadDictionaryFromAsset(context: Context) {
        if (isDictionaryLoaded) return
        try {
            val assetManager = context.assets
            val inputStream = assetManager.open("text/dictionary_en.txt")
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    val word = line.trim().lowercase()
                    if (word.isNotEmpty() && !dictionarySet.contains(word)) {
                        addWordInternal(word)
                    }
                    line = reader.readLine()
                }
            }
            isDictionaryLoaded = true
        } catch (_: Exception) {
            // Gracefully keep fallback vocabulary if asset read fails
        }
    }

    private fun addWordInternal(word: String) {
        if (!dictionarySet.contains(word)) {
            dictionarySet.add(word)
            dictionaryList.add(word)
        }
    }

    /**
     * Allows dynamically learning or adding user custom words
     */
    fun addUserWord(word: String) {
        val trimmed = word.trim()
        if (trimmed.length > 1) {
            userCustomWords.add(trimmed.lowercase())
            addWordInternal(trimmed.lowercase())
        }
    }

    /**
     * Core suggestion generation returning the 3 required sections:
     * - Section 1: User's verbatim word
     * - Section 2: Predicted completion / highest rank prediction
     * - Section 3: Auto-corrected candidate or alternative suggestion
     */
    fun getSuggestions(input: String): SuggestionResult {
        val trimmedInput = input.trim()
        if (trimmedInput.isEmpty()) {
            return SuggestionResult(
                userWord = "",
                predictedWord = matchCasing(input, "I"),
                autoCorrectWord = matchCasing(input, "The")
            )
        }

        val lowerQuery = trimmedInput.lowercase()
        val userWord = trimmedInput

        // 1. Direct typo replacement check
        val typoMatch = commonTypoMap[lowerQuery]

        // 2. Find prefix predictions
        val prefixMatches = mutableListOf<String>()
        for (word in dictionaryList) {
            if (word.startsWith(lowerQuery)) {
                prefixMatches.add(word)
                if (prefixMatches.size >= 5) break
            }
        }

        // 3. Compute predicted word (Section 2)
        val predictedWordRaw = when {
            typoMatch != null -> typoMatch
            prefixMatches.isNotEmpty() -> {
                // If the exact word is typed and there is a longer continuation, use continuation
                if (prefixMatches[0] == lowerQuery && prefixMatches.size > 1) {
                    prefixMatches[1]
                } else {
                    prefixMatches[0]
                }
            }
            else -> findClosestCorrection(lowerQuery) ?: lowerQuery
        }

        // 4. Compute auto-correct word (Section 3)
        val autoCorrectWordRaw = when {
            typoMatch != null -> {
                // Typo was matched, provide alternative prefix or closest candidate
                prefixMatches.firstOrNull { it != lowerQuery && it != typoMatch.lowercase() }
                    ?: findClosestCorrection(lowerQuery, exclude = listOf(typoMatch.lowercase()))
                    ?: lowerQuery
            }
            !dictionarySet.contains(lowerQuery) -> {
                // Misspelled: find best correction different from predicted
                findClosestCorrection(lowerQuery, exclude = listOf(predictedWordRaw.lowercase()))
                    ?: prefixMatches.firstOrNull { it != predictedWordRaw.lowercase() }
                    ?: lowerQuery
            }
            prefixMatches.size > 1 -> {
                // Word is valid, provide alternative candidate
                val secondOption = prefixMatches.firstOrNull { it != predictedWordRaw.lowercase() && it != lowerQuery }
                secondOption ?: (prefixMatches.getOrNull(1) ?: lowerQuery)
            }
            else -> {
                findClosestCorrection(lowerQuery, exclude = listOf(predictedWordRaw.lowercase()))
                    ?: lowerQuery
            }
        }

        return SuggestionResult(
            userWord = userWord,
            predictedWord = matchCasing(trimmedInput, predictedWordRaw),
            autoCorrectWord = matchCasing(trimmedInput, autoCorrectWordRaw)
        )
    }

    /**
     * Finds closest word in dictionary within Levenshtein distance <= 2
     */
    private fun findClosestCorrection(query: String, exclude: List<String> = emptyList()): String? {
        if (query.isEmpty()) return null
        var bestMatch: String? = null
        var minDistance = Int.MAX_VALUE

        val maxAllowedDistance = if (query.length <= 3) 1 else 2

        for (word in dictionaryList) {
            if (exclude.contains(word) || word == query) continue

            // Quick length filter: skip words with length difference > maxAllowedDistance
            if (abs(word.length - query.length) > maxAllowedDistance) continue

            // Bonus heuristic: prefer words sharing the first letter
            val firstCharSame = word[0] == query[0]
            val distance = calculateLevenshteinDistance(query, word)

            val adjustedDistance = if (firstCharSame) distance else distance + 1

            if (distance <= maxAllowedDistance && adjustedDistance < minDistance) {
                minDistance = adjustedDistance
                bestMatch = word
                if (distance == 1 && firstCharSame) {
                    // Fast break on high-confidence match
                    break
                }
            }
        }

        return bestMatch
    }

    /**
     * Efficient iterative Levenshtein Distance calculation
     */
    fun calculateLevenshteinDistance(s1: String, s2: String): Int {
        val len1 = s1.length
        val len2 = s2.length

        var prev = IntArray(len2 + 1) { it }
        var curr = IntArray(len2 + 1)

        for (i in 1..len1) {
            curr[0] = i
            for (j in 1..len2) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                curr[j] = min(
                    min(curr[j - 1] + 1, prev[j] + 1),
                    prev[j - 1] + cost
                )
            }
            val temp = prev
            prev = curr
            curr = temp
        }

        return prev[len2]
    }

    /**
     * Adapts candidate string casing to match the user's input style:
     * - UPPERCASE if input is all caps (e.g., "HEL" -> "HELLO")
     * - TitleCase if input is capitalized (e.g., "Hel" -> "Hello")
     * - lowercase otherwise (e.g., "hel" -> "hello")
     */
    fun matchCasing(source: String, candidate: String): String {
        if (source.isEmpty() || candidate.isEmpty()) return candidate
        return when {
            source.length > 1 && source.all { it.isLetter() && it.isUpperCase() } -> {
                candidate.uppercase()
            }
            source.first().isUpperCase() -> {
                candidate.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
            else -> {
                candidate.lowercase()
            }
        }
    }

}
