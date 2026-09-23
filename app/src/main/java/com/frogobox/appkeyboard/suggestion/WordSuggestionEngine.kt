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

    // Common instant typo and shorthand substitution mapping (English & Indonesian)
    private val commonTypoMap = mapOf(
        // English common typos & shorthands
        "teh" to "the",
        "wht" to "what",
        "adn" to "and",
        "dont" to "don't",
        "cant" to "can't",
        "wont" to "won't",
        "im" to "I'm",
        "ive" to "I've",
        "youre" to "you're",
        "theyre" to "theyre",
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
        "frogo" to "Frogo",

        // Indonesian common typos, conversational shorthands & seller slang
        "yg" to "yang",
        "dgn" to "dengan",
        "sy" to "saya",
        "km" to "kamu",
        "gak" to "tidak",
        "gk" to "tidak",
        "ga" to "tidak",
        "ngga" to "nggak",
        "sdh" to "sudah",
        "udh" to "sudah",
        "blm" to "belum",
        "bgt" to "banget",
        "trs" to "terus",
        "tdk" to "tidak",
        "kpn" to "kapan",
        "gmn" to "gimana",
        "bs" to "bisa",
        "bkn" to "bukan",
        "tlg" to "tolong",
        "mhn" to "mohon",
        "makasih" to "terima kasih",
        "mksh" to "terima kasih",
        "makasi" to "terima kasih",
        "tf" to "transfer",
        "rek" to "rekening",
        "brg" to "barang",
        "ongkir" to "ongkir",
        "cod" to "COD",
        "resi" to "resi",
        "bca" to "BCA",
        "bri" to "BRI",
        "bni" to "BNI",
        "mandiri" to "Mandiri",
        "jnt" to "J&T",
        "jne" to "JNE",
        "sicepat" to "SiCepat"
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
            "help", "home", "call", "start", "stop", "test", "ready", "super", "cool", "fine",
            // Core Indonesian Fallback
            "yang", "dan", "di", "ini", "itu", "dengan", "untuk", "dari", "tidak", "ada",
            "akan", "bisa", "sudah", "saya", "kamu", "kita", "mereka", "dia", "kami", "tapi",
            "karena", "juga", "hanya", "semua", "banyak", "lagi", "bila", "jika", "kalau", "saat",
            "pesanan", "barang", "paket", "produk", "ongkir", "resi", "rekening", "transfer", "lunas"
        )
        for (w in fallbackWords) {
            addWordInternal(w)
        }
    }

    /**
     * Loads extended bilingual dictionary from assets (dictionary_en.txt and dictionary_id.txt)
     */
    fun loadDictionaryFromAsset(context: Context) {
        if (isDictionaryLoaded) return
        val assetManager = context.assets
        val dictionaryFiles = listOf("text/dictionary_en.txt", "text/dictionary_id.txt")
        val batch = ArrayList<String>()

        for (fileName in dictionaryFiles) {
            try {
                val inputStream = assetManager.open(fileName)
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String? = reader.readLine()
                    while (line != null) {
                        val word = line.trim().lowercase()
                        if (word.isNotEmpty() && !dictionarySet.contains(word)) {
                            dictionarySet.add(word)
                            batch.add(word)
                        }
                        line = reader.readLine()
                    }
                }
            } catch (_: Exception) {
                // Gracefully continue to next file
            }
        }
        dictionaryList.addAll(batch)
        isDictionaryLoaded = true
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
            candidate.any { it.isUpperCase() } -> {
                candidate
            }
            else -> {
                candidate.lowercase()
            }
        }
    }

    /**
     * Evaluates whether [input] should be auto-corrected when spacebar is pressed.
     * Returns the replacement word if:
     * 1. A direct typo or shorthand substitution exists (e.g., "teh" -> "the", "yg" -> "yang").
     * 2. The word is not in dictionary and a high-confidence correction is available.
     * Returns null if the word is already valid or no auto-correction is warranted.
     */
    fun getAutoCorrectReplacement(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.length < 2) return null
        val lower = trimmed.lowercase()

        // 1. Direct typo or shorthand substitution (highest confidence)
        val typoMatch = commonTypoMap[lower]
        if (typoMatch != null) {
            return matchCasing(trimmed, typoMatch)
        }

        // 2. If the word is already recognized in dictionary, do not auto-correct
        if (dictionarySet.contains(lower)) {
            return null
        }

        // 3. For unrecognized words, look for close correction (Levenshtein distance <= 1 or <= 2)
        val suggestion = findClosestCorrection(lower)
        if (suggestion != null && !suggestion.equals(lower, ignoreCase = true)) {
            val dist = calculateLevenshteinDistance(lower, suggestion)
            if (trimmed.length <= 3 && dist == 1) {
                return matchCasing(trimmed, suggestion)
            } else if (trimmed.length > 3 && dist <= 2 && suggestion.first() == lower.first()) {
                return matchCasing(trimmed, suggestion)
            }
        }
        return null
    }

}
