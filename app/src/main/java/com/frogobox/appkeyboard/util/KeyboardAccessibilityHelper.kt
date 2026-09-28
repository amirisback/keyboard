package com.frogobox.appkeyboard.util

import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard

/**
 * Accessibility and TalkBack helper providing phonetic spelling,
 * descriptive semantic labels, and spoken announcements for screen reader users.
 */
object KeyboardAccessibilityHelper {

    private val PHONETIC_MAP = mapOf(
        'A' to "Huruf A, Alfa",
        'B' to "Huruf B, Bravo",
        'C' to "Huruf C, Charlie",
        'D' to "Huruf D, Delta",
        'E' to "Huruf E, Echo",
        'F' to "Huruf F, Foxtrot",
        'G' to "Huruf G, Golf",
        'H' to "Huruf H, Hotel",
        'I' to "Huruf I, India",
        'J' to "Huruf J, Juliet",
        'K' to "Huruf K, Kilo",
        'L' to "Huruf L, Lima",
        'M' to "Huruf M, Mike",
        'N' to "Huruf N, November",
        'O' to "Huruf O, Oscar",
        'P' to "Huruf P, Papa",
        'Q' to "Huruf Q, Quebec",
        'R' to "Huruf R, Romeo",
        'S' to "Huruf S, Sierra",
        'T' to "Huruf T, Tango",
        'U' to "Huruf U, Uniform",
        'V' to "Huruf V, Victor",
        'W' to "Huruf W, Whiskey",
        'X' to "Huruf X, X-ray",
        'Y' to "Huruf Y, Yankee",
        'Z' to "Huruf Z, Zulu"
    )

    /**
     * Resolves a spoken accessibility description for a given character code.
     */
    fun getKeyDescription(code: Int, isShifted: Boolean = false): String {
        return when (code) {
            ItemMainKeyboard.KEYCODE_DELETE -> "Hapus karakter sebelumnya"
            ItemMainKeyboard.KEYCODE_SHIFT -> if (isShifted) "Tombol Shift aktif" else "Tombol Shift nonaktif"
            ItemMainKeyboard.KEYCODE_SPACE -> "Spasi"
            ItemMainKeyboard.KEYCODE_ENTER -> "Kirim atau Baris Baru"
            ItemMainKeyboard.KEYCODE_MODE_CHANGE -> "Beralih simbol atau angka"
            ItemMainKeyboard.KEYCODE_EMOJI -> "Buka panel stiker dan emoji"
            ItemMainKeyboard.KEYCODE_TAB -> "Tombol Tabulasi"
            ItemMainKeyboard.KEYCODE_ARROW_LEFT -> "Kursor ke kiri"
            ItemMainKeyboard.KEYCODE_ARROW_RIGHT -> "Kursor ke kanan"
            ItemMainKeyboard.KEYCODE_ARROW_UP -> "Kursor ke atas"
            ItemMainKeyboard.KEYCODE_ARROW_DOWN -> "Kursor ke bawah"
            else -> {
                if (code <= 0) return ""
                val ch = code.toChar()
                val upper = ch.uppercaseChar()
                PHONETIC_MAP[upper] ?: "Karakter $ch"
            }
        }
    }

    /**
     * Generates accessible announcements for layout and mode changes.
     */
    fun getModeAnnouncement(mode: String): String {
        return when (mode.uppercase()) {
            "SPLIT" -> "Mode keyboard terbelah aktif untuk dua jempol"
            "LEFT" -> "Mode satu tangan sisi kiri aktif"
            "RIGHT" -> "Mode satu tangan sisi kanan aktif"
            "OFF" -> "Mode keyboard layar penuh aktif"
            "INCOGNITO" -> "Mode privasi penyamaran aktif. Riwayat dan saran dinonaktifkan"
            else -> "Mode $mode"
        }
    }
}
