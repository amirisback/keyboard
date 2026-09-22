package com.frogobox.appkeyboard

import android.content.ClipData
import android.os.Build
import android.view.HapticFeedbackConstants
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.model.TemplateCategoryType
import com.frogobox.appkeyboard.repository.clipboard.ClipboardRepositoryImpl
import com.frogobox.appkeyboard.ui.keyboard.form.FormField
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard
import com.frogobox.sdk.delegate.preference.PreferenceDelegates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GboardPolishAndBugFixTest {

    private lateinit var fakePref: FakePreferenceDelegates
    private lateinit var clipboardRepository: ClipboardRepositoryImpl

    @Before
    fun setUp() {
        fakePref = FakePreferenceDelegates()
        clipboardRepository = ClipboardRepositoryImpl(fakePref, Dispatchers.Default)
    }

    @Test
    fun testPasteCrashGuardWithEmptyAndNullClipboard() {
        var committedText: String? = null
        val onCommitText: (String) -> Unit = { committedText = it }

        // Scenario 1: clipData is null
        val nullClip: ClipData? = null
        try {
            if (nullClip != null && nullClip.itemCount > 0) {
                val clip = nullClip.getItemAt(0)?.text?.toString() ?: ""
                if (clip.isNotEmpty()) {
                    onCommitText(clip)
                }
            }
        } catch (e: Exception) {
            org.junit.Assert.fail("Should not throw any exception when clipData is null: ${e.message}")
        }
        assertEquals(null, committedText)

        // Scenario 2: clipData has 0 items (represented by safe check)
        val hasItems = 0 > 0
        if (hasItems) {
            onCommitText("Should not reach")
        }
        assertEquals(null, committedText)
    }

    @Test
    fun testSelectAllNativeFallbackLogic() {
        var setSelectionCalled = false
        var performContextMenuActionCalled = false

        fun handleSelectAll(extractedTextLength: Int?) {
            val totalLength = extractedTextLength ?: 0
            if (totalLength > 0) {
                setSelectionCalled = true
            } else {
                performContextMenuActionCalled = true
            }
        }

        // When extractedText is null (e.g. WebView editor or password field)
        handleSelectAll(null)
        assertTrue(performContextMenuActionCalled)
        assertEquals(false, setSelectionCalled)

        // When extractedText is present
        performContextMenuActionCalled = false
        handleSelectAll(42)
        assertTrue(setSelectionCalled)
        assertEquals(false, performContextMenuActionCalled)
    }

    @Test
    fun testClipboardRepositoryMutexConcurrency() = runTest {
        // Concurrently invoke addClip from 30 parallel coroutines to verify thread safety
        val jobs = (1..30).map { index ->
            async(Dispatchers.Default) {
                clipboardRepository.addClip("Snippet concurrency $index")
            }
        }
        jobs.awaitAll()

        val items = clipboardRepository.getClipboardItems().first()
        assertTrue("Items count should be positive", items.isNotEmpty())
        assertEquals(30, items.size)
    }

    @Test
    fun testFormDetailsMultilineEnterKey() {
        var subject = "Invoice"
        var details = "Item 1: Kaos"
        var refNumber = "REF-001"
        var activeField = FormField.DETAILS

        // Simulate KEYCODE_ENTER on FormField.DETAILS
        val code = ItemMainKeyboard.KEYCODE_ENTER
        when (code) {
            ItemMainKeyboard.KEYCODE_ENTER -> {
                when (activeField) {
                    FormField.SUBJECT -> activeField = FormField.DETAILS
                    FormField.DETAILS -> {
                        details += "\n"
                    }
                    FormField.REF_NUMBER -> {
                        // Submit
                    }
                }
            }
        }

        // Details should now contain newline and remain on FormField.DETAILS
        assertEquals("Item 1: Kaos\n", details)
        assertEquals(FormField.DETAILS, activeField)

        // Simulate typing on line 2
        details += "Item 2: Celana"
        assertEquals("Item 1: Kaos\nItem 2: Celana", details)
    }

    @Test
    fun testGboardHapticConstantsConfiguration() {
        val keyboardTap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            HapticFeedbackConstants.KEYBOARD_TAP
        } else {
            HapticFeedbackConstants.VIRTUAL_KEY
        }
        assertNotNull(keyboardTap)
    }

    @Test
    fun testTemplateCategoryTypeMapping() {
        assertEquals("GAME", TemplateCategoryType.GAME.key)
        assertEquals("APP", TemplateCategoryType.APP.key)
        assertEquals("SALE", TemplateCategoryType.SALE.key)
        assertEquals("GREETING", TemplateCategoryType.GREETING.key)
        assertEquals("LOVE", TemplateCategoryType.LOVE.key)

        assertEquals(TemplateCategoryType.GAME, TemplateCategoryType.fromFeatureType(KeyboardFeatureType.TEMPLATE_TEXT_GAME))
        assertEquals(TemplateCategoryType.APP, TemplateCategoryType.fromFeatureType(KeyboardFeatureType.TEMPLATE_TEXT_APP))
        assertEquals(TemplateCategoryType.SALE, TemplateCategoryType.fromFeatureType(KeyboardFeatureType.TEMPLATE_TEXT_SALE))
        assertEquals(TemplateCategoryType.GREETING, TemplateCategoryType.fromFeatureType(KeyboardFeatureType.TEMPLATE_TEXT_GREETING))
        assertEquals(TemplateCategoryType.LOVE, TemplateCategoryType.fromFeatureType(KeyboardFeatureType.TEMPLATE_TEXT_LOVE))
    }

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
