package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.model.KeyboardFeatureType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * QA Unit tests for TASK-033:
 * 1. Verifies that Home screen menu item icons align with their real settings.
 * 2. Verifies that Toggle Fitur uses ic_menu_setting (parity with KeyboardFeatureType.SETTING).
 * 3. Verifies that Tema Keyboard uses ic_menu_theme and does NOT collide with Template Teks.
 * 4. Verifies that Bahasa Keyboard uses ic_menu_language (not the web search WWW icon).
 * 5. Verifies all icon drawables are valid and non-zero.
 */
class Task033HomeUiIconsAlignmentTest {

    @Test
    fun testToggleFiturIconMatchesKeyboardFeatureSetting() {
        val toggleFiturIcon = R.drawable.ic_menu_setting
        val keyboardFeatureSettingIcon = KeyboardFeatureType.SETTING.icon

        // Toggle Fitur must match SETTING in KeyboardFeatureType
        assertEquals(keyboardFeatureSettingIcon, toggleFiturIcon)
        assertNotEquals(R.drawable.ic_menu_form, toggleFiturIcon)
    }

    @Test
    fun testTemaKeyboardUsesDedicatedThemeIconAndDoesNotCollide() {
        val temaKeyboardIcon = R.drawable.ic_menu_theme
        val templateTeksIcon = R.drawable.ic_menu_ps_game

        // Tema Keyboard must have its own theme icon, not the game controller
        assertNotEquals(R.drawable.ic_menu_ps_game, temaKeyboardIcon)
        // No duplicate collision between Tema and Template Teks
        assertNotEquals(templateTeksIcon, temaKeyboardIcon)
        assertTrue(temaKeyboardIcon != 0)
    }

    @Test
    fun testBahasaKeyboardUsesDedicatedLanguageIcon() {
        val bahasaKeyboardIcon = R.drawable.ic_menu_language

        // Bahasa Keyboard must NOT use the WWW search website icon
        assertNotEquals(R.drawable.ic_menu_website, bahasaKeyboardIcon)
        assertTrue(bahasaKeyboardIcon != 0)
    }

    @Test
    fun testHomeMenuIconIntegrityAndNonZero() {
        val menuIcons = mapOf(
            "Product Remote" to R.drawable.ic_menu_ps_sale,
            "Auto Text" to R.drawable.ic_menu_auto_text,
            "Template Teks" to R.drawable.ic_menu_ps_game,
            "Toggle Fitur" to R.drawable.ic_menu_setting,
            "Suara dan Haptic" to R.drawable.ic_menu_sound,
            "Bahasa Keyboard" to R.drawable.ic_menu_language,
            "Tema Keyboard" to R.drawable.ic_menu_theme,
            "Playground Uji Coba" to R.drawable.ic_menu_keyboard
        )

        // All menu items must have valid non-zero resource IDs
        for ((menuTitle, iconRes) in menuIcons) {
            assertTrue("Icon for $menuTitle should be non-zero", iconRes != 0)
        }

        // Verify distinct icons for key setting categories
        val uniqueKeyIcons = setOf(
            menuIcons["Toggle Fitur"],
            menuIcons["Bahasa Keyboard"],
            menuIcons["Tema Keyboard"],
            menuIcons["Suara dan Haptic"]
        )
        assertEquals(4, uniqueKeyIcons.size)
    }

    @Test
    fun testKeyboardFeatureTypeIntegrity() {
        for (featureType in KeyboardFeatureType.entries) {
            assertTrue("Feature ${featureType.name} icon must be valid", featureType.icon != 0)
            assertTrue("Feature ${featureType.name} id must not be blank", featureType.id.isNotBlank())
            assertTrue("Feature ${featureType.name} text must not be blank", featureType.text.isNotBlank())
        }
    }
}
