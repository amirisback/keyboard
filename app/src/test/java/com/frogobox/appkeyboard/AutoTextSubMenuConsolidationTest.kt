package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.services.KeyboardUtil
import com.frogobox.appkeyboard.ui.keyboard.autotext.AutoTextCategory
import com.frogobox.sdk.delegate.preference.PreferenceDelegates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test suite validating TASK-028 AutoText & Templates Consolidation:
 * - AutoTextCategory enum structure, titles, icons, and bidirectional mappings
 * - KeyboardUtil menuToggle exclusion of standalone template features
 * - Validation of consolidated menu items in the main keyboard toolbar
 */
class AutoTextSubMenuConsolidationTest {

    @Test
    fun testAutoTextCategoryEnumValuesAndOrder() {
        val categories = AutoTextCategory.entries
        assertEquals(6, categories.size)

        assertEquals(AutoTextCategory.MY_CUSTOM, categories[0])
        assertEquals("cat_my_custom", categories[0].id)
        assertEquals("My Custom", categories[0].title)
        assertEquals("✏️", categories[0].icon)
        assertTrue(categories[0].isCustom)
        assertNull(categories[0].templateFeatureType)

        assertEquals(AutoTextCategory.GAME, categories[1])
        assertEquals("cat_game", categories[1].id)
        assertEquals("Game", categories[1].title)
        assertEquals("🎮", categories[1].icon)
        assertFalse(categories[1].isCustom)
        assertEquals(KeyboardFeatureType.TEMPLATE_TEXT_GAME, categories[1].templateFeatureType)

        assertEquals(AutoTextCategory.APP, categories[2])
        assertEquals("cat_app", categories[2].id)
        assertEquals("App", categories[2].title)
        assertEquals("📱", categories[2].icon)
        assertFalse(categories[2].isCustom)
        assertEquals(KeyboardFeatureType.TEMPLATE_TEXT_APP, categories[2].templateFeatureType)

        assertEquals(AutoTextCategory.SALE, categories[3])
        assertEquals("cat_sale", categories[3].id)
        assertEquals("Sale", categories[3].title)
        assertEquals("💰", categories[3].icon)
        assertFalse(categories[3].isCustom)
        assertEquals(KeyboardFeatureType.TEMPLATE_TEXT_SALE, categories[3].templateFeatureType)

        assertEquals(AutoTextCategory.GREETING, categories[4])
        assertEquals("cat_greeting", categories[4].id)
        assertEquals("Greeting", categories[4].title)
        assertEquals("👋", categories[4].icon)
        assertFalse(categories[4].isCustom)
        assertEquals(KeyboardFeatureType.TEMPLATE_TEXT_GREETING, categories[4].templateFeatureType)

        assertEquals(AutoTextCategory.LOVE, categories[5])
        assertEquals("cat_love", categories[5].id)
        assertEquals("Love", categories[5].title)
        assertEquals("❤️", categories[5].icon)
        assertFalse(categories[5].isCustom)
        assertEquals(KeyboardFeatureType.TEMPLATE_TEXT_LOVE, categories[5].templateFeatureType)
    }

    @Test
    fun testAutoTextCategoryFromFeatureTypeMapping() {
        assertEquals(AutoTextCategory.GAME, AutoTextCategory.fromFeatureType(KeyboardFeatureType.TEMPLATE_TEXT_GAME))
        assertEquals(AutoTextCategory.APP, AutoTextCategory.fromFeatureType(KeyboardFeatureType.TEMPLATE_TEXT_APP))
        assertEquals(AutoTextCategory.SALE, AutoTextCategory.fromFeatureType(KeyboardFeatureType.TEMPLATE_TEXT_SALE))
        assertEquals(AutoTextCategory.GREETING, AutoTextCategory.fromFeatureType(KeyboardFeatureType.TEMPLATE_TEXT_GREETING))
        assertEquals(AutoTextCategory.LOVE, AutoTextCategory.fromFeatureType(KeyboardFeatureType.TEMPLATE_TEXT_LOVE))

        // Non-template feature types should map to default MY_CUSTOM
        assertEquals(AutoTextCategory.MY_CUSTOM, AutoTextCategory.fromFeatureType(KeyboardFeatureType.AUTO_TEXT))
        assertEquals(AutoTextCategory.MY_CUSTOM, AutoTextCategory.fromFeatureType(KeyboardFeatureType.NEWS))
        assertEquals(AutoTextCategory.MY_CUSTOM, AutoTextCategory.fromFeatureType(KeyboardFeatureType.PRODUCT_REMOTE))
    }

    @Test
    fun testKeyboardUtilMenuToggleExcludesTemplates() {
        val fakePref = TestPreferenceDelegates()
        val keyboardUtil = KeyboardUtil(fakePref)
        val menuList = keyboardUtil.menuToggle()

        // 11 functional tools remain (Suggestion, AutoText, ProductRemote, Clipboard, TextEdit, News, Movie, Web, Form, ChangeKeyboard, Setting)
        assertEquals(11, menuList.size)

        val menuIds = menuList.map { it.id }.toSet()

        // Standalone template items must be absent from main menu
        assertFalse(menuIds.contains(KeyboardFeatureType.TEMPLATE_TEXT_GAME.id))
        assertFalse(menuIds.contains(KeyboardFeatureType.TEMPLATE_TEXT_APP.id))
        assertFalse(menuIds.contains(KeyboardFeatureType.TEMPLATE_TEXT_SALE.id))
        assertFalse(menuIds.contains(KeyboardFeatureType.TEMPLATE_TEXT_GREETING.id))
        assertFalse(menuIds.contains(KeyboardFeatureType.TEMPLATE_TEXT_LOVE.id))

        // Core AutoText must be present
        assertTrue(menuIds.contains(KeyboardFeatureType.AUTO_TEXT.id))
        assertTrue(menuIds.contains(KeyboardFeatureType.PRODUCT_REMOTE.id))
        assertTrue(menuIds.contains(KeyboardFeatureType.CLIPBOARD.id))
        assertTrue(menuIds.contains(KeyboardFeatureType.TEXT_EDIT.id))
        assertTrue(menuIds.contains(KeyboardFeatureType.NEWS.id))
        assertTrue(menuIds.contains(KeyboardFeatureType.MOVIE.id))
        assertTrue(menuIds.contains(KeyboardFeatureType.WEB.id))
        assertTrue(menuIds.contains(KeyboardFeatureType.FORM.id))
        assertTrue(menuIds.contains(KeyboardFeatureType.CHANGE_KEYBOARD.id))
        assertTrue(menuIds.contains(KeyboardFeatureType.SETTING.id))
        assertTrue(menuIds.contains(KeyboardFeatureType.SUGGESTION.id))
    }

    @Test
    fun testCategorySwitchingModelLogic() {
        var activeCategory = AutoTextCategory.MY_CUSTOM
        assertEquals("Tap to insert shortcut into chat", getSubtitle(activeCategory))

        activeCategory = AutoTextCategory.GAME
        assertEquals("Tap any game template to paste into chat", getSubtitle(activeCategory))

        activeCategory = AutoTextCategory.SALE
        assertEquals("Tap any sale template to paste into chat", getSubtitle(activeCategory))

        activeCategory = AutoTextCategory.GREETING
        assertEquals("Tap any greeting template to paste into chat", getSubtitle(activeCategory))

        activeCategory = AutoTextCategory.LOVE
        assertEquals("Tap any love template to paste into chat", getSubtitle(activeCategory))

        activeCategory = AutoTextCategory.APP
        assertEquals("Tap any app template to paste into chat", getSubtitle(activeCategory))
    }

    private fun getSubtitle(category: AutoTextCategory): String {
        return when (category) {
            AutoTextCategory.MY_CUSTOM -> "Tap to insert shortcut into chat"
            AutoTextCategory.GAME -> "Tap any game template to paste into chat"
            AutoTextCategory.APP -> "Tap any app template to paste into chat"
            AutoTextCategory.SALE -> "Tap any sale template to paste into chat"
            AutoTextCategory.GREETING -> "Tap any greeting template to paste into chat"
            AutoTextCategory.LOVE -> "Tap any love template to paste into chat"
        }
    }

    private class TestPreferenceDelegates : PreferenceDelegates {
        private val boolMap = mutableMapOf<String, Boolean>()

        override fun savePrefString(key: String, value: String) {}
        override fun getPrefString(key: String): String = ""
        override fun getPrefString(key: String, defaultValue: String): String = defaultValue

        override fun savePrefInt(key: String, value: Int) {}
        override fun getPrefInt(key: String): Int = 0
        override fun getPrefInt(key: String, defaultValue: Int): Int = defaultValue

        override fun savePrefBoolean(key: String, value: Boolean) { boolMap[key] = value }
        override fun getPrefBoolean(key: String): Boolean = boolMap[key] ?: true
        override fun getPrefBoolean(key: String, defaultValue: Boolean): Boolean = boolMap[key] ?: defaultValue

        override fun savePrefFloat(key: String, value: Float) {}
        override fun getPrefFloat(key: String): Float = 0f
        override fun getPrefFloat(key: String, defaultValue: Float): Float = defaultValue

        override fun savePrefLong(key: String, value: Long) {}
        override fun getPrefLong(key: String): Long = 0L
        override fun getPrefLong(key: String, defaultValue: Long): Long = defaultValue

        override fun deletePref(key: String) { boolMap.remove(key) }
        override fun nukePref() { boolMap.clear() }

        @Suppress("UNCHECKED_CAST")
        override fun <T> save(key: String, value: T) {}

        @Suppress("UNCHECKED_CAST")
        override fun <T> get(key: String, defaultValue: T): T = defaultValue
    }

}
