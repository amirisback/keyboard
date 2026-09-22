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

    @Test
    fun testThemeIsDarkCategorization() {
        val mappedThemes = KeyboardThemeType.entries.map { it.mapToModel() }
        assertEquals(11, mappedThemes.size)

        // Light themes must have isDark == false
        val defaultTheme = mappedThemes.first { it.name == "Default" }
        assertFalse("Default theme should not be classified as dark", defaultTheme.isDark)

        val yellowTheme = mappedThemes.first { it.name == "Amber Gold" }
        assertFalse("Amber Gold theme should not be classified as dark", yellowTheme.isDark)

        // Dark themes must have isDark == true
        val darkThemeNames = listOf(
            "Frogo Purple",
            "Midnight AMOLED",
            "Ocean Blue",
            "Forest Emerald",
            "Crimson Sunset",
            "Sunset Orange",
            "Nordic Cyan",
            "Sakura Pink",
            "Wallpaper"
        )

        darkThemeNames.forEach { name ->
            val theme = mappedThemes.first { it.name == name }
            assertTrue("Theme $name should be classified as dark", theme.isDark)
        }
    }

    @Test
    fun testThemeContrastRatioCalculations() {
        // WCAG relative luminance calculation
        fun sRgbToLinear(c: Int): Double {
            val v = c / 255.0
            return if (v <= 0.03928) v / 12.92 else Math.pow((v + 0.055) / 1.055, 2.4)
        }

        fun relativeLuminance(r: Int, g: Int, b: Int): Double {
            return 0.2126 * sRgbToLinear(r) + 0.7152 * sRgbToLinear(g) + 0.0722 * sRgbToLinear(b)
        }

        fun contrastRatio(lum1: Double, lum2: Double): Double {
            val lighter = maxOf(lum1, lum2)
            val darker = minOf(lum1, lum2)
            return (lighter + 0.05) / (darker + 0.05)
        }

        // Color definitions
        val darkTextLum = relativeLuminance(15, 23, 42) // #0F172A (Slate 900)
        val whiteTextLum = relativeLuminance(255, 255, 255) // #FFFFFF

        // Light themes with dark text
        val defaultBgLum = relativeLuminance(252, 252, 255) // #FCFCFF
        val yellowBgLum = relativeLuminance(251, 192, 45) // #FBC02D (Amber Gold)

        val defaultContrast = contrastRatio(defaultBgLum, darkTextLum)
        val yellowContrast = contrastRatio(yellowBgLum, darkTextLum)

        assertTrue(
            "Default theme contrast with dark text ($defaultContrast) must exceed WCAG AA minimum 4.5",
            defaultContrast >= 4.5
        )
        assertTrue(
            "Amber Gold theme contrast with dark text ($yellowContrast) must exceed WCAG AA minimum 4.5",
            yellowContrast >= 4.5
        )

        // Dark themes with white text
        val amoledBgLum = relativeLuminance(18, 18, 18) // #121212
        val purpleBgLum = relativeLuminance(98, 0, 238) // #6200EE
        val blueBgLum = relativeLuminance(25, 118, 210) // #1976D2

        val amoledContrast = contrastRatio(whiteTextLum, amoledBgLum)
        val purpleContrast = contrastRatio(whiteTextLum, purpleBgLum)
        val blueContrast = contrastRatio(whiteTextLum, blueBgLum)

        assertTrue("AMOLED contrast ($amoledContrast) must exceed 4.5", amoledContrast >= 4.5)
        assertTrue("Purple contrast ($purpleContrast) must exceed 4.5", purpleContrast >= 4.5)
        assertTrue("Blue contrast ($blueContrast) must exceed 4.5", blueContrast >= 4.5)
    }
}
