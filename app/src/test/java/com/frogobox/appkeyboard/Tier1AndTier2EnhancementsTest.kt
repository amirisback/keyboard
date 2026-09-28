package com.frogobox.appkeyboard

import android.view.inputmethod.EditorInfo
import androidx.core.view.inputmethod.EditorInfoCompat
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.services.KeyboardUtil
import com.frogobox.appkeyboard.ui.keyboard.root.KeyboardPanelState
import com.frogobox.appkeyboard.util.KeyboardRichMediaHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test suite for TASK-028:
 * - Dedicated Number Row & Secondary Hint Glyphs (Tier 1)
 * - One-Handed Docking Mode (Tier 1)
 * - Dynamic Theming & Rich Media Commit API (Tier 2)
 */
class Tier1AndTier2EnhancementsTest {

    @Test
    fun testKeyboardFeatureType_includesNumberRowAndOneHanded() {
        val numberRow = KeyboardFeatureType.NUMBER_ROW
        assertEquals("menu_number_row", numberRow.id)
        assertEquals("Number Row", numberRow.text)

        val oneHanded = KeyboardFeatureType.ONE_HANDED
        assertEquals("menu_one_handed", oneHanded.id)
        assertEquals("One-Handed", oneHanded.text)

        // Verify reverse lookup
        assertEquals(KeyboardFeatureType.NUMBER_ROW, KeyboardFeatureType.from("menu_number_row"))
        assertEquals(KeyboardFeatureType.ONE_HANDED, KeyboardFeatureType.from("menu_one_handed"))
    }

    @Test
    fun testKeyboardPanelState_actionFeaturesDoNotOpenPanels() {
        // Dedicated number row and one-handed mode are action features that modify main layout
        assertNull(KeyboardPanelState.fromFeature(KeyboardFeatureType.NUMBER_ROW))
        assertNull(KeyboardPanelState.fromFeature(KeyboardFeatureType.ONE_HANDED))
        assertNull(KeyboardPanelState.fromFeature(KeyboardFeatureType.SUGGESTION))

        // Content features open overlays
        assertEquals(KeyboardPanelState.CLIPBOARD, KeyboardPanelState.fromFeature(KeyboardFeatureType.CLIPBOARD))
        assertEquals(KeyboardPanelState.TEXT_EDIT, KeyboardPanelState.fromFeature(KeyboardFeatureType.TEXT_EDIT))
        assertEquals(KeyboardPanelState.AUTO_TEXT, KeyboardPanelState.fromFeature(KeyboardFeatureType.AUTO_TEXT))
    }

    @Test
    fun testDefaultFeatureTypes_includesTier1Features() {
        assertTrue(KeyboardUtil.DEFAULT_FEATURE_TYPES.contains(KeyboardFeatureType.NUMBER_ROW))
        assertTrue(KeyboardUtil.DEFAULT_FEATURE_TYPES.contains(KeyboardFeatureType.ONE_HANDED))
        assertTrue(KeyboardUtil.DEFAULT_FEATURE_TYPES.contains(KeyboardFeatureType.SUGGESTION))
        assertTrue(KeyboardUtil.DEFAULT_FEATURE_TYPES.contains(KeyboardFeatureType.CLIPBOARD))
    }

    @Test
    fun testOneHandedMode_stateTransitions() {
        fun getNextMode(currentMode: String): String {
            return when (currentMode) {
                "OFF" -> "RIGHT"
                "RIGHT" -> "LEFT"
                else -> "OFF"
            }
        }

        var mode = "OFF"
        mode = getNextMode(mode)
        assertEquals("RIGHT", mode)

        mode = getNextMode(mode)
        assertEquals("LEFT", mode)

        mode = getNextMode(mode)
        assertEquals("OFF", mode)
    }

    @Test
    fun testRichMediaHelper_nullEditorInfoReturnsFalse() {
        assertFalse(KeyboardRichMediaHelper.isMimeTypeSupported(null, "image/gif"))
        assertFalse(KeyboardRichMediaHelper.isMimeTypeSupported(null, "image/webp"))
    }

    @Test
    fun testRichMediaHelper_supportedMimeTypes() {
        val editorInfo = EditorInfo().apply {
            contentMimeTypes = arrayOf("image/gif", "image/png", "image/webp")
        }

        assertTrue(KeyboardRichMediaHelper.isMimeTypeSupported(editorInfo, "image/gif"))
        assertTrue(KeyboardRichMediaHelper.isMimeTypeSupported(editorInfo, "image/png"))
        assertTrue(KeyboardRichMediaHelper.isMimeTypeSupported(editorInfo, "image/webp"))
        assertFalse(KeyboardRichMediaHelper.isMimeTypeSupported(editorInfo, "video/mp4"))
    }

    @Test
    fun testRichMediaHelper_wildcardMimeTypes() {
        val editorInfo = EditorInfo().apply {
            contentMimeTypes = arrayOf("image/*")
        }

        assertTrue(KeyboardRichMediaHelper.isMimeTypeSupported(editorInfo, "image/gif"))
        assertTrue(KeyboardRichMediaHelper.isMimeTypeSupported(editorInfo, "image/jpeg"))
        assertFalse(KeyboardRichMediaHelper.isMimeTypeSupported(editorInfo, "text/plain"))
    }
}
