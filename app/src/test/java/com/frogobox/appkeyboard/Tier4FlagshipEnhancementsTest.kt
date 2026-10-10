package com.frogobox.appkeyboard

import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.ClipboardItem
import com.frogobox.appkeyboard.model.KeyboardThemeType
import com.frogobox.appkeyboard.util.ClipboardSecurityGuard
import com.frogobox.appkeyboard.util.HapticFeedbackHelper
import com.frogobox.appkeyboard.util.HardwareKeyboardHelper
import com.frogobox.appkeyboard.util.InlineTextExpanderHelper
import com.frogobox.appkeyboard.util.KeyboardAccessibilityHelper
import com.frogobox.appkeyboard.util.SmartPunctuationHelper
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Comprehensive Unit Test Suite verifying Tier 4 Flagship Keyboard Enhancements (TASK-030):
 * 1. Smart Punctuation (Double-Space Period & Auto-Capitalization)
 * 2. Inline Text Expander (Teks Pintas in Candidate Strip)
 * 3. Clipboard Security Guard & Sensitive OTP Hygiene
 * 4. Hardware/Bluetooth Keyboard Interoperability
 * 5. TalkBack Phonetic Accessibility
 * 6. High-Contrast WCAG AAA Theme Tokens
 * 7. Haptic Intensity Profiles
 */
class Tier4FlagshipEnhancementsTest {

    // =========================================================================
    // 1. SMART PUNCTUATION TESTS
    // =========================================================================

    @Test
    fun testDoubleSpacePeriod_withinTimeout_triggersReplacementAndAutoShift() {
        val now = 10_000L
        val lastSpaceTime = 9_700L // 300ms elapsed (< 500ms threshold)
        val textBefore = "Halo "

        val result = SmartPunctuationHelper.checkDoubleSpacePeriod(
            lastSpaceTime = lastSpaceTime,
            currentTime = now,
            textBeforeCursor = textBefore
        )

        assertTrue("Double space should be handled", result.isHandled)
        assertEquals(1, result.textToDeleteLength)
        assertEquals(". ", result.replacementText)
        assertTrue(result.shouldAutoShift)
    }

    @Test
    fun testDoubleSpacePeriod_exceedingTimeout_isNotHandled() {
        val now = 10_000L
        val lastSpaceTime = 9_200L // 800ms elapsed (> 500ms threshold)
        val textBefore = "Halo "

        val result = SmartPunctuationHelper.checkDoubleSpacePeriod(
            lastSpaceTime = lastSpaceTime,
            currentTime = now,
            textBeforeCursor = textBefore
        )

        assertFalse("Double space exceeding timeout should not trigger", result.isHandled)
    }

    @Test
    fun testDoubleSpacePeriod_alreadyFollowsPeriod_doesNotDuplicate() {
        val now = 10_000L
        val lastSpaceTime = 9_800L
        val textBefore = "Halo. " // Already has period and space

        val result = SmartPunctuationHelper.checkDoubleSpacePeriod(
            lastSpaceTime = lastSpaceTime,
            currentTime = now,
            textBeforeCursor = textBefore
        )

        assertFalse("Should not add another period if already punctuated", result.isHandled)
    }

    @Test
    fun testShouldAutoCapitalize_sentenceBoundaries() {
        // Empty / start of input
        assertTrue(SmartPunctuationHelper.shouldAutoCapitalize(""))
        assertTrue(SmartPunctuationHelper.shouldAutoCapitalize(null))

        // After newline
        assertTrue(SmartPunctuationHelper.shouldAutoCapitalize("Baris pertama\n"))

        // After period and space
        assertTrue(SmartPunctuationHelper.shouldAutoCapitalize("Selamat pagi. "))

        // After question mark and space
        assertTrue(SmartPunctuationHelper.shouldAutoCapitalize("Apa kabar? "))

        // After exclamation mark and space
        assertTrue(SmartPunctuationHelper.shouldAutoCapitalize("Luar biasa! "))

        // Mid-sentence should NOT auto capitalize
        assertFalse(SmartPunctuationHelper.shouldAutoCapitalize("Sedang mengetik kata "))
        assertFalse(SmartPunctuationHelper.shouldAutoCapitalize("huruf"))
    }

    // =========================================================================
    // 2. INLINE TEXT EXPANDER TESTS
    // =========================================================================

    @Test
    fun testInlineTextExpander_builtInShortcuts() {
        val matchOtw = InlineTextExpanderHelper.findExpansion("otw")
        assertNotNull(matchOtw)
        assertEquals("On the way!", matchOtw?.expandedText)

        val matchBrb = InlineTextExpanderHelper.findExpansion("BRB")
        assertNotNull(matchBrb)
        assertEquals("Be right back", matchBrb?.expandedText)

        val matchAlamat = InlineTextExpanderHelper.findExpansion("!alamat")
        assertNotNull(matchAlamat)
        assertTrue(matchAlamat?.expandedText?.contains("Sudirman") == true)
    }

    @Test
    fun testInlineTextExpander_customAutoTextPriority() {
        val customList = listOf(
            AutoTextEntity(id = 1, title = "otw", body = "Saya sedang meluncur ke lokasi pertemuan")
        )

        val match = InlineTextExpanderHelper.findExpansion("otw", customList)
        assertNotNull(match)
        assertEquals("Saya sedang meluncur ke lokasi pertemuan", match?.expandedText)
    }

    @Test
    fun testInlineTextExpander_unmatchedWord_returnsNull() {
        val match = InlineTextExpanderHelper.findExpansion("randomkata")
        assertNull(match)
    }

    @Test
    fun testInlineTextExpander_formatChipLabel() {
        val match = InlineTextExpanderHelper.TextExpansionMatch("otw", "On the way!", "OTW")
        val label = InlineTextExpanderHelper.formatChipLabel(match)
        assertTrue(label.contains("OTW"))
        assertTrue(label.contains("On the way!"))
    }

    // =========================================================================
    // 3. CLIPBOARD SECURITY GUARD TESTS
    // =========================================================================

    @Test
    fun testClipboardSecurityGuard_detectsSensitiveContent() {
        // Verification OTPs
        assertTrue(ClipboardSecurityGuard.isSensitiveContent("123456"))
        assertTrue(ClipboardSecurityGuard.isSensitiveContent("982301"))
        assertTrue(ClipboardSecurityGuard.isSensitiveContent("Kode OTP Anda: 7129"))

        // Credit Cards
        assertTrue(ClipboardSecurityGuard.isSensitiveContent("4532-1234-5678-9012"))

        // Token
        assertTrue(ClipboardSecurityGuard.isSensitiveContent("Bearer eyJhbGciOiJIUzI1NiJ9"))

        // Normal text should not be flagged as sensitive
        assertFalse(ClipboardSecurityGuard.isSensitiveContent("Halo semuanya apa kabar"))
        assertFalse(ClipboardSecurityGuard.isSensitiveContent("https://google.com"))
    }

    @Test
    fun testClipboardSecurityGuard_filterExpiredClips_preservesPinnedItems() {
        val now = 100_000_000L
        val expiredTimestamp = now - (20 * 60 * 1000L) // 20 minutes old (expired)
        val freshTimestamp = now - (5 * 60 * 1000L)    // 5 minutes old (valid)

        val clips = listOf(
            ClipboardItem(id = "1", text = "Old Unpinned", timestamp = expiredTimestamp, isPinned = false),
            ClipboardItem(id = "2", text = "Old But Pinned", timestamp = expiredTimestamp, isPinned = true),
            ClipboardItem(id = "3", text = "Fresh Unpinned", timestamp = freshTimestamp, isPinned = false)
        )

        val filtered = ClipboardSecurityGuard.filterExpiredClips(clips, now)

        assertEquals(2, filtered.size)
        assertTrue(filtered.any { it.id == "2" }) // Pinned is preserved
        assertTrue(filtered.any { it.id == "3" }) // Fresh is preserved
        assertFalse(filtered.any { it.id == "1" }) // Expired unpinned is purged
    }

    @Test
    fun testClipboardSecurityGuard_maskSensitiveText() {
        val maskedOtp = ClipboardSecurityGuard.maskSensitiveText("123456")
        assertEquals("****56", maskedOtp)

        val normalText = ClipboardSecurityGuard.maskSensitiveText("Catatan belanja")
        assertEquals("Catatan belanja", normalText)
    }

    // =========================================================================
    // 4. HARDWARE KEYBOARD INTEROP TESTS
    // =========================================================================

    @Test
    fun testHardwareKeyboardHelper_shortcutsAndNavigation() {
        // Supported Ctrl shortcuts
        assertTrue(HardwareKeyboardHelper.isShortcutAction(KeyEvent.KEYCODE_C, isCtrl = true))
        assertTrue(HardwareKeyboardHelper.isShortcutAction(KeyEvent.KEYCODE_V, isCtrl = true))
        assertTrue(HardwareKeyboardHelper.isShortcutAction(KeyEvent.KEYCODE_X, isCtrl = true))
        assertTrue(HardwareKeyboardHelper.isShortcutAction(KeyEvent.KEYCODE_A, isCtrl = true))
        assertTrue(HardwareKeyboardHelper.isShortcutAction(KeyEvent.KEYCODE_Z, isCtrl = true))

        // Without Ctrl, regular letters are not treated as shortcuts
        assertFalse(HardwareKeyboardHelper.isShortcutAction(KeyEvent.KEYCODE_C, isCtrl = false))
        assertFalse(HardwareKeyboardHelper.isShortcutAction(KeyEvent.KEYCODE_V, isCtrl = false))
        assertFalse(HardwareKeyboardHelper.isShortcutAction(KeyEvent.KEYCODE_ENTER, isCtrl = true))

        // Navigation keys
        assertTrue(HardwareKeyboardHelper.isNavigationKey(KeyEvent.KEYCODE_DPAD_LEFT))
        assertTrue(HardwareKeyboardHelper.isNavigationKey(KeyEvent.KEYCODE_DPAD_RIGHT))
        assertTrue(HardwareKeyboardHelper.isNavigationKey(KeyEvent.KEYCODE_FORWARD_DEL))
        assertFalse(HardwareKeyboardHelper.isNavigationKey(KeyEvent.KEYCODE_SPACE))

        // When InputConnection is null, handleHardwareKeyEvent returns false safely
        assertFalse(HardwareKeyboardHelper.handleHardwareKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_C), null))
    }

    // =========================================================================
    // 5. ACCESSIBILITY PHONETIC TESTS
    // =========================================================================

    @Test
    fun testKeyboardAccessibilityHelper_phoneticSpelling() {
        val descA = KeyboardAccessibilityHelper.getKeyDescription('a'.code)
        assertTrue(descA.contains("Alfa"))

        val descB = KeyboardAccessibilityHelper.getKeyDescription('b'.code)
        assertTrue(descB.contains("Bravo"))

        val descDelete = KeyboardAccessibilityHelper.getKeyDescription(ItemMainKeyboard.KEYCODE_DELETE)
        assertTrue(descDelete.contains("Hapus"))

        val descShift = KeyboardAccessibilityHelper.getKeyDescription(ItemMainKeyboard.KEYCODE_SHIFT, isShifted = true)
        assertTrue(descShift.contains("Shift"))
    }

    @Test
    fun testKeyboardAccessibilityHelper_modeAnnouncements() {
        val splitAnnounce = KeyboardAccessibilityHelper.getModeAnnouncement("SPLIT")
        assertTrue(splitAnnounce.contains("terbelah"))

        val incognitoAnnounce = KeyboardAccessibilityHelper.getModeAnnouncement("INCOGNITO")
        assertTrue(incognitoAnnounce.contains("penyamaran"))
    }

    // =========================================================================
    // 6. HIGH-CONTRAST THEME & HAPTIC TESTS
    // =========================================================================

    @Test
    fun testHighContrastTheme_isRegisteredAndDark() {
        val themeType = KeyboardThemeType.HIGH_CONTRAST
        assertEquals("High Contrast (WCAG AAA)", themeType.label)
        assertTrue("High contrast theme must be categorized as dark theme", themeType.isDark)
    }

    @Test
    fun testHapticFeedbackHelper_intensityProfiles() {
        assertEquals(HapticFeedbackHelper.HapticIntensity.OFF, HapticFeedbackHelper.fromString("OFF"))
        assertEquals(HapticFeedbackHelper.HapticIntensity.SUBTLE, HapticFeedbackHelper.fromString("SUBTLE"))
        assertEquals(HapticFeedbackHelper.HapticIntensity.MEDIUM, HapticFeedbackHelper.fromString("MEDIUM"))
        assertEquals(HapticFeedbackHelper.HapticIntensity.STRONG, HapticFeedbackHelper.fromString("STRONG"))
    }
}
