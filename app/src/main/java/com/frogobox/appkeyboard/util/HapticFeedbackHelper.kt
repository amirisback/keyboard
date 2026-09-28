package com.frogobox.appkeyboard.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Haptic tactile intensity manager supporting subtle, medium, and firm tactile feedback.
 * Adheres strictly to modern Android vibration APIs across API levels.
 */
object HapticFeedbackHelper {

    enum class HapticIntensity(val durationMs: Long, val displayName: String) {
        OFF(0L, "Nonaktif"),
        SUBTLE(15L, "Halus (Subtle)"),
        MEDIUM(30L, "Sedang (Medium)"),
        STRONG(55L, "Kuat (Strong)")
    }

    /**
     * Triggers tactile feedback conforming to the specified [HapticIntensity].
     */
    fun performHaptic(context: Context, intensity: HapticIntensity) {
        if (intensity == HapticIntensity.OFF || intensity.durationMs <= 0) return

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            context.getSystemService(Vibrator::class.java)
        } ?: return

        if (!vibrator.hasVibrator()) return

        val effect = VibrationEffect.createOneShot(intensity.durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(effect)
    }

    /**
     * Triggers custom tactile feedback with a specific duration in milliseconds.
     */
    fun performHapticMs(context: Context, durationMs: Long) {
        if (durationMs <= 0) return
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            context.getSystemService(Vibrator::class.java)
        } ?: return

        if (!vibrator.hasVibrator()) return

        val effect = VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(effect)
    }

    fun fromString(value: String): HapticIntensity {
        return HapticIntensity.entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: HapticIntensity.MEDIUM
    }
}
