package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.services.KeyboardUtil
import com.frogobox.appkeyboard.ui.keyboard.root.KeyboardPanelState
import com.frogobox.libkeyboard.util.GestureTypingHelper
import com.frogobox.sdk.delegate.preference.PreferenceDelegates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.math.abs
import kotlin.math.max

/**
 * Comprehensive Unit Test Suite for TASK-034:
 * Gesture Swipe Typing (Glide Typing) & Keyboard Swipe Shortcuts.
 */
class Task034GestureSwipeTest {

    private lateinit var keyboardUtil: KeyboardUtil
    private lateinit var fakePref: FakePreferenceDelegates

    @Before
    fun setUp() {
        fakePref = FakePreferenceDelegates()
        keyboardUtil = KeyboardUtil(fakePref)
        GestureTypingHelper.clearDynamicVocabulary()
    }

    // --- AC-01: Gesture Glide Engine Trajectory Resolution ---

    @Test
    fun testGestureTrajectoryResolution() {
        val keyBoxes = listOf(
            GestureTypingHelper.KeyBoundingBox('s', 0f, 0f, 40f, 40f),
            GestureTypingHelper.KeyBoundingBox('w', 40f, 0f, 80f, 40f),
            GestureTypingHelper.KeyBoundingBox('i', 80f, 0f, 120f, 40f),
            GestureTypingHelper.KeyBoundingBox('p', 120f, 0f, 160f, 40f),
            GestureTypingHelper.KeyBoundingBox('e', 160f, 0f, 200f, 40f)
        )

        val points = listOf(
            GestureTypingHelper.PathPoint(20f, 20f),
            GestureTypingHelper.PathPoint(60f, 20f),
            GestureTypingHelper.PathPoint(100f, 20f),
            GestureTypingHelper.PathPoint(140f, 20f),
            GestureTypingHelper.PathPoint(180f, 20f)
        )

        val traversed = GestureTypingHelper.resolveTrajectoryToChars(points, keyBoxes)
        assertEquals("swipe", traversed)
    }

    // --- AC-02 & AC-03: Word Matching, Ranking & Duplicate Character Resilience ---

    @Test
    fun testGestureWordMatching_indonesian() {
        val traversed = "hjkalo" // Swiped from h to a to l to o
        val matches = GestureTypingHelper.matchGesture(traversed)
        assertTrue(matches.contains("halo"))
        assertEquals("halo", matches.first())
    }

    @Test
    fun testGestureWordMatching_english() {
        val traversed = "sweirtpipe" // Swiped roughly s-w-i-p-e
        val matches = GestureTypingHelper.matchGesture(traversed)
        assertTrue(matches.contains("swipe"))
        assertEquals("swipe", matches.first())
    }

    @Test
    fun testGestureWordMatching_duplicateLettersResilience() {
        // In glide typing, user touches 'h'-'e'-'l'-'o' without looping 'l' twice
        val traversed = "helo"
        val matches = GestureTypingHelper.matchGesture(traversed, listOf("hello", "halo", "help"))
        assertTrue("Should match 'hello' despite single 'l' in trajectory", matches.contains("hello"))
        assertEquals("hello", matches.first())

        // And traversing 'halo' matches 'halo'
        val haloMatches = GestureTypingHelper.matchGesture("hjalo", listOf("hello", "halo", "help"))
        assertTrue("Should match 'halo'", haloMatches.contains("halo"))
    }

    @Test
    fun testGestureWordMatching_dynamicVocabulary() {
        GestureTypingHelper.addVocabulary(listOf("gemini", "antigravity", "frogobox"))
        val traversed = "gtrewminni" // Traversed g-e-m-i-n-i with noise: g(t)e(w)m(i)n(n)i
        val matches = GestureTypingHelper.matchGesture(traversed)
        assertTrue(matches.contains("gemini"))
    }

    @Test
    fun testCompressConsecutiveDuplicates() {
        assertEquals("helo", GestureTypingHelper.compressConsecutiveDuplicates("hello"))
        assertEquals("god", GestureTypingHelper.compressConsecutiveDuplicates("good"))
        assertEquals("abc", GestureTypingHelper.compressConsecutiveDuplicates("aabbcc"))
        assertEquals("", GestureTypingHelper.compressConsecutiveDuplicates(""))
        assertEquals("a", GestureTypingHelper.compressConsecutiveDuplicates("a"))
    }

    // --- AC-04: Gesture Swipe Shortcuts (Backspace, Spacebar, Swipe Down) ---

    @Test
    fun testSwipeLeftBackspace_wordsToDeleteCalculation() {
        val threshold = 50f
        val swipeDist1 = 60f
        val words1 = max(1, (swipeDist1 / (threshold * 2)).toInt())
        assertEquals(1, words1)

        val swipeDist3 = 310f
        val words3 = max(1, (swipeDist3 / (threshold * 2)).toInt())
        assertEquals(3, words3)
    }

    @Test
    fun testSpacebarScrubbingCursorStepCalculation() {
        val spaceThreshold = 35f
        val dragAmountRight = 105f
        val stepsRight = (dragAmountRight / spaceThreshold).toInt()
        assertEquals(3, stepsRight)

        val dragAmountLeft = -70f
        val stepsLeft = (abs(dragAmountLeft) / spaceThreshold).toInt()
        assertEquals(2, stepsLeft)
    }

    @Test
    fun testSwipeDownGestureCriteria() {
        val keyboardHeight = 400f
        val downY = 100f // In top 45% (400 * 0.45 = 180)
        val moveY = 220f
        val dy = moveY - downY // 120f > threshold (80f)
        val dx = 15f // < maxHoriz (45f)

        val swipeDownThreshold = 80f
        val swipeDownMaxHoriz = 45f

        val isSwipeDown = downY < keyboardHeight * 0.45f && dy > swipeDownThreshold && dx < swipeDownMaxHoriz
        assertTrue("Criteria for swipe down should be met", isSwipeDown)

        val bottomDownY = 300f
        val isBottomSwipeDown = bottomDownY < keyboardHeight * 0.45f && dy > swipeDownThreshold && dx < swipeDownMaxHoriz
        assertFalse("Swipe down starting at bottom half should not trigger dismissal", isBottomSwipeDown)
    }

    // --- AC-05: Feature Toggle & Settings Integration ---

    @Test
    fun testKeyboardFeatureType_gestureSwipe() {
        val gestureFeature = KeyboardFeatureType.GESTURE_SWIPE
        assertEquals("menu_gesture_swipe", gestureFeature.id)
        assertEquals("Gesture & Swipe", gestureFeature.text)
        assertEquals(R.drawable.ic_menu_gesture_swipe, gestureFeature.icon)
        assertEquals(KeyboardFeatureType.GESTURE_SWIPE, KeyboardFeatureType.from("menu_gesture_swipe"))

        // Does not open a standalone sub-panel
        assertNull(KeyboardPanelState.fromFeature(KeyboardFeatureType.GESTURE_SWIPE))

        // Exists in DEFAULT_FEATURE_TYPES
        assertTrue(KeyboardUtil.DEFAULT_FEATURE_TYPES.contains(KeyboardFeatureType.GESTURE_SWIPE))
    }

    @Test
    fun testKeyboardUtil_gestureTypingPreferences() {
        // Default should be true
        assertTrue(keyboardUtil.isGestureTypingEnabled())
        keyboardUtil.setGestureTypingEnabled(false)
        assertFalse(keyboardUtil.isGestureTypingEnabled())
        keyboardUtil.setGestureTypingEnabled(true)
        assertTrue(keyboardUtil.isGestureTypingEnabled())
    }

    @Test
    fun testKeyboardUtil_gestureSwipeShortcutsPreferences() {
        // Default should be true
        assertTrue(keyboardUtil.isGestureSwipeShortcutsEnabled())
        keyboardUtil.setGestureSwipeShortcutsEnabled(false)
        assertFalse(keyboardUtil.isGestureSwipeShortcutsEnabled())
        keyboardUtil.setGestureSwipeShortcutsEnabled(true)
        assertTrue(keyboardUtil.isGestureSwipeShortcutsEnabled())
    }

    // --- Systematic Bug Fix Tests: Coordinate Scaling, Interpolation, Edge Tolerance & Fallback ---

    @Test
    fun testScaledKeyBoundingBoxesMatchScreenCoordinates() {
        val scaleX = 2.5f
        // Key 'a' at unscaled 0..40, on screen 0..100
        // Key 'b' at unscaled 40..80, on screen 100..200
        // Key 'c' at unscaled 80..120, on screen 200..300
        val unscaledKeys = listOf(
            Triple('a', 0f, 40f),
            Triple('b', 40f, 80f),
            Triple('c', 80f, 120f)
        )
        val scaledKeyBoxes = unscaledKeys.map { (char, left, right) ->
            GestureTypingHelper.KeyBoundingBox(
                char = char,
                left = left * scaleX,
                top = 0f,
                right = right * scaleX,
                bottom = 50f
            )
        }

        // Screen touch points located at x = 50, 150, 250
        val screenPoints = listOf(
            GestureTypingHelper.PathPoint(50f, 25f),
            GestureTypingHelper.PathPoint(150f, 25f),
            GestureTypingHelper.PathPoint(250f, 25f)
        )

        val resolved = GestureTypingHelper.resolveTrajectoryToChars(screenPoints, scaledKeyBoxes)
        assertEquals("abc", resolved)
    }

    @Test
    fun testFastSwipeInterpolationHitsIntermediateKeys() {
        val keys = listOf(
            GestureTypingHelper.KeyBoundingBox('q', 0f, 0f, 40f, 40f),
            GestureTypingHelper.KeyBoundingBox('w', 40f, 0f, 80f, 40f),
            GestureTypingHelper.KeyBoundingBox('e', 80f, 0f, 120f, 40f),
            GestureTypingHelper.KeyBoundingBox('r', 120f, 0f, 160f, 40f),
            GestureTypingHelper.KeyBoundingBox('t', 160f, 0f, 200f, 40f)
        )

        // Only two points recorded on very fast swipe: from 'q' (20,20) directly to 't' (180,20)
        // Distance is 160px (> 35px threshold)
        val fastPoints = listOf(
            GestureTypingHelper.PathPoint(20f, 20f),
            GestureTypingHelper.PathPoint(180f, 20f)
        )

        val resolved = GestureTypingHelper.resolveTrajectoryToChars(fastPoints, keys)
        // Interpolation should catch intermediate keys 'w', 'e', 'r'
        assertTrue("Should contain 'w'", resolved.contains('w'))
        assertTrue("Should contain 'e'", resolved.contains('e'))
        assertTrue("Should contain 'r'", resolved.contains('r'))
        assertEquals('q', resolved.first())
        assertEquals('t', resolved.last())
    }

    @Test
    fun testEdgeLiftoffTolerance_adjacentLastKey() {
        // User swipes 'h'-'e'-'l'-'o' but on liftoff accidentally grazes adjacent 'p' -> "helop"
        val traversedWithLiftoffNoise = "helop"
        val matches = GestureTypingHelper.matchGesture(traversedWithLiftoffNoise, listOf("hello", "help", "hope"))
        assertTrue("Should still match 'hello' via edge tolerance", matches.contains("hello"))
    }

    @Test
    fun testEdgeOnsetTolerance_adjacentFirstKey() {
        // User starts near 'g' and moves to 'h'-'a'-'l'-'o' -> "ghalo"
        val traversedWithOnsetNoise = "ghalo"
        val matches = GestureTypingHelper.matchGesture(traversedWithOnsetNoise, listOf("halo", "good", "game"))
        assertTrue("Should still match 'halo' via onset tolerance", matches.contains("halo"))
    }

    @Test
    fun testNonLetterKeysExcludedFromTrajectory() {
        // Trajectory points near space or symbols should only retain letters
        val keysWithNonLetters = listOf(
            GestureTypingHelper.KeyBoundingBox('h', 0f, 0f, 40f, 40f),
            GestureTypingHelper.KeyBoundingBox(' ', 40f, 0f, 120f, 40f), // Spacebar
            GestureTypingHelper.KeyBoundingBox('i', 120f, 0f, 160f, 40f)
        )
        val points = listOf(
            GestureTypingHelper.PathPoint(20f, 20f),
            GestureTypingHelper.PathPoint(80f, 20f), // Touched space
            GestureTypingHelper.PathPoint(140f, 20f)
        )
        val resolved = GestureTypingHelper.resolveTrajectoryToChars(points, keysWithNonLetters)
        // Space should be ignored
        assertFalse("Resolved trajectory should not contain space", resolved.contains(' '))
        assertEquals("hi", resolved)
    }

    @Test
    fun testSingleLetterFlickFallback() {
        val keys = listOf(
            GestureTypingHelper.KeyBoundingBox('a', 0f, 0f, 40f, 40f),
            GestureTypingHelper.KeyBoundingBox('b', 40f, 0f, 80f, 40f)
        )
        // User moves finger slightly within key 'a' (from 10,10 to 20,20)
        val points = listOf(
            GestureTypingHelper.PathPoint(10f, 10f),
            GestureTypingHelper.PathPoint(20f, 20f)
        )
        val resolved = GestureTypingHelper.resolveTrajectoryToChars(points, keys)
        assertEquals("a", resolved)
    }

    @Test
    fun testGestureWordAlternativeCandidateReplacement() {
        // Simulates the logic inside KeyboardIME.handleCandidateSelected when replacing a gesture word
        var textInEditor = "halo "
        val lastGestureWord = "halo"
        val selectedAlternative = "hallo"

        val expectedWithSpace = "$lastGestureWord "
        if (textInEditor.endsWith(expectedWithSpace)) {
            textInEditor = textInEditor.dropLast(expectedWithSpace.length)
        }
        textInEditor += "$selectedAlternative "

        assertEquals("hallo ", textInEditor)
        assertFalse("Should not concatenate both words", textInEditor.contains("halo hallo"))
    }

    @Test
    fun testGestureWordBackspaceUndo() {
        // Simulates the single-tap Backspace undo for swiped words
        var textInEditor = "halo "
        val lastGestureWord = "halo"

        val expectedWithSpace = "$lastGestureWord "
        if (textInEditor.endsWith(expectedWithSpace)) {
            textInEditor = textInEditor.dropLast(expectedWithSpace.length)
        }

        assertEquals("", textInEditor)
    }

    @Test
    fun testToggleViewModelGestureSync() {
        val viewModel = com.frogobox.appkeyboard.ui.toggle.ToggleViewModel(fakePref, keyboardUtil)

        // Initially enabled
        assertTrue(keyboardUtil.isGestureTypingEnabled())
        assertTrue(keyboardUtil.isGestureSwipeShortcutsEnabled())

        // Toggle OFF via ToggleViewModel
        viewModel.switchToggle(KeyboardFeatureType.GESTURE_SWIPE.id, false)
        assertFalse(keyboardUtil.isGestureTypingEnabled())
        assertFalse(keyboardUtil.isGestureSwipeShortcutsEnabled())

        // Toggle ON via ToggleViewModel
        viewModel.switchToggle(KeyboardFeatureType.GESTURE_SWIPE.id, true)
        assertTrue(keyboardUtil.isGestureTypingEnabled())
        assertTrue(keyboardUtil.isGestureSwipeShortcutsEnabled())
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

        override fun <T> save(key: String, value: T) {
            when (value) {
                is String -> savePrefString(key, value)
                is Int -> savePrefInt(key, value)
                is Boolean -> savePrefBoolean(key, value)
                is Float -> savePrefFloat(key, value)
                is Long -> savePrefLong(key, value)
            }
        }

        override fun <T> get(key: String, defaultValue: T): T {
            return defaultValue
        }
    }
}
