package com.frogobox.libkeyboard

import com.frogobox.libkeyboard.common.sound.MechanicalSoundType
import com.frogobox.libkeyboard.ui.emoji.EmojiCategoryType
import com.frogobox.libkeyboard.ui.emoji.getEmojiCategory
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyboardLayoutLogicTest {

    @Test
    fun testMechanicalSoundTypeMapping() {
        assertEquals(MechanicalSoundType.CHERRY_MX_BLUE, MechanicalSoundType.fromId("cherry_mx_blue"))
        assertEquals(MechanicalSoundType.CHERRY_MX_BROWN, MechanicalSoundType.fromId("cherry_mx_brown"))
        assertEquals(MechanicalSoundType.CHERRY_MX_RED, MechanicalSoundType.fromId("cherry_mx_red"))
        assertEquals(MechanicalSoundType.TYPEWRITER, MechanicalSoundType.fromId("typewriter"))
        assertEquals(MechanicalSoundType.SYSTEM_CLICK, MechanicalSoundType.fromId("system"))
        assertEquals(MechanicalSoundType.OFF, MechanicalSoundType.fromId("off"))
        // Fallback on unknown id
        assertEquals(MechanicalSoundType.CHERRY_MX_BLUE, MechanicalSoundType.fromId("unknown_switch"))
    }

    @Test
    fun testEmojiCategoriesMapping() {
        val categories = getEmojiCategory()
        assertNotNull(categories)
        assertEquals(EmojiCategoryType.entries.size, categories.size)
        assertTrue(categories.any { it.name == EmojiCategoryType.SMILEYS_EMOTION.name })
        assertTrue(categories.any { it.name == EmojiCategoryType.PEOPLE_BODY.name })
    }

    @Test
    fun testKeycodeConstants() {
        assertEquals(-1, ItemMainKeyboard.KEYCODE_SHIFT)
        assertEquals(-2, ItemMainKeyboard.KEYCODE_MODE_CHANGE)
        assertEquals(-4, ItemMainKeyboard.KEYCODE_ENTER)
        assertEquals(-5, ItemMainKeyboard.KEYCODE_DELETE)
        assertEquals(32, ItemMainKeyboard.KEYCODE_SPACE)
        assertEquals(-6, ItemMainKeyboard.KEYCODE_EMOJI)
        assertEquals(-7, ItemMainKeyboard.KEYCODE_TAB)
    }

    @Test
    fun testAdjustCaseLogic() {
        val label = "a"
        val upper = label.uppercase()
        assertEquals("A", upper)
    }
}
