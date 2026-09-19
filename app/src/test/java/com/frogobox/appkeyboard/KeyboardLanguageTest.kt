package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.ui.language.KeyboardLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for KeyboardLanguage data model, metadata integrity, active resolution, and search filtering.
 */
class KeyboardLanguageTest {

    private val sampleLanguages = listOf(
        KeyboardLanguage(
            name = "English (QWERTY)",
            xml = 101,
            layoutType = "QWERTY",
            code = "EN",
            script = "Latin"
        ),
        KeyboardLanguage(
            name = "English (QWERTZ)",
            xml = 102,
            layoutType = "QWERTZ",
            code = "EN",
            script = "Latin"
        ),
        KeyboardLanguage(
            name = "English (DVORAK)",
            xml = 103,
            layoutType = "DVORAK",
            code = "EN",
            script = "Latin"
        ),
        KeyboardLanguage(
            name = "French",
            xml = 104,
            layoutType = "AZERTY",
            code = "FR",
            script = "Français (Latin)"
        ),
        KeyboardLanguage(
            name = "German",
            xml = 105,
            layoutType = "QWERTZ",
            code = "DE",
            script = "Deutsch (Latin)"
        ),
        KeyboardLanguage(
            name = "Russian",
            xml = 106,
            layoutType = "ЙЦУКЕН",
            code = "RU",
            script = "Русский (Cyrillic)"
        ),
        KeyboardLanguage(
            name = "Persian",
            xml = 107,
            layoutType = "Standard",
            code = "FA",
            script = "فارسی (Perso-Arabic)",
            isRtl = true
        )
    )

    @Test
    fun testKeyboardLanguageBackwardCompatibilityDefaults() {
        // Constructor call without new optional parameters
        val legacyLanguage = KeyboardLanguage(
            name = "Legacy Language",
            xml = 999
        )

        assertEquals("Legacy Language", legacyLanguage.name)
        assertEquals(999, legacyLanguage.xml)
        assertEquals("Standard", legacyLanguage.layoutType)
        assertEquals("EN", legacyLanguage.code)
        assertEquals("Latin", legacyLanguage.script)
        assertFalse(legacyLanguage.isRtl)
    }

    @Test
    fun testActiveLanguageResolution() {
        val activeXml = 105 // German
        val active = sampleLanguages.firstOrNull { it.xml == activeXml }

        assertNotNull(active)
        assertEquals("German", active?.name)
        assertEquals("DE", active?.code)
        assertEquals("QWERTZ", active?.layoutType)

        val nonExistentActive = sampleLanguages.firstOrNull { it.xml == 9999 }
        assertNull(nonExistentActive)
    }

    @Test
    fun testSearchFilteringByName() {
        val query = "french"
        val results = sampleLanguages.filter {
            it.name.lowercase().contains(query.lowercase()) ||
            it.code.lowercase().contains(query.lowercase()) ||
            it.layoutType.lowercase().contains(query.lowercase()) ||
            it.script.lowercase().contains(query.lowercase())
        }

        assertEquals(1, results.size)
        assertEquals("French", results[0].name)
        assertEquals("FR", results[0].code)
    }

    @Test
    fun testSearchFilteringByLayoutType() {
        val query = "QWERTZ"
        val results = sampleLanguages.filter {
            it.name.lowercase().contains(query.lowercase()) ||
            it.code.lowercase().contains(query.lowercase()) ||
            it.layoutType.lowercase().contains(query.lowercase()) ||
            it.script.lowercase().contains(query.lowercase())
        }

        // Both English (QWERTZ) and German (QWERTZ)
        assertEquals(2, results.size)
        assertTrue(results.any { it.name == "English (QWERTZ)" })
        assertTrue(results.any { it.name == "German" })
    }

    @Test
    fun testSearchFilteringByCode() {
        val query = "RU"
        val results = sampleLanguages.filter {
            it.name.lowercase().contains(query.lowercase()) ||
            it.code.lowercase().contains(query.lowercase()) ||
            it.layoutType.lowercase().contains(query.lowercase()) ||
            it.script.lowercase().contains(query.lowercase())
        }

        assertEquals(1, results.size)
        assertEquals("Russian", results[0].name)
        assertEquals("ЙЦУКЕН", results[0].layoutType)
    }

    @Test
    fun testSearchFilteringByScript() {
        val query = "cyrillic"
        val results = sampleLanguages.filter {
            it.name.lowercase().contains(query.lowercase()) ||
            it.code.lowercase().contains(query.lowercase()) ||
            it.layoutType.lowercase().contains(query.lowercase()) ||
            it.script.lowercase().contains(query.lowercase())
        }

        assertEquals(1, results.size)
        assertEquals("Russian", results[0].name)
    }

    @Test
    fun testSearchFilteringNoResults() {
        val query = "NonExistentLanguage123"
        val results = sampleLanguages.filter {
            it.name.lowercase().contains(query.lowercase()) ||
            it.code.lowercase().contains(query.lowercase()) ||
            it.layoutType.lowercase().contains(query.lowercase()) ||
            it.script.lowercase().contains(query.lowercase())
        }

        assertTrue(results.isEmpty())
    }

    @Test
    fun testRtlMetadataIntegrity() {
        val persian = sampleLanguages.first { it.code == "FA" }
        assertTrue(persian.isRtl)
        assertEquals("Standard", persian.layoutType)

        val english = sampleLanguages.first { it.code == "EN" && it.layoutType == "QWERTY" }
        assertFalse(english.isRtl)
    }
}
