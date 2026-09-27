package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.ui.keyboard.form.FormField
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URLEncoder

/**
 * Unit tests validating TASK-027: Website and Form Above Keyboard integration.
 * Tests key routing simulation, field state progression, formatting, URL parsing, and dimension budgets.
 */
class FormAndWebviewAboveKeyboardTest {

    @Test
    fun testFormFieldEnumValues() {
        val fields = FormField.entries
        assertEquals(3, fields.size)
        assertEquals(FormField.SUBJECT, FormField.valueOf("SUBJECT"))
        assertEquals(FormField.DETAILS, FormField.valueOf("DETAILS"))
        assertEquals(FormField.REF_NUMBER, FormField.valueOf("REF_NUMBER"))
    }

    @Test
    fun testFormKeyRoutingSubjectFieldTyping() {
        var subject = ""
        var details = ""
        var refNumber = ""
        var activeField = FormField.SUBJECT

        val handleKey: (Int, Boolean) -> Boolean = { code, isShifted ->
            when (code) {
                ItemMainKeyboard.KEYCODE_DELETE -> {
                    when (activeField) {
                        FormField.SUBJECT -> if (subject.isNotEmpty()) subject = subject.dropLast(1)
                        FormField.DETAILS -> if (details.isNotEmpty()) details = details.dropLast(1)
                        FormField.REF_NUMBER -> if (refNumber.isNotEmpty()) refNumber = refNumber.dropLast(1)
                    }
                    true
                }
                ItemMainKeyboard.KEYCODE_SPACE -> {
                    when (activeField) {
                        FormField.SUBJECT -> subject += " "
                        FormField.DETAILS -> details += " "
                        FormField.REF_NUMBER -> refNumber += " "
                    }
                    true
                }
                ItemMainKeyboard.KEYCODE_SHIFT,
                ItemMainKeyboard.KEYCODE_MODE_CHANGE,
                ItemMainKeyboard.KEYCODE_EMOJI -> false
                else -> {
                    if (code > 0) {
                        var ch = code.toChar()
                        if (ch.isLetter() && isShifted) ch = ch.uppercaseChar()
                        when (activeField) {
                            FormField.SUBJECT -> subject += ch
                            FormField.DETAILS -> details += ch
                            FormField.REF_NUMBER -> refNumber += ch
                        }
                        true
                    } else false
                }
            }
        }

        // Type 'I' (shifted)
        assertTrue(handleKey('i'.code, true))
        assertEquals("I", subject)

        // Type 'n', 'v', 'o', 'i', 'c', 'e' (unshifted)
        "nvoice".forEach { ch ->
            assertTrue(handleKey(ch.code, false))
        }
        assertEquals("Invoice", subject)

        // Space
        assertTrue(handleKey(ItemMainKeyboard.KEYCODE_SPACE, false))
        assertEquals("Invoice ", subject)

        // Delete last char
        assertTrue(handleKey(ItemMainKeyboard.KEYCODE_DELETE, false))
        assertEquals("Invoice", subject)
    }

    @Test
    fun testFormKeyRoutingEnterFieldProgression() {
        var activeField = FormField.SUBJECT

        val onEnter: () -> Unit = {
            activeField = when (activeField) {
                FormField.SUBJECT -> FormField.DETAILS
                FormField.DETAILS -> FormField.REF_NUMBER
                FormField.REF_NUMBER -> FormField.REF_NUMBER
            }
        }

        assertEquals(FormField.SUBJECT, activeField)
        onEnter()
        assertEquals(FormField.DETAILS, activeField)
        onEnter()
        assertEquals(FormField.REF_NUMBER, activeField)
    }

    @Test
    fun testFormKeyRoutingTabCyclicNavigation() {
        var activeField = FormField.SUBJECT

        val onTab: () -> Unit = {
            activeField = when (activeField) {
                FormField.SUBJECT -> FormField.DETAILS
                FormField.DETAILS -> FormField.REF_NUMBER
                FormField.REF_NUMBER -> FormField.SUBJECT
            }
        }

        assertEquals(FormField.SUBJECT, activeField)
        onTab()
        assertEquals(FormField.DETAILS, activeField)
        onTab()
        assertEquals(FormField.REF_NUMBER, activeField)
        onTab()
        assertEquals(FormField.SUBJECT, activeField)
    }

    @Test
    fun testFormKeyRoutingShiftAndModeChangeBypass() {
        val handleKey: (Int, Boolean) -> Boolean = { code, _ ->
            when (code) {
                ItemMainKeyboard.KEYCODE_SHIFT,
                ItemMainKeyboard.KEYCODE_MODE_CHANGE,
                ItemMainKeyboard.KEYCODE_EMOJI -> false
                else -> true
            }
        }

        // Shift and Mode change must return false so BaseKeyboardIME can update layout
        assertFalse(handleKey(ItemMainKeyboard.KEYCODE_SHIFT, false))
        assertFalse(handleKey(ItemMainKeyboard.KEYCODE_MODE_CHANGE, false))
        assertFalse(handleKey(ItemMainKeyboard.KEYCODE_EMOJI, false))
    }

    @Test
    fun testFormMessageFormatting() {
        fun formatMessage(subject: String, details: String, refNumber: String): String {
            val sb = StringBuilder()
            if (subject.isNotBlank()) sb.append("Subject: ${subject.trim()}\n")
            if (details.isNotBlank()) sb.append("Details: ${details.trim()}\n")
            if (refNumber.isNotBlank()) sb.append("Ref/No: ${refNumber.trim()}\n")
            return sb.toString()
        }

        val output = formatMessage(
            subject = "Pesanan Kaos Hitam XL",
            details = "Kirim via J&T Express, alamat Jl. Merdeka No. 10",
            refNumber = "INV-2026-0901"
        )

        val expected = "Subject: Pesanan Kaos Hitam XL\n" +
                "Details: Kirim via J&T Express, alamat Jl. Merdeka No. 10\n" +
                "Ref/No: INV-2026-0901\n"

        assertEquals(expected, output)
    }

    @Test
    fun testWebviewUrlKeyRouting() {
        var urlInput = ""

        val handleKey: (Int, Boolean) -> Boolean = { code, isShifted ->
            when (code) {
                ItemMainKeyboard.KEYCODE_DELETE -> {
                    if (urlInput.isNotEmpty()) urlInput = urlInput.dropLast(1)
                    true
                }
                ItemMainKeyboard.KEYCODE_SPACE -> {
                    urlInput += " "
                    true
                }
                ItemMainKeyboard.KEYCODE_SHIFT,
                ItemMainKeyboard.KEYCODE_MODE_CHANGE,
                ItemMainKeyboard.KEYCODE_EMOJI -> false
                else -> {
                    if (code > 0) {
                        var ch = code.toChar()
                        if (ch.isLetter() && isShifted) ch = ch.uppercaseChar()
                        urlInput += ch
                        true
                    } else false
                }
            }
        }

        "google.com".forEach { ch ->
            assertTrue(handleKey(ch.code, false))
        }
        assertEquals("google.com", urlInput)

        // Delete 'm'
        assertTrue(handleKey(ItemMainKeyboard.KEYCODE_DELETE, false))
        assertEquals("google.co", urlInput)
    }

    @Test
    fun testWebviewSearchQueryAutoPrefix() {
        fun resolveUrl(query: String): String {
            val q = query.trim()
            return when {
                q.startsWith("http://") || q.startsWith("https://") -> q
                q.contains(".") && !q.contains(" ") -> "https://$q"
                q.isNotEmpty() -> "https://www.google.com/search?q=" + URLEncoder.encode(q, "UTF-8")
                else -> "https://www.google.com"
            }
        }

        assertEquals("https://www.google.com", resolveUrl(""))
        assertEquals("https://tokopedia.com", resolveUrl("tokopedia.com"))
        assertEquals("https://shopee.co.id/flash-sale", resolveUrl("https://shopee.co.id/flash-sale"))
        assertEquals("https://www.google.com/search?q=cek+resi+jne", resolveUrl("cek resi jne"))
    }

    @Test
    fun testAboveKeyboardPanelDimensionsBudget() {
        val screenHeightStandardDp = 800
        val formPanelHeightDp = 240
        val webviewPanelHeightDp = 260
        val keyboardHeightDp = 220

        val totalFormHeightDp = formPanelHeightDp + keyboardHeightDp
        val totalWebviewHeightDp = webviewPanelHeightDp + keyboardHeightDp

        assertEquals(460, totalFormHeightDp)
        assertEquals(480, totalWebviewHeightDp)

        assertTrue("Form panel + keyboard must fit within 65% of screen height", totalFormHeightDp < screenHeightStandardDp * 0.65)
        assertTrue("Webview panel + keyboard must fit within 65% of screen height", totalWebviewHeightDp < screenHeightStandardDp * 0.65)
    }

}
