package com.frogobox.appkeyboard.util

import com.frogobox.appkeyboard.model.ClipboardItem
import java.util.regex.Pattern

/**
 * Security and privacy guard for clipboard data hygiene:
 * - Detects sensitive authentication codes (OTPs), credit cards, and credential tokens.
 * - Manages automatic purging of stale unpinned clips after the expiration window (15 minutes).
 * - Masks sensitive snippets to prevent shoulder-surfing and accidental disclosure.
 */
object ClipboardSecurityGuard {

    const val DEFAULT_PURGE_WINDOW_MS = 15 * 60 * 1000L // 15 minutes

    private val OTP_PATTERN = Pattern.compile("(?i)\\b(?:otp|kode|code|pin|verifikasi)?[:\\s-]*(\\d{4,8})\\b")
    private val CREDIT_CARD_PATTERN = Pattern.compile("\\b(?:\\d{4}[ -]?){3}\\d{4}\\b")
    private val TOKEN_PATTERN = Pattern.compile("(?i)\\b(?:bearer|token|apikey|secret|password|sandi)[:\\s=]+[a-zA-Z0-9_\\-\\.]+\\b")

    /**
     * Inspects text for sensitive information patterns (OTPs, credit cards, secret keys).
     */
    fun isSensitiveContent(text: String): Boolean {
        if (text.isBlank()) return false
        val trimmed = text.trim()
        if (CREDIT_CARD_PATTERN.matcher(trimmed).find()) return true
        if (TOKEN_PATTERN.matcher(trimmed).find()) return true

        // Standalone 4 to 8 digit code often copied from SMS/WhatsApp OTP
        if (trimmed.length in 4..8 && trimmed.all { it.isDigit() }) return true
        if (OTP_PATTERN.matcher(trimmed).find() && (trimmed.contains("otp", ignoreCase = true) || trimmed.contains("kode", ignoreCase = true))) {
            return true
        }

        return false
    }

    /**
     * Filters out expired temporary clipboard items while strictly preserving pinned clips.
     *
     * @param items List of clipboard items to evaluate.
     * @param currentTime Current timestamp in milliseconds.
     * @param maxAgeMs Maximum age in milliseconds for unpinned items before purging (default 15 mins).
     * @return Filtered list containing only valid, non-expired items.
     */
    fun filterExpiredClips(
        items: List<ClipboardItem>,
        currentTime: Long,
        maxAgeMs: Long = DEFAULT_PURGE_WINDOW_MS
    ): List<ClipboardItem> {
        return items.filter { item ->
            // Rule: Pinned items are NEVER purged
            if (item.isPinned) {
                true
            } else {
                val age = currentTime - item.timestamp
                age in 0..maxAgeMs
            }
        }
    }

    /**
     * Obfuscates sensitive portions of a string for safe display.
     */
    fun maskSensitiveText(text: String): String {
        if (!isSensitiveContent(text)) return text

        val trimmed = text.trim()
        return if (trimmed.length in 4..8 && trimmed.all { it.isDigit() }) {
            // Mask OTP: e.g. "123456" -> "****56"
            val visibleCount = 2.coerceAtMost(trimmed.length)
            val maskedPrefix = "*".repeat(trimmed.length - visibleCount)
            maskedPrefix + trimmed.takeLast(visibleCount)
        } else if (CREDIT_CARD_PATTERN.matcher(trimmed).find()) {
            // Mask card: "1234-5678-9012-3456" -> "****-****-****-3456"
            val cleanDigits = trimmed.filter { it.isDigit() }
            if (cleanDigits.length >= 12) {
                "****-****-****-${cleanDigits.takeLast(4)}"
            } else {
                "****"
            }
        } else {
            // Generic token masking
            "🔒 [Data Sensitif Dilindungi]"
        }
    }
}
