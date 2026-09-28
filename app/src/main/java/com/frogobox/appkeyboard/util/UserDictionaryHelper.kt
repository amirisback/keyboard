package com.frogobox.appkeyboard.util

import android.content.Context
import android.content.SharedPreferences

/**
 * On-device adaptive user dictionary learning engine.
 * Records frequency of user-typed words completely locally on-device.
 * Guarantees zero learning in Incognito/Password modes.
 */
object UserDictionaryHelper {

    private const val PREF_NAME = "frogo_user_dictionary_pref"
    private const val PREF_KEY_WORDS = "learned_words_data"
    private const val MAX_STORED_WORDS = 500

    private val VALID_WORD_REGEX = Regex("^[a-zA-Z]{2,32}$")

    var inMemoryWordsForTest: MutableMap<String, Int>? = null

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Checks whether a word meets hygiene and security criteria for learning.
     * Bypasses immediately if under Incognito or Password entry modes.
     */
    fun canLearnWord(word: String, isIncognito: Boolean, isPassword: Boolean): Boolean {
        if (isIncognito || isPassword) return false
        val cleanWord = word.trim().lowercase()
        return VALID_WORD_REGEX.matches(cleanWord)
    }

    /**
     * Learns a typed word if it meets hygiene and security criteria.
     */
    fun learnWord(context: Context?, word: String, isIncognito: Boolean, isPassword: Boolean): Boolean {
        if (!canLearnWord(word, isIncognito, isPassword)) return false
        val cleanWord = word.trim().lowercase()

        val map = getLearnedWords(context).toMutableMap()
        val currentCount = map[cleanWord] ?: 0
        map[cleanWord] = currentCount + 1

        // Cap storage size by keeping highest frequency words
        if (map.size > MAX_STORED_WORDS) {
            val sorted = map.entries.sortedByDescending { it.value }.take(MAX_STORED_WORDS)
            map.clear()
            sorted.forEach { map[it.key] = it.value }
        }

        saveLearnedWords(context, map)
        return true
    }

    fun getLearnedWords(context: Context? = null): Map<String, Int> {
        inMemoryWordsForTest?.let { return it }
        if (context == null) return emptyMap()
        val raw = try {
            getPrefs(context).getString(PREF_KEY_WORDS, "") ?: ""
        } catch (_: Exception) {
            ""
        }
        if (raw.isBlank()) return emptyMap()

        val result = mutableMapOf<String, Int>()
        raw.split(";").forEach { entry ->
            val parts = entry.split(":")
            if (parts.size == 2) {
                val count = parts[1].toIntOrNull() ?: 1
                result[parts[0]] = count
            }
        }
        return result
    }

    fun getSuggestions(context: Context? = null, prefix: String, limit: Int = 3): List<String> {
        if (prefix.isBlank()) return emptyList()
        val lowerPrefix = prefix.lowercase()
        val map = getLearnedWords(context)

        return map.filterKeys { it.startsWith(lowerPrefix) && it != lowerPrefix }
            .entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key }
    }

    fun clearLearnedWords(context: Context? = null) {
        inMemoryWordsForTest?.clear()
        if (context != null) {
            try {
                getPrefs(context).edit().remove(PREF_KEY_WORDS).apply()
            } catch (_: Exception) {}
        }
    }

    private fun saveLearnedWords(context: Context?, map: Map<String, Int>) {
        if (inMemoryWordsForTest != null) {
            inMemoryWordsForTest?.clear()
            inMemoryWordsForTest?.putAll(map)
            return
        }
        if (context == null) return
        val serialized = map.entries.joinToString(";") { "${it.key}:${it.value}" }
        try {
            getPrefs(context).edit().putString(PREF_KEY_WORDS, serialized).apply()
        } catch (_: Exception) {}
    }
}
