package com.frogobox.appkeyboard.util

import java.util.Locale

/**
 * On-device AI Writing and Tone Transformer for smart text polishing and stylistic adjustments.
 * Operates 100% locally with zero network latency and privacy isolation.
 */
object AiTextTransformer {

    enum class AiToneStyle(val title: String, val chipLabel: String, val iconEmoji: String) {
        POLISH("Rapikan & Ejaan", "Rapikan", "✨"),
        FORMAL("Formal & Profesional", "Formal", "👔"),
        POLITE("Ramah & Sopan", "Sopan", "🤝"),
        CASUAL("Kasual & Santai", "Kasual", "😊"),
        SLANG("Gaul & Tren", "Gaul", "😎"),
        SUMMARIZE("Ringkas & Inti", "Ringkas", "📝")
    }

    private val TYPO_NORMALIZATION_MAP = linkedMapOf(
        "\\bsy\\b" to "saya",
        "\\baq\\b" to "aku",
        "\\byg\\b" to "yang",
        "\\byng\\b" to "yang",
        "\\bdgn\\b" to "dengan",
        "\\bbgt\\b" to "banget",
        "\\bbngt\\b" to "banget",
        "\\bgk\\b" to "tidak",
        "\\bgak\\b" to "tidak",
        "\\bga\\b" to "tidak",
        "\\bngga\\b" to "tidak",
        "\\bnggak\\b" to "tidak",
        "\\budh\\b" to "sudah",
        "\\bsdh\\b" to "sudah",
        "\\bblm\\b" to "belum",
        "\\bklo\\b" to "kalau",
        "\\bkalo\\b" to "kalau",
        "\\bgpp\\b" to "tidak apa-apa",
        "\\btks\\b" to "terima kasih",
        "\\bmksih\\b" to "terima kasih",
        "\\bmakasih\\b" to "terima kasih",
        "\\bthx\\b" to "thanks",
        "\\bty\\b" to "thank you",
        "\\bpls\\b" to "please",
        "\\bplz\\b" to "please",
        "\\bu\\b" to "you",
        "\\br\\b" to "are"
    )

    private val FORMAL_DICTIONARY = linkedMapOf(
        "\\b(gua|gue|gw|aku)\\b" to "saya",
        "\\b(lu|lo|elu|kamu|km)\\b" to "Anda",
        "\\b(nggak|gak|gk|ga)\\b" to "tidak",
        "\\b(udah|udh)\\b" to "sudah",
        "\\b(banget|bgt)\\b" to "sangat",
        "\\b(kalo|klo)\\b" to "apabila",
        "\\bgimana\\b" to "bagaimana",
        "\\b(kenapa|kenape)\\b" to "mengapa",
        "\\bcuma\\b" to "hanya",
        "\\bbikin\\b" to "membuat",
        "\\bngasih\\b" to "memberikan",
        "\\b(?<!terima\\s)kasih\\b" to "memberikan",
        "\\bngomong\\b" to "berbicara",
        "\\b(pengen|pingin)\\b" to "ingin",
        "\\bmakasih\\b" to "terima kasih",
        "\\bwanna\\b" to "would like to",
        "\\bgonna\\b" to "going to"
    )

    private val CASUAL_DICTIONARY = linkedMapOf(
        "\\bsaya\\b" to "aku",
        "\\b(anda|Anda)\\b" to "kamu",
        "\\btidak\\b" to "nggak",
        "\\bsudah\\b" to "udah",
        "\\bsangat\\b" to "banget",
        "\\b(apabila|jika)\\b" to "kalau",
        "\\bbagaimana\\b" to "gimana",
        "\\bmengapa\\b" to "kenapa",
        "\\bhanya\\b" to "cuma",
        "\\bmembuat\\b" to "bikin",
        "\\bmemberikan\\b" to "ngasih"
    )

    private val SLANG_DICTIONARY = linkedMapOf(
        "\\b(menurut saya|menurutku)\\b" to "honestly",
        "\\b(sangat|banget)\\b" to "parah abis",
        "\\b(bagus|keren)\\b" to "gokil parah",
        "\\b(tidak tahu|gatau)\\b" to "skip dulu",
        "\\b(benar|betul)\\b" to "valid no debat",
        "\\bsantai\\b" to "chill aja",
        "\\bsebenarnya\\b" to "literally"
    )

    private val FILLER_WORDS = listOf(
        "pada dasarnya",
        "sebenarnya",
        "seperti yang kita ketahui",
        "dalam hal ini",
        "sebagaimana mestinya",
        "basically",
        "actually",
        "in fact"
    )

    /**
     * Transforms input text based on the requested [AiToneStyle].
     */
    fun transform(text: String, tone: AiToneStyle): String {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return ""

        return when (tone) {
            AiToneStyle.POLISH -> polishText(trimmed)
            AiToneStyle.FORMAL -> formalizeText(trimmed)
            AiToneStyle.POLITE -> politeText(trimmed)
            AiToneStyle.CASUAL -> casualizeText(trimmed)
            AiToneStyle.SLANG -> slangText(trimmed)
            AiToneStyle.SUMMARIZE -> summarizeText(trimmed)
        }
    }

    private fun polishText(text: String): String {
        var result = normalizeSpacingAndPunctuation(text)
        for ((pattern, replacement) in TYPO_NORMALIZATION_MAP) {
            result = result.replace(Regex(pattern, RegexOption.IGNORE_CASE), replacement)
        }
        return capitalizeSentences(result)
    }

    private fun formalizeText(text: String): String {
        var result = polishText(text)
        for ((pattern, replacement) in FORMAL_DICTIONARY) {
            result = result.replace(Regex(pattern, RegexOption.IGNORE_CASE), replacement)
        }
        val capitalized = capitalizeSentences(result)
        return if (!capitalized.endsWith(".") && !capitalized.endsWith("!") && !capitalized.endsWith("?")) {
            "$capitalized."
        } else {
            capitalized
        }
    }

    private fun politeText(text: String): String {
        val formalized = formalizeText(text)
        val lower = formalized.lowercase(Locale.ROOT)
        val prefix = when {
            lower.startsWith("tolong") || lower.startsWith("mohon") -> ""
            lower.contains("kirim") || lower.contains("bantu") || lower.contains("minta") -> "Mohon bantuannya, "
            else -> "Dengan hormat, "
        }
        val suffix = when {
            lower.contains("terima kasih") -> ""
            else -> " Terima kasih banyak."
        }
        return "$prefix$formalized$suffix".trim()
    }

    private fun casualizeText(text: String): String {
        var result = normalizeSpacingAndPunctuation(text)
        for ((pattern, replacement) in CASUAL_DICTIONARY) {
            result = result.replace(Regex(pattern, RegexOption.IGNORE_CASE), replacement)
        }
        val capitalized = capitalizeSentences(result)
        return if (!capitalized.endsWith("!") && !capitalized.endsWith("?") && !capitalized.endsWith(".")) {
            "$capitalized ya!"
        } else {
            capitalized
        }
    }

    private fun slangText(text: String): String {
        var result = normalizeSpacingAndPunctuation(text)
        for ((pattern, replacement) in SLANG_DICTIONARY) {
            result = result.replace(Regex(pattern, RegexOption.IGNORE_CASE), replacement)
        }
        val capitalized = capitalizeSentences(result)
        return if (!capitalized.endsWith("🔥") && !capitalized.endsWith("!")) {
            "$capitalized 🔥"
        } else {
            capitalized
        }
    }

    private fun summarizeText(text: String): String {
        var cleaned = text
        for (filler in FILLER_WORDS) {
            cleaned = cleaned.replace(Regex("\\b$filler\\b,?\\s*", RegexOption.IGNORE_CASE), "")
        }
        val sentences = cleaned.split(Regex("(?<=[.!?])\\s+"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (sentences.size <= 1) {
            return "• " + capitalizeSentences(cleaned.trim())
        }

        return sentences.take(3).joinToString("\n") { "• " + capitalizeSentences(it) }
    }

    private fun normalizeSpacingAndPunctuation(text: String): String {
        var result = text.replace(Regex("\\s+"), " ")
        result = result.replace(Regex("\\s+([,.!?:;])"), "$1")
        result = result.replace(Regex("([,.!?:;])([^\\s0-9])"), "$1 $2")
        return result.trim()
    }

    private fun capitalizeSentences(text: String): String {
        if (text.isEmpty()) return text
        val sentences = text.split(Regex("(?<=[.!?])\\s+"))
        return sentences.joinToString(" ") { sentence ->
            if (sentence.isNotEmpty()) {
                sentence.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            } else {
                sentence
            }
        }
    }
}
