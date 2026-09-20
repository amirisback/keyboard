package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.ui.language.KeyboardLanguage
import com.frogobox.libkeyboard.common.sound.MechanicalSoundType
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for Mechanical Sound Switch Profiles (Mechvibes)
 * and Programmer / Coding Keyboard (Codeboard style).
 */
class MechanicalSoundAndProgrammerKeyboardTest {

    @Test
    fun testMechanicalSoundTypeResolution() {
        assertEquals(MechanicalSoundType.CHERRY_MX_BLUE, MechanicalSoundType.fromId("cherry_mx_blue"))
        assertEquals(MechanicalSoundType.CHERRY_MX_BROWN, MechanicalSoundType.fromId("cherry_mx_brown"))
        assertEquals(MechanicalSoundType.CHERRY_MX_RED, MechanicalSoundType.fromId("cherry_mx_red"))
        assertEquals(MechanicalSoundType.TYPEWRITER, MechanicalSoundType.fromId("typewriter"))
        assertEquals(MechanicalSoundType.SYSTEM_CLICK, MechanicalSoundType.fromId("system"))
        assertEquals(MechanicalSoundType.OFF, MechanicalSoundType.fromId("off"))

        // Case insensitivity
        assertEquals(MechanicalSoundType.CHERRY_MX_BLUE, MechanicalSoundType.fromId("CHERRY_MX_BLUE"))
        assertEquals(MechanicalSoundType.TYPEWRITER, MechanicalSoundType.fromId("Typewriter"))

        // Fallback for null or unknown id
        assertEquals(MechanicalSoundType.CHERRY_MX_BLUE, MechanicalSoundType.fromId(null))
        assertEquals(MechanicalSoundType.CHERRY_MX_BLUE, MechanicalSoundType.fromId("unknown_switch"))
    }

    @Test
    fun testMechanicalSoundTypeResources() {
        val mechanicalProfiles = listOf(
            MechanicalSoundType.CHERRY_MX_BLUE,
            MechanicalSoundType.CHERRY_MX_BROWN,
            MechanicalSoundType.CHERRY_MX_RED,
            MechanicalSoundType.TYPEWRITER
        )

        for (profile in mechanicalProfiles) {
            assertTrue("Raw resource ID should be valid for $profile", profile.rawResId > 0)
            assertTrue("Title should not be empty for $profile", profile.title.isNotBlank())
            assertTrue("Description should not be empty for $profile", profile.description.isNotBlank())
        }

        assertEquals(0, MechanicalSoundType.OFF.rawResId)
        assertEquals(-1, MechanicalSoundType.SYSTEM_CLICK.rawResId)
    }

    @Test
    fun testProgrammerKeycodes() {
        assertEquals(-7, ItemMainKeyboard.KEYCODE_TAB)
        assertEquals(-8, ItemMainKeyboard.KEYCODE_ARROW_LEFT)
        assertEquals(-9, ItemMainKeyboard.KEYCODE_ARROW_RIGHT)
        assertEquals(-10, ItemMainKeyboard.KEYCODE_ARROW_UP)
        assertEquals(-11, ItemMainKeyboard.KEYCODE_ARROW_DOWN)

        // Ensure keycodes are unique and negative
        val keycodes = listOf(
            ItemMainKeyboard.KEYCODE_SHIFT,
            ItemMainKeyboard.KEYCODE_MODE_CHANGE,
            ItemMainKeyboard.KEYCODE_ENTER,
            ItemMainKeyboard.KEYCODE_DELETE,
            ItemMainKeyboard.KEYCODE_EMOJI,
            ItemMainKeyboard.KEYCODE_TAB,
            ItemMainKeyboard.KEYCODE_ARROW_LEFT,
            ItemMainKeyboard.KEYCODE_ARROW_RIGHT,
            ItemMainKeyboard.KEYCODE_ARROW_UP,
            ItemMainKeyboard.KEYCODE_ARROW_DOWN
        )

        assertEquals(keycodes.size, keycodes.toSet().size)
        assertTrue(keycodes.all { it < 0 })
    }

    @Test
    fun testProgrammerKeyboardMetadataAndSearch() {
        val programmerKeyboard = KeyboardLanguage(
            name = "Programmer / Coding (QWERTY)",
            xml = com.frogobox.libkeyboard.R.xml.keys_letters_programmer,
            layoutType = "Codeboard",
            code = "DEV",
            script = "Code & Symbols"
        )

        assertEquals("Programmer / Coding (QWERTY)", programmerKeyboard.name)
        assertEquals("Codeboard", programmerKeyboard.layoutType)
        assertEquals("DEV", programmerKeyboard.code)
        assertEquals("Code & Symbols", programmerKeyboard.script)

        val languages = listOf(
            KeyboardLanguage("English (QWERTY)", 1, "QWERTY", "EN", "Latin"),
            programmerKeyboard
        )

        // Search by 'programmer'
        val searchProgrammer = languages.filter { it.name.lowercase().contains("programmer") }
        assertEquals(1, searchProgrammer.size)
        assertEquals("DEV", searchProgrammer[0].code)

        // Search by 'codeboard'
        val searchCodeboard = languages.filter { it.layoutType.lowercase().contains("codeboard") }
        assertEquals(1, searchCodeboard.size)

        // Search by 'dev'
        val searchDev = languages.filter { it.code.lowercase().contains("dev") }
        assertEquals(1, searchDev.size)
    }

    @Test
    fun testVolumeClamping() {
        fun clampVolume(rawVolumePercent: Int): Float {
            return (rawVolumePercent / 100f).coerceIn(0.05f, 1.0f)
        }

        assertEquals(0.8f, clampVolume(80), 0.001f)
        assertEquals(0.05f, clampVolume(0), 0.001f)
        assertEquals(0.05f, clampVolume(-20), 0.001f)
        assertEquals(1.0f, clampVolume(100), 0.001f)
        assertEquals(1.0f, clampVolume(150), 0.001f)
    }
}
