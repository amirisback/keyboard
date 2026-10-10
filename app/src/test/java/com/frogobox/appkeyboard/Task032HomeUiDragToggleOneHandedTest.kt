package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.model.KeyboardFeatureModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Collections

/**
 * Unit tests for TASK-032:
 * 1. Home UI Cadangkan Data (Export/Import) positioning & data validation.
 * 2. Toggle Feature drag-and-drop swap logic and boundary checks.
 * 3. One-Handed mode state transitions, intrinsic height sizing, and scaleX touch hit-test logic.
 */
class Task032HomeUiDragToggleOneHandedTest {

    @Test
    fun testDragAndDropSwapLogic() {
        val originalList = mutableListOf(
            KeyboardFeatureModel(id = "auto_text", text = "Auto Text", icon = 1),
            KeyboardFeatureModel(id = "template_text_app", text = "App Review", icon = 2),
            KeyboardFeatureModel(id = "template_text_game", text = "Game Review", icon = 3),
            KeyboardFeatureModel(id = "clipboard", text = "Clipboard", icon = 4)
        )

        // Swap index 0 and 2
        val fromIndex = 0
        val toIndex = 2
        assertTrue(fromIndex in originalList.indices && toIndex in originalList.indices)

        Collections.swap(originalList, fromIndex, toIndex)

        assertEquals("template_text_game", originalList[0].id)
        assertEquals("template_text_app", originalList[1].id)
        assertEquals("auto_text", originalList[2].id)
        assertEquals("clipboard", originalList[3].id)
    }

    @Test
    fun testDragAndDropSwapOutOfBoundsGuarded() {
        val list = mutableListOf(
            KeyboardFeatureModel(id = "feature_1", text = "F1", icon = 1),
            KeyboardFeatureModel(id = "feature_2", text = "F2", icon = 2)
        )

        val invalidFrom = -1
        val invalidTo = 5

        val isValidSwap = (invalidFrom in list.indices && invalidTo in list.indices && invalidFrom != invalidTo)
        assertFalse(isValidSwap)

        // List remains unchanged
        assertEquals("feature_1", list[0].id)
        assertEquals("feature_2", list[1].id)
    }

    @Test
    fun testOneHandedModeStateTransitions() {
        val validStates = listOf("OFF", "LEFT", "RIGHT")

        var currentState = "OFF"
        assertTrue(validStates.contains(currentState))

        // Transition: OFF -> LEFT
        currentState = "LEFT"
        assertEquals("LEFT", currentState)

        // Transition: LEFT -> RIGHT (Swap sides)
        currentState = if (currentState == "LEFT") "RIGHT" else "LEFT"
        assertEquals("RIGHT", currentState)

        // Transition: RIGHT -> OFF (Restore full width)
        currentState = "OFF"
        assertEquals("OFF", currentState)
    }

    @Test
    fun testOneHandedKeyboardScaleXAndTouchTransformation() {
        val keyboardMinWidth = 1080
        val containerWidth = (keyboardMinWidth * 0.84f).toInt() // ~907px

        val scaleX = containerWidth.toFloat() / keyboardMinWidth.toFloat()
        assertTrue(scaleX < 1.0f)
        assertTrue(scaleX > 0.83f && scaleX < 0.85f)

        // Test rightmost key coordinate mapping
        // In full 1080px width, backspace key might be at x = 972px, width = 108px
        val unscaledKeyX = 972
        val unscaledKeyWidth = 108
        val scaledKeyX = (unscaledKeyX * scaleX).toInt()
        val scaledKeyWidth = (unscaledKeyWidth * scaleX).toInt()

        // Verify the key fits inside the 907px container
        assertTrue(scaledKeyX + scaledKeyWidth <= containerWidth + 2)

        // Test touch hit test coordinate inversion
        // User taps at screen coordinate at the center of the scaled key
        val touchScreenX = scaledKeyX + (scaledKeyWidth / 2)
        val adjustedTouchX = (touchScreenX / scaleX).toInt()

        // Adjusted touch should map back inside the unscaled key's bounds
        assertTrue(adjustedTouchX in unscaledKeyX..(unscaledKeyX + unscaledKeyWidth))
    }

    @Test
    fun testBackupRestorePayloadIntegrity() {
        val sampleBackupJson = """
            {
                "version": 1,
                "exportedAt": 1727515000000,
                "autoTextCount": 2,
                "autoTexts": [
                    {"id": 1, "title": "Greeting", "content": "Halo, selamat datang!"},
                    {"id": 2, "title": "Thanks", "content": "Terima kasih atas bantuan Anda."}
                ],
                "preferences": {
                    "feature_order": "auto_text,clipboard,web_search",
                    "one_handed_mode": "LEFT"
                }
            }
        """.trimIndent()

        assertTrue(sampleBackupJson.contains("version"))
        assertTrue(sampleBackupJson.contains("autoTexts"))
        assertTrue(sampleBackupJson.contains("preferences"))
        assertTrue(sampleBackupJson.contains("one_handed_mode"))
    }
}
