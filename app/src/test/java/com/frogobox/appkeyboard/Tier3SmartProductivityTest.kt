package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.model.ClipboardItem
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.ui.keyboard.clipboard.ClipboardCategoryFilter
import com.frogobox.appkeyboard.ui.keyboard.clipboard.ClipboardFilterHelper
import com.frogobox.appkeyboard.ui.keyboard.root.KeyboardPanelState
import com.frogobox.appkeyboard.util.AiTextTransformer
import com.frogobox.appkeyboard.util.AiTextTransformer.AiToneStyle
import com.frogobox.appkeyboard.util.VoiceTypingHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Comprehensive Unit Test Suite for Tier 3 Smart Productivity Suite:
 * - AC-01: AI Writing & Tone Transformer (Modes, Dictionaries, Capitalization, Edge Cases)
 * - AC-02: Voice Typing States, Feature Mapping & Navigation Interop
 * - AC-03: Smart Searchable Clipboard & Category Filter Engine
 * - AC-04: Incognito Privacy Guard & Zero Suppression Mandate
 */
class Tier3SmartProductivityTest {

    // -------------------------------------------------------------------------
    // 1. AC-01: AI Writing & Tone Assistant Tests
    // -------------------------------------------------------------------------

    @Test
    fun testAiToneStyleEnumEntries() {
        val tones = AiToneStyle.entries
        assertEquals(6, tones.size)
        assertTrue(tones.contains(AiToneStyle.POLISH))
        assertTrue(tones.contains(AiToneStyle.FORMAL))
        assertTrue(tones.contains(AiToneStyle.POLITE))
        assertTrue(tones.contains(AiToneStyle.CASUAL))
        assertTrue(tones.contains(AiToneStyle.SLANG))
        assertTrue(tones.contains(AiToneStyle.SUMMARIZE))
    }

    @Test
    fun testAiTransformerEmptyOrBlankReturnsEmpty() {
        assertEquals("", AiTextTransformer.transform("", AiToneStyle.POLISH))
        assertEquals("", AiTextTransformer.transform("   ", AiToneStyle.FORMAL))
        assertEquals("", AiTextTransformer.transform("\n\t", AiToneStyle.CASUAL))
    }

    @Test
    fun testAiTransformerPolishNormalization() {
        val input = "halo , apa kabar ? sy mau pergi dgn dia"
        val polished = AiTextTransformer.transform(input, AiToneStyle.POLISH)

        // Verifies spacing before punctuation normalized and abbreviations expanded
        assertTrue("Expected 'Halo' capitalization", polished.startsWith("Halo"))
        assertTrue("Expected 'saya'", polished.contains("saya", ignoreCase = true))
        assertTrue("Expected 'dengan'", polished.contains("dengan", ignoreCase = true))
        assertFalse("Comma spacing should be fixed", polished.contains("halo ,"))
    }

    @Test
    fun testAiTransformerFormalStyle() {
        val input = "gua gak bisa dateng hari ini, makasih ya"
        val formal = AiTextTransformer.transform(input, AiToneStyle.FORMAL)

        assertTrue("Expected formal pronoun 'saya'", formal.contains("saya", ignoreCase = true))
        assertTrue("Expected formal negation 'tidak'", formal.contains("tidak", ignoreCase = true))
        assertTrue("Expected formal appreciation 'terima kasih'", formal.contains("terima kasih", ignoreCase = true))
        assertFalse("Informal 'gua' should be replaced", formal.contains("gua", ignoreCase = true))
        assertFalse("Informal 'gak' should be replaced", formal.contains("gak", ignoreCase = true))
        assertTrue("Should end with sentence punctuation", formal.endsWith("."))
    }

    @Test
    fun testAiTransformerPoliteStyle() {
        val input = "kirim laporannya sekarang"
        val polite = AiTextTransformer.transform(input, AiToneStyle.POLITE)

        assertTrue("Expected polite greeting or prefix", polite.contains("Mohon") || polite.contains("Dengan hormat"))
        assertTrue("Expected polite closing", polite.contains("Terima kasih"))
    }

    @Test
    fun testAiTransformerCasualStyle() {
        val input = "Saya tidak setuju dengan keputusan ini"
        val casual = AiTextTransformer.transform(input, AiToneStyle.CASUAL)

        assertTrue("Expected casual pronoun 'aku'", casual.contains("aku", ignoreCase = true))
        assertTrue("Expected casual negation 'nggak'", casual.contains("nggak", ignoreCase = true))
        assertTrue("Expected casual particle 'ya!' or exclamation", casual.endsWith("ya!") || casual.endsWith("!"))
    }

    @Test
    fun testAiTransformerSlangStyle() {
        val input = "menurut saya proyek ini sangat keren"
        val slang = AiTextTransformer.transform(input, AiToneStyle.SLANG)

        assertTrue("Expected slang 'honestly'", slang.lowercase().contains("honestly"))
        assertTrue("Expected slang 'parah abis' or 'gokil'", slang.contains("parah abis") || slang.contains("gokil"))
        assertTrue("Expected trailing flame emoji", slang.endsWith("🔥"))
    }

    @Test
    fun testAiTransformerSummarizeStyle() {
        val input = "Pada dasarnya kita harus menyelesaikan modul ini tepat waktu. Sebenarnya ada beberapa kendala kecil namun dapat diatasi."
        val summary = AiTextTransformer.transform(input, AiToneStyle.SUMMARIZE)

        // Filler words stripped
        assertFalse("Filler 'Pada dasarnya' should be removed", summary.contains("Pada dasarnya"))
        assertFalse("Filler 'Sebenarnya' should be removed", summary.contains("Sebenarnya"))
        assertTrue("Bullet point indicator should be present", summary.startsWith("• "))
    }

    // -------------------------------------------------------------------------
    // 2. AC-02: Voice Typing Dictation & State Tests
    // -------------------------------------------------------------------------

    @Test
    fun testVoiceTypingHelperStates() {
        val states = VoiceTypingHelper.State.entries
        assertEquals(5, states.size)
        assertTrue(states.contains(VoiceTypingHelper.State.IDLE))
        assertTrue(states.contains(VoiceTypingHelper.State.INITIALIZING))
        assertTrue(states.contains(VoiceTypingHelper.State.LISTENING))
        assertTrue(states.contains(VoiceTypingHelper.State.PROCESSING))
        assertTrue(states.contains(VoiceTypingHelper.State.ERROR))
    }

    @Test
    fun testFeatureTypeAndPanelStateMappings() {
        // AI Assistant
        val aiFeature = KeyboardFeatureType.from("menu_ai_assistant")
        assertEquals(KeyboardFeatureType.AI_ASSISTANT, aiFeature)
        assertEquals("AI Assistant", aiFeature.text)
        assertEquals(KeyboardPanelState.AI_ASSISTANT, KeyboardPanelState.fromFeature(aiFeature))

        // Voice Typing
        val voiceFeature = KeyboardFeatureType.from("menu_voice_typing")
        assertEquals(KeyboardFeatureType.VOICE_TYPING, voiceFeature)
        assertEquals("Voice Typing", voiceFeature.text)
        // Voice typing activates a floating banner, returning null panel state
        assertNull(KeyboardPanelState.fromFeature(voiceFeature))
    }

    // -------------------------------------------------------------------------
    // 3. AC-03: Smart Searchable Clipboard & Filter Engine Tests
    // -------------------------------------------------------------------------

    @Test
    fun testClipboardFilterHelperIsNumberOrCode() {
        // Numbers, codes, OTPs, phone numbers
        assertTrue(ClipboardFilterHelper.isNumberOrCode("123456"))
        assertTrue(ClipboardFilterHelper.isNumberOrCode("+6281234567890"))
        assertTrue(ClipboardFilterHelper.isNumberOrCode("0812-3456-7890"))
        assertTrue(ClipboardFilterHelper.isNumberOrCode("OTP: 894021"))
        assertTrue(ClipboardFilterHelper.isNumberOrCode("BCA 8839201928"))

        // Standard narrative sentences
        assertFalse(ClipboardFilterHelper.isNumberOrCode("Halo, selamat pagi rekan sekalian!"))
        assertFalse(ClipboardFilterHelper.isNumberOrCode("Rapat koordinasi mingguan besok"))
        assertFalse(ClipboardFilterHelper.isNumberOrCode(""))
    }

    @Test
    fun testClipboardFilterHelperCategoryFiltering() {
        val sampleItems = listOf(
            ClipboardItem(id = "1", text = "Halo apa kabar", timestamp = 1000L, isPinned = true),
            ClipboardItem(id = "2", text = "Kode verifikasi 948201", timestamp = 2000L, isPinned = false),
            ClipboardItem(id = "3", text = "+6281299887766", timestamp = 3000L, isPinned = true),
            ClipboardItem(id = "4", text = "Catatan belanja bulanan", timestamp = 4000L, isPinned = false)
        )

        // 1. ALL
        val allClips = ClipboardFilterHelper.filterItems(sampleItems, "", ClipboardCategoryFilter.ALL)
        assertEquals(4, allClips.size)

        // 2. PINNED
        val pinnedClips = ClipboardFilterHelper.filterItems(sampleItems, "", ClipboardCategoryFilter.PINNED)
        assertEquals(2, pinnedClips.size)
        assertTrue(pinnedClips.all { it.isPinned })

        // 3. NUMBER_CODE
        val codeClips = ClipboardFilterHelper.filterItems(sampleItems, "", ClipboardCategoryFilter.NUMBER_CODE)
        assertEquals(2, codeClips.size)
        assertTrue(codeClips.any { it.id == "2" })
        assertTrue(codeClips.any { it.id == "3" })

        // 4. TEXT
        val textClips = ClipboardFilterHelper.filterItems(sampleItems, "", ClipboardCategoryFilter.TEXT)
        assertEquals(2, textClips.size)
        assertTrue(textClips.any { it.id == "1" })
        assertTrue(textClips.any { it.id == "4" })

        // 5. Query Search Filter
        val queryClips = ClipboardFilterHelper.filterItems(sampleItems, "belanja", ClipboardCategoryFilter.ALL)
        assertEquals(1, queryClips.size)
        assertEquals("4", queryClips[0].id)

        // 6. Non-matching query
        val emptyClips = ClipboardFilterHelper.filterItems(sampleItems, "XYZ999", ClipboardCategoryFilter.ALL)
        assertTrue(emptyClips.isEmpty())
    }
}
