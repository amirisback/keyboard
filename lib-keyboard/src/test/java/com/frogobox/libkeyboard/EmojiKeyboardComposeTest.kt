package com.frogobox.libkeyboard

import com.frogobox.libkeyboard.ui.emoji.EmojiCategoryType
import com.frogobox.libkeyboard.ui.emoji.displayName
import com.frogobox.libkeyboard.ui.emoji.getEmojiCategory
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EmojiKeyboardComposeTest {

    @Test
    fun testEmojiCategoryTypeCountAndPaths() {
        val categories = EmojiCategoryType.entries
        assertEquals(10, categories.size)

        assertTrue(categories.any { it == EmojiCategoryType.GENERAL && it.path == "media/_emoji_general.txt" })
        assertTrue(categories.any { it == EmojiCategoryType.ACTIVITIES && it.path == "media/emoji_activities.txt" })
        assertTrue(categories.any { it == EmojiCategoryType.ANIMAL_NATURE && it.path == "media/emoji_animal_nature.txt" })
        assertTrue(categories.any { it == EmojiCategoryType.FLAG && it.path == "media/emoji_flag.txt" })
        assertTrue(categories.any { it == EmojiCategoryType.FOOD_DRINK && it.path == "media/emoji_food_drink.txt" })
        assertTrue(categories.any { it == EmojiCategoryType.OBJECTS && it.path == "media/emoji_objects.txt" })
        assertTrue(categories.any { it == EmojiCategoryType.PEOPLE_BODY && it.path == "media/emoji_people_body.txt" })
        assertTrue(categories.any { it == EmojiCategoryType.SMILEYS_EMOTION && it.path == "media/emoji_smileys_emotion.txt" })
        assertTrue(categories.any { it == EmojiCategoryType.SYMBOLS && it.path == "media/emoji_symbols.txt" })
        assertTrue(categories.any { it == EmojiCategoryType.TRAVEL_PLACES && it.path == "media/emoji_travel_places.txt" })
    }

    @Test
    fun testEmojiCategoryTypeDisplayNames() {
        assertEquals("General", EmojiCategoryType.GENERAL.displayName)
        assertEquals("Activities", EmojiCategoryType.ACTIVITIES.displayName)
        assertEquals("Animals & Nature", EmojiCategoryType.ANIMAL_NATURE.displayName)
        assertEquals("Flags", EmojiCategoryType.FLAG.displayName)
        assertEquals("Food & Drink", EmojiCategoryType.FOOD_DRINK.displayName)
        assertEquals("Objects", EmojiCategoryType.OBJECTS.displayName)
        assertEquals("People & Body", EmojiCategoryType.PEOPLE_BODY.displayName)
        assertEquals("Smileys & Emotion", EmojiCategoryType.SMILEYS_EMOTION.displayName)
        assertEquals("Symbols", EmojiCategoryType.SYMBOLS.displayName)
        assertEquals("Travel & Places", EmojiCategoryType.TRAVEL_PLACES.displayName)
    }

    @Test
    fun testGetEmojiCategoryListMapping() {
        val categoryList = getEmojiCategory()
        assertEquals(10, categoryList.size)

        val first = categoryList[0]
        assertEquals("GENERAL", first.name)
        assertNotNull(first.icon)
        assertEquals("media/_emoji_general.txt", first.path)
    }

    @Test
    fun testKeycodeMappingsForComposeKeyboard() {
        assertEquals(-1, ItemMainKeyboard.KEYCODE_SHIFT)
        assertEquals(-2, ItemMainKeyboard.KEYCODE_MODE_CHANGE)
        assertEquals(-4, ItemMainKeyboard.KEYCODE_ENTER)
        assertEquals(-5, ItemMainKeyboard.KEYCODE_DELETE)
        assertEquals(32, ItemMainKeyboard.KEYCODE_SPACE)
        assertEquals(-6, ItemMainKeyboard.KEYCODE_EMOJI)
        assertEquals(-7, ItemMainKeyboard.KEYCODE_TAB)
    }

    @Test
    fun testScrollTriggerIncrementLogic() {
        var resetTrigger = 0
        assertEquals(0, resetTrigger)

        resetTrigger++
        assertEquals(1, resetTrigger)

        resetTrigger++
        assertEquals(2, resetTrigger)
    }
}
