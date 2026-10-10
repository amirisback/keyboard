package com.frogobox.appkeyboard.util

import com.frogobox.appkeyboard.model.AutoTextEntity

/**
 * High-speed on-device text shortcut expansion engine.
 * Detects shorthand triggers typed by the user and provides instant full-text expansions
 * surfaced directly in the keyboard candidate strip.
 */
object InlineTextExpanderHelper {

    data class TextExpansionMatch(
        val trigger: String,
        val expandedText: String,
        val label: String
    )

    // Built-in standard quick expansion library (Indonesian & English)
    private val DEFAULT_SNIPPETS = mapOf(
        "otw" to TextExpansionMatch("otw", "On the way!", "OTW"),
        "omw" to TextExpansionMatch("omw", "On my way!", "OMW"),
        "brb" to TextExpansionMatch("brb", "Be right back", "BRB"),
        "thx" to TextExpansionMatch("thx", "Terima kasih banyak!", "Thanks"),
        "ty" to TextExpansionMatch("ty", "Thank you very much!", "Thank You"),
        "gws" to TextExpansionMatch("gws", "Semoga lekas sembuh! (Get well soon)", "GWS"),
        "asap" to TextExpansionMatch("asap", "As soon as possible", "ASAP"),
        "rek" to TextExpansionMatch("rek", "Silakan transfer ke No. Rekening: BCA / Mandiri an. Pelanggan", "Rekening"),
        "!alamat" to TextExpansionMatch("!alamat", "Alamat: Jl. Sudirman No. 45, Jakarta Pusat", "Alamat"),
        "!email" to TextExpansionMatch("!email", "email.pengguna@example.com", "Email"),
        "siap" to TextExpansionMatch("siap", "Siap, segera kami proses ya!", "Siap Proses"),
        "makasih" to TextExpansionMatch("makasih", "Terima kasih banyak atas kerjasamanya!", "Terima Kasih")
    )

    /**
     * Searches for an expansion match for the currently typed word.
     *
     * @param word The current word fragment before the cursor.
     * @param customAutoTexts Custom AutoText database records created by user.
     * @return [TextExpansionMatch] if found, or null if no expansion matches.
     */
    fun findExpansion(
        word: String,
        customAutoTexts: List<AutoTextEntity> = emptyList()
    ): TextExpansionMatch? {
        val trimmed = word.trim()
        if (trimmed.isEmpty()) return null

        val lower = trimmed.lowercase()

        // 1. Check custom user AutoText database first (highest priority)
        val customMatch = customAutoTexts.firstOrNull {
            it.title.equals(trimmed, ignoreCase = true) ||
            it.title.equals(lower, ignoreCase = true)
        }
        if (customMatch != null) {
            return TextExpansionMatch(
                trigger = customMatch.title,
                expandedText = customMatch.body,
                label = customMatch.title
            )
        }

        // 2. Check built-in quick snippets
        val defaultMatch = DEFAULT_SNIPPETS[lower] ?: DEFAULT_SNIPPETS[trimmed]
        if (defaultMatch != null) {
            return defaultMatch
        }

        // 3. Fallback: Check if AutoText body or title starts with the prefix trigger
        val partialAutoText = customAutoTexts.firstOrNull {
            it.title.startsWith(trimmed, ignoreCase = true) && trimmed.length >= 2
        }
        if (partialAutoText != null) {
            return TextExpansionMatch(
                trigger = trimmed,
                expandedText = partialAutoText.body,
                label = partialAutoText.title
            )
        }

        return null
    }

    /**
     * Formats a human-readable hero preview text for the candidate strip chip.
     */
    fun formatChipLabel(match: TextExpansionMatch): String {
        val snippetPreview = if (match.expandedText.length > 24) {
            "${match.expandedText.take(24)}…"
        } else {
            match.expandedText
        }
        return "⚡ ${match.label.uppercase()}: $snippetPreview"
    }
}
