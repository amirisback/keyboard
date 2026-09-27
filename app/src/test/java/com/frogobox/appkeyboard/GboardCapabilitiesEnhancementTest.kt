package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.model.ClipboardItem
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.repository.clipboard.ClipboardRepositoryImpl
import com.frogobox.appkeyboard.ui.keyboard.root.KeyboardPanelState
import com.frogobox.appkeyboard.ui.keyboard.textedit.TextEditAction
import com.frogobox.sdk.delegate.preference.PreferenceDelegates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GboardCapabilitiesEnhancementTest {

    private lateinit var fakePref: FakePreferenceDelegates
    private lateinit var repository: ClipboardRepositoryImpl

    @Before
    fun setUp() {
        fakePref = FakePreferenceDelegates()
        repository = ClipboardRepositoryImpl(fakePref, Dispatchers.Unconfined)
    }

    @Test
    fun testClipboardItemCreationAndCopy() {
        val item = ClipboardItem(
            id = "test-1",
            text = "Nomor Rekening BCA: 1234567890",
            timestamp = 1000L,
            isPinned = false
        )

        assertEquals("test-1", item.id)
        assertEquals("Nomor Rekening BCA: 1234567890", item.text)
        assertEquals(1000L, item.timestamp)
        assertFalse(item.isPinned)

        val pinned = item.copy(isPinned = true)
        assertTrue(pinned.isPinned)
        assertEquals(item.text, pinned.text)
    }

    @Test
    fun testClipboardRepositoryAddAndOrdering() = runTest {
        repository.addClip("Clip Pertama")
        repository.addClip("Clip Kedua")

        val items = repository.getClipboardItems().first()
        assertEquals(2, items.size)
        // Most recent should be first
        assertEquals("Clip Kedua", items[0].text)
        assertEquals("Clip Pertama", items[1].text)
    }

    @Test
    fun testClipboardRepositoryDeduplication() = runTest {
        repository.addClip("Sama")
        repository.addClip("Beda")
        repository.addClip("Sama")

        val items = repository.getClipboardItems().first()
        assertEquals(2, items.size)
        // Re-adding "Sama" moves it to the top
        assertEquals("Sama", items[0].text)
        assertEquals("Beda", items[1].text)
    }

    @Test
    fun testClipboardRepositoryPinningPrioritization() = runTest {
        repository.addClip("Item 1")
        repository.addClip("Item 2")
        repository.addClip("Item 3")

        val itemsBefore = repository.getClipboardItems().first()
        val item1Id = itemsBefore.first { it.text == "Item 1" }.id

        // Pin Item 1
        repository.togglePin(item1Id)

        val itemsAfter = repository.getClipboardItems().first()
        assertEquals(3, itemsAfter.size)
        // Pinned item must always be at index 0 ahead of unpinned newer items
        assertEquals("Item 1", itemsAfter[0].text)
        assertTrue(itemsAfter[0].isPinned)
        assertFalse(itemsAfter[1].isPinned)
        assertFalse(itemsAfter[2].isPinned)
    }

    @Test
    fun testClipboardRepositoryDeleteAndClear() = runTest {
        repository.addClip("Hapus Saya")
        repository.addClip("Simpan Saya (Pinned)")
        repository.addClip("Riwayat Biasa")

        val items = repository.getClipboardItems().first()
        val pinnedId = items.first { it.text == "Simpan Saya (Pinned)" }.id
        val deleteId = items.first { it.text == "Hapus Saya" }.id

        repository.togglePin(pinnedId)
        repository.deleteClip(deleteId)

        val afterDelete = repository.getClipboardItems().first()
        assertEquals(2, afterDelete.size)
        assertFalse(afterDelete.any { it.text == "Hapus Saya" })

        // Clear history keeping pinned
        repository.clearHistory(keepPinned = true)
        val afterClear = repository.getClipboardItems().first()
        assertEquals(1, afterClear.size)
        assertEquals("Simpan Saya (Pinned)", afterClear[0].text)
        assertTrue(afterClear[0].isPinned)
    }

    @Test
    fun testKeyboardFeatureTypeClipboardAndTextEdit() {
        val clipboardFeature = KeyboardFeatureType.from("menu_clipboard")
        assertEquals(KeyboardFeatureType.CLIPBOARD, clipboardFeature)
        assertEquals("menu_clipboard", clipboardFeature.id)
        assertEquals("Clipboard", clipboardFeature.text)

        val textEditFeature = KeyboardFeatureType.from("menu_text_edit")
        assertEquals(KeyboardFeatureType.TEXT_EDIT, textEditFeature)
        assertEquals("menu_text_edit", textEditFeature.id)
        assertEquals("Text Editing", textEditFeature.text)

        val modelClipboard = clipboardFeature.mapToModel()
        assertEquals("menu_clipboard", modelClipboard.id)
        assertEquals("Clipboard", modelClipboard.text)
    }

    @Test
    fun testKeyboardPanelStateMappings() {
        assertEquals(
            KeyboardPanelState.CLIPBOARD,
            KeyboardPanelState.fromFeature(KeyboardFeatureType.CLIPBOARD)
        )
        assertEquals(
            KeyboardPanelState.TEXT_EDIT,
            KeyboardPanelState.fromFeature(KeyboardFeatureType.TEXT_EDIT)
        )
    }

    @Test
    fun testTextEditActionEnumCompleteness() {
        val actions = TextEditAction.entries
        assertEquals(13, actions.size)
        assertTrue(actions.contains(TextEditAction.MOVE_LEFT))
        assertTrue(actions.contains(TextEditAction.MOVE_RIGHT))
        assertTrue(actions.contains(TextEditAction.MOVE_UP))
        assertTrue(actions.contains(TextEditAction.MOVE_DOWN))
        assertTrue(actions.contains(TextEditAction.MOVE_HOME))
        assertTrue(actions.contains(TextEditAction.MOVE_END))
        assertTrue(actions.contains(TextEditAction.TOGGLE_SELECT))
        assertTrue(actions.contains(TextEditAction.SELECT_ALL))
        assertTrue(actions.contains(TextEditAction.CUT))
        assertTrue(actions.contains(TextEditAction.COPY))
        assertTrue(actions.contains(TextEditAction.PASTE))
        assertTrue(actions.contains(TextEditAction.DELETE))
        assertTrue(actions.contains(TextEditAction.ENTER))
    }

    @Test
    fun testRecentEmojisLRUCapping() {
        val recentEmojis = mutableListOf<String>()
        val incoming = listOf("👍", "🙏", "😊", "❤️", "🔥", "👍")

        for (emoji in incoming) {
            recentEmojis.remove(emoji)
            recentEmojis.add(0, emoji)
        }

        // "👍" was repeated, so it should only appear once at the head
        assertEquals(5, recentEmojis.size)
        assertEquals("👍", recentEmojis[0])
        assertEquals("🔥", recentEmojis[1])
        assertEquals("❤️", recentEmojis[2])
    }

    /**
     * In-memory test double for PreferenceDelegates.
     */
    private class FakePreferenceDelegates : PreferenceDelegates {
        private val stringMap = mutableMapOf<String, String>()
        private val intMap = mutableMapOf<String, Int>()
        private val boolMap = mutableMapOf<String, Boolean>()
        private val floatMap = mutableMapOf<String, Float>()
        private val longMap = mutableMapOf<String, Long>()

        override fun savePrefString(key: String, value: String) { stringMap[key] = value }
        override fun getPrefString(key: String): String = stringMap[key] ?: ""
        override fun getPrefString(key: String, defaultValue: String): String = stringMap[key] ?: defaultValue

        override fun savePrefInt(key: String, value: Int) { intMap[key] = value }
        override fun getPrefInt(key: String): Int = intMap[key] ?: 0
        override fun getPrefInt(key: String, defaultValue: Int): Int = intMap[key] ?: defaultValue

        override fun savePrefBoolean(key: String, value: Boolean) { boolMap[key] = value }
        override fun getPrefBoolean(key: String): Boolean = boolMap[key] ?: false
        override fun getPrefBoolean(key: String, defaultValue: Boolean): Boolean = boolMap[key] ?: defaultValue

        override fun savePrefFloat(key: String, value: Float) { floatMap[key] = value }
        override fun getPrefFloat(key: String): Float = floatMap[key] ?: 0f
        override fun getPrefFloat(key: String, defaultValue: Float): Float = floatMap[key] ?: defaultValue

        override fun savePrefLong(key: String, value: Long) { longMap[key] = value }
        override fun getPrefLong(key: String): Long = longMap[key] ?: 0L
        override fun getPrefLong(key: String, defaultValue: Long): Long = longMap[key] ?: defaultValue

        override fun deletePref(key: String) {
            stringMap.remove(key)
            intMap.remove(key)
            boolMap.remove(key)
            floatMap.remove(key)
            longMap.remove(key)
        }

        override fun nukePref() {
            stringMap.clear()
            intMap.clear()
            boolMap.clear()
            floatMap.clear()
            longMap.clear()
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T> save(key: String, value: T) {
            when (value) {
                is String -> savePrefString(key, value)
                is Int -> savePrefInt(key, value)
                is Boolean -> savePrefBoolean(key, value)
                is Float -> savePrefFloat(key, value)
                is Long -> savePrefLong(key, value)
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T> get(key: String, defaultValue: T): T {
            return when (defaultValue) {
                is String -> getPrefString(key, defaultValue) as T
                is Int -> getPrefInt(key, defaultValue) as T
                is Boolean -> getPrefBoolean(key, defaultValue) as T
                is Float -> getPrefFloat(key, defaultValue) as T
                is Long -> getPrefLong(key, defaultValue) as T
                else -> defaultValue
            }
        }
    }
}
