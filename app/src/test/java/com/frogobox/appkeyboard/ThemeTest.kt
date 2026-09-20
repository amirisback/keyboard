package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.model.KeyboardThemeModel
import com.frogobox.appkeyboard.model.KeyboardThemeType
import com.frogobox.appkeyboard.model.ThemeType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test for Keyboard Theme models and theme logic verification.
 */
class ThemeTest {

    @Test
    fun testKeyboardThemeTypeMapping() {
        val themes = KeyboardThemeType.entries.map { it.mapToModel() }
        assertEquals(11, themes.size)

        val defaultTheme = themes.first { it.name == "Default" }
        assertEquals(ThemeType.COLOR, defaultTheme.themType)
        assertTrue(defaultTheme.background != 0)

        val purpleTheme = themes.first { it.name == "Frogo Purple" }
        assertEquals(ThemeType.COLOR, purpleTheme.themType)
        assertTrue(purpleTheme.background != 0)

        val imageTheme = themes.first { it.name == "Wallpaper" }
        assertEquals(ThemeType.IMAGE, imageTheme.themType)
        assertTrue(imageTheme.background != 0)
    }

    @Test
    fun testKeyboardThemeTypeFrom() {
        assertEquals(KeyboardThemeType.DEFAULT, KeyboardThemeType from "DEFAULT")
        assertEquals(KeyboardThemeType.PURPLE, KeyboardThemeType from "PURPLE")
        assertEquals(KeyboardThemeType.DARK, KeyboardThemeType from "DARK")
        assertEquals(KeyboardThemeType.BLUE, KeyboardThemeType from "BLUE")
        assertEquals(KeyboardThemeType.GREEN, KeyboardThemeType from "GREEN")
        assertEquals(KeyboardThemeType.RED, KeyboardThemeType from "RED")
        assertEquals(KeyboardThemeType.ORANGE, KeyboardThemeType from "ORANGE")
        assertEquals(KeyboardThemeType.CYAN, KeyboardThemeType from "CYAN")
        assertEquals(KeyboardThemeType.PINK, KeyboardThemeType from "PINK")
        assertEquals(KeyboardThemeType.YELLOW, KeyboardThemeType from "YELLOW")
        assertEquals(KeyboardThemeType.IMAGE_BG_DARK, KeyboardThemeType from "IMAGE_BG_DARK")
        // Unknown fallback to DEFAULT
        assertEquals(KeyboardThemeType.DEFAULT, KeyboardThemeType from "UNKNOWN_THEME")
    }

    @Test
    fun testThemeActiveMatchingLogic() {
        val colorTheme = KeyboardThemeModel(
            name = "Default",
            description = "Default Color",
            themType = ThemeType.COLOR,
            background = 1001
        )
        val imageTheme = KeyboardThemeModel(
            name = "Image",
            description = "Sample Wallpaper",
            themType = ThemeType.IMAGE,
            background = 1001 // simulate identical integer resource ID
        )

        // Matching function considering both background and type
        fun isThemeActive(
            theme: KeyboardThemeModel,
            activeColor: Int,
            activeType: String
        ): Boolean {
            return theme.background == activeColor && theme.themType.name == activeType
        }

        // When COLOR is active
        assertTrue(isThemeActive(colorTheme, 1001, ThemeType.COLOR.name))
        assertFalse(isThemeActive(imageTheme, 1001, ThemeType.COLOR.name))

        // When IMAGE is active
        assertFalse(isThemeActive(colorTheme, 1001, ThemeType.IMAGE.name))
        assertTrue(isThemeActive(imageTheme, 1001, ThemeType.IMAGE.name))
    }

    @Test
    fun testSafeThemeTypeParsing() {
        val safeParse = { typeStr: String ->
            runCatching { ThemeType.valueOf(typeStr) }.getOrDefault(ThemeType.COLOR)
        }

        assertEquals(ThemeType.COLOR, safeParse("COLOR"))
        assertEquals(ThemeType.IMAGE, safeParse("IMAGE"))
        assertEquals(ThemeType.COLOR, safeParse("CORRUPTED_VALUE"))
        assertEquals(ThemeType.COLOR, safeParse(""))
    }
}
