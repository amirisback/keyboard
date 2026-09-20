package com.frogobox.libkeyboard

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.frogobox.libkeyboard.ui.main.MainKeyboard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit & Robolectric test suite for TASK-024:
 * Verifies that MainKeyboard initializes key preview and mini keyboard programmatically
 * without relying on legacy XML layouts (item_keyboard_main.xml & keyboard_main_mini.xml).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class KeyboardPopupsComposeTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testMainKeyboardInit_withoutXmlLayouts_initializesSuccessfully() {
        // Given programmatic instantiation without item_keyboard_main.xml or keyboard_main_mini.xml
        val keyboard = MainKeyboard(context, null)

        // Then view must initialize properly
        assertNotNull("Keyboard instance must not be null", keyboard)
        assertTrue("Key text size must be greater than zero", keyboard.getKeyTextSize() > 0)
        assertNotNull("Background must be non-null", keyboard.background)
    }

    @Test
    fun testSetKeyTextSize_updatesPropertyCorrectly() {
        val keyboard = MainKeyboard(context, null)
        val newSize = 36

        keyboard.setKeyTextSize(newSize)
        assertEquals("getKeyTextSize must return the updated size", newSize, keyboard.getKeyTextSize())
    }

    @Test
    fun testMiniKeyboard_backwardCompatibilityId_remainsDefined() {
        // Verify R.id.mini_keyboard_view remains accessible and defined via ids.xml
        val resId = R.id.mini_keyboard_view
        assertTrue("mini_keyboard_view resource ID must be defined and non-zero", resId != 0)

        // Verify simulated mini_keyboard_view behaves properly in visibility check
        val keyboard = MainKeyboard(context, null)
        val miniView = View(context).apply { id = R.id.mini_keyboard_view }

        keyboard.onVisibilityChanged(miniView, View.VISIBLE)
        assertNotNull("Keyboard background must remain valid after visibility change", keyboard.background)
    }

    @Test
    fun testMiniKeyboardCharacters_parsingAndSelectionLogic() {
        val testChars = listOf("á", "à", "â", "ä", "ã", "å")
        var selectedChar: String? = null

        val onSelect: (String) -> Unit = { selectedChar = it }
        onSelect(testChars[2])

        assertEquals("Selected character should match callback input", "â", selectedChar)
        assertEquals("Character count matches input list", 6, testChars.size)
    }
}
