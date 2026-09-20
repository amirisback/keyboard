package com.frogobox.libkeyboard

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.frogobox.libkeyboard.ui.main.MainKeyboard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit and Robolectric regression test suite for TASK-021 & TASK-022:
 * Validates defensive null-safety architecture and valid keyTextSize initialization
 * during programmatic instantiation and window attachment / visibility changes.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainKeyboardBackgroundTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testProgrammaticInstantiation_initializesValidKeyTextSize() {
        // Given programmatic instantiation with null AttributeSet (Compose interop pattern)
        val keyboard = MainKeyboard(context, null)

        // Then keyTextSize must be initialized to keyboard_text_size dimension instead of 0
        val expectedSize = context.resources.getDimensionPixelSize(R.dimen.keyboard_text_size)
        assertTrue("Key text size must be greater than 0", keyboard.getKeyTextSize() > 0)
        assertEquals("Key text size should match R.dimen.keyboard_text_size", expectedSize, keyboard.getKeyTextSize())
    }

    @Test
    fun testProgrammaticInstantiation_initializesNonNullBackground() {
        // Given programmatic instantiation with null AttributeSet (Compose interop pattern)
        val keyboard = MainKeyboard(context, null)

        // Then background must be non-null immediately upon init (Defense Layer 1)
        assertNotNull("Background must be non-null after programmatic instantiation", keyboard.background)
        assertTrue("Background should be an instance of ColorDrawable", keyboard.background is ColorDrawable)
    }

    @Test
    fun testOnVisibilityChanged_whenBackgroundNull_fallbacksGracefullyWithoutNPE() {
        // Given programmatic instantiation
        val keyboard = MainKeyboard(context, null)

        // Simulate edge-case where background was cleared or set to null at runtime
        keyboard.background = null
        assertNull(keyboard.background)

        // When onVisibilityChanged is triggered with VISIBLE (as in dispatchAttachedToWindow)
        keyboard.onVisibilityChanged(keyboard, View.VISIBLE)

        // Then no NPE must be thrown, and background must be defensively restored (Defense Layer 2)
        assertNotNull("Background must be safely restored upon becoming visible", keyboard.background)
        assertTrue("Restored background should be a ColorDrawable", keyboard.background is ColorDrawable)
    }

    @Test
    fun testOnVisibilityChanged_whenBackgroundNonNull_appliesColorFilterSafely() {
        // Given programmatic instantiation
        val keyboard = MainKeyboard(context, null)
        assertNotNull(keyboard.background)

        // When onVisibilityChanged is dispatched with VISIBLE
        keyboard.onVisibilityChanged(keyboard, View.VISIBLE)

        // Then background remains valid and non-null without exception
        assertNotNull("Background must remain non-null after visibility change", keyboard.background)
    }

    @Test
    fun testOnVisibilityChanged_miniKeyboardView_safeCastWithoutClassCastException() {
        // Given programmatic instantiation
        val keyboard = MainKeyboard(context, null)

        // Set background to a non-LayerDrawable (e.g., ColorDrawable)
        keyboard.background = ColorDrawable(Color.DKGRAY)

        // Create a simulated mini_keyboard_view
        val miniView = View(context).apply {
            id = R.id.mini_keyboard_view
        }

        // When onVisibilityChanged is triggered targeting mini_keyboard_view
        // with a non-LayerDrawable background
        keyboard.onVisibilityChanged(miniView, View.VISIBLE)

        // Then no ClassCastException or NPE must occur (safe-cast as? LayerDrawable succeeds)
        assertNotNull("Background remains safe and non-null", keyboard.background)
    }

    @Test
    fun testOnVisibilityChanged_whenVisibilityNotVisible_doesNotThrow() {
        val keyboard = MainKeyboard(context, null)
        keyboard.background = null

        // GONE or INVISIBLE should bypass visibility logic cleanly
        keyboard.onVisibilityChanged(keyboard, View.GONE)
        keyboard.onVisibilityChanged(keyboard, View.INVISIBLE)

        assertNull("Background remains null when view is not visible", keyboard.background)
    }
}
