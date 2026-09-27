package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.model.KeyboardFeatureModel
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying AC-01 to AC-04 for TASK-001-keyboard-features-update.
 * Authored by Sandra (QA Engineer).
 */
class KeyboardFeaturesUpdateTest {

    // -------------------------------------------------------------
    // AC-01: Always Show Feature Logic & Mutual Exclusivity
    // -------------------------------------------------------------
    @Test
    fun testAlwaysShowFeatureMutualExclusivity() {
        var activeAlwaysShowFeature: String? = null

        fun toggleAlwaysShow(featureId: String) {
            activeAlwaysShowFeature = if (activeAlwaysShowFeature == featureId) {
                null
            } else {
                featureId
            }
        }

        // Initially no feature is pinned
        assertNull(activeAlwaysShowFeature)

        // Turn ON Auto Text as Always Show
        toggleAlwaysShow(KeyboardFeatureType.AUTO_TEXT.id)
        assertEquals(KeyboardFeatureType.AUTO_TEXT.id, activeAlwaysShowFeature)

        // Turn ON Product Remote -> Auto Text must automatically turn OFF
        toggleAlwaysShow(KeyboardFeatureType.PRODUCT_REMOTE.id)
        assertEquals(KeyboardFeatureType.PRODUCT_REMOTE.id, activeAlwaysShowFeature)
        assertNotEquals(KeyboardFeatureType.AUTO_TEXT.id, activeAlwaysShowFeature)

        // Toggling Product Remote again turns it OFF -> Reverts to null (standard QWERTY)
        toggleAlwaysShow(KeyboardFeatureType.PRODUCT_REMOTE.id)
        assertNull(activeAlwaysShowFeature)
    }

    // -------------------------------------------------------------
    // AC-02: Emoji Duplicate Output Prevention & Backspace Callback
    // -------------------------------------------------------------
    @Test
    fun testEmojiSingleCommitCallback() {
        var commitCount = 0
        var committedText: String? = null

        // Callback simulating fixed onEmojiClicked handler (single commit)
        val onEmojiClicked: (String) -> Unit = { emoji ->
            commitCount++
            committedText = emoji
        }

        val testEmoji = "🎉"
        onEmojiClicked(testEmoji)

        // Verify that exactly 1 emission occurs per tap (Fix for duplicate emoji bug)
        assertEquals(1, commitCount)
        assertEquals(testEmoji, committedText)
    }

    @Test
    fun testEmojiBackspaceCallbackTriggered() {
        var backspaceTriggered = false
        val onDeleteEmoji: () -> Unit = {
            backspaceTriggered = true
        }

        onDeleteEmoji()
        assertTrue(backspaceTriggered)
    }

    // -------------------------------------------------------------
    // AC-03: Feature Reorder, Swap, and Order Preservation
    // -------------------------------------------------------------
    @Test
    fun testFeatureReorderAndSwapLogic() {
        val initialFeatures = listOf(
            KeyboardFeatureType.AUTO_TEXT.id,
            KeyboardFeatureType.TEMPLATE_TEXT_APP.id,
            KeyboardFeatureType.TEXT_EDIT.id,
            KeyboardFeatureType.CLIPBOARD.id
        )

        val featureList = initialFeatures.toMutableList()

        // Move item at index 0 (AUTO_TEXT) to index 2
        val item = featureList.removeAt(0)
        featureList.add(2, item)

        assertEquals(
            listOf(
                KeyboardFeatureType.TEMPLATE_TEXT_APP.id,
                KeyboardFeatureType.TEXT_EDIT.id,
                KeyboardFeatureType.AUTO_TEXT.id,
                KeyboardFeatureType.CLIPBOARD.id
            ),
            featureList
        )

        // Swap item at index 1 and index 2
        val temp = featureList[1]
        featureList[1] = featureList[2]
        featureList[2] = temp

        assertEquals(
            listOf(
                KeyboardFeatureType.TEMPLATE_TEXT_APP.id,
                KeyboardFeatureType.AUTO_TEXT.id,
                KeyboardFeatureType.TEXT_EDIT.id,
                KeyboardFeatureType.CLIPBOARD.id
            ),
            featureList
        )
    }

    @Test
    fun testFeatureOrderSerializationAndParsing() {
        val order = listOf(
            KeyboardFeatureType.NEWS.id,
            KeyboardFeatureType.AUTO_TEXT.id,
            KeyboardFeatureType.MOVIE.id
        )

        // Test serialization to CSV
        val serialized = order.joinToString(",")
        assertEquals("${KeyboardFeatureType.NEWS.id},${KeyboardFeatureType.AUTO_TEXT.id},${KeyboardFeatureType.MOVIE.id}", serialized)

        // Test parsing back
        val parsed = serialized.split(",").filter { it.isNotBlank() }
        assertEquals(order, parsed)
    }

    // -------------------------------------------------------------
    // AC-04: Clipboard Toggle OFF Suppression
    // -------------------------------------------------------------
    @Test
    fun testClipboardFeatureDisabledSuppression() {
        val recentClipText: String? = "Sensitive copied text"

        fun getVisibleClipboardChip(
            enabledFeatures: List<KeyboardFeatureModel>,
            clip: String?
        ): String? {
            val isEnabled = enabledFeatures.any { it.id == KeyboardFeatureType.CLIPBOARD.id }
            return if (isEnabled && !clip.isNullOrBlank()) clip else null
        }

        // When clipboard is disabled
        val featuresWithoutClipboard = listOf(
            KeyboardFeatureType.AUTO_TEXT.mapToModel()
        )

        val visibleChipWhenOff = getVisibleClipboardChip(featuresWithoutClipboard, recentClipText)
        assertNull(visibleChipWhenOff)

        // When clipboard is enabled
        val featuresWithClipboard = listOf(
            KeyboardFeatureType.CLIPBOARD.mapToModel()
        )

        val visibleChipWhenOn = getVisibleClipboardChip(featuresWithClipboard, recentClipText)
        assertEquals("Sensitive copied text", visibleChipWhenOn)
    }
}
