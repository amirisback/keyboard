package com.frogobox.appkeyboard.util

/**
 * Ergonomic height resizer and bottom chin offset helper.
 * Provides granular scaling factor presets and system gesture bar offset spacing.
 */
object KeyboardHeightHelper {

    enum class HeightScale(val id: String, val displayName: String, val scaleFactor: Float) {
        COMPACT("COMPACT", "Ringkas (85%)", 0.85f),
        NORMAL("NORMAL", "Normal (100%)", 1.0f),
        TALL("TALL", "Tinggi (115%)", 1.15f),
        EXTRA_TALL("EXTRA_TALL", "Ekstra Tinggi (130%)", 1.30f);

        companion object {
            fun fromString(value: String): HeightScale {
                return entries.firstOrNull { it.id.equals(value, ignoreCase = true) || it.name.equals(value, ignoreCase = true) }
                    ?: NORMAL
            }
        }
    }

    enum class ChinOffset(val id: String, val displayName: String, val offsetDp: Int) {
        NONE("NONE", "0 dp", 0),
        SMALL("SMALL", "8 dp", 8),
        MEDIUM("MEDIUM", "16 dp", 16),
        LARGE("LARGE", "24 dp", 24);

        companion object {
            fun fromString(value: String): ChinOffset {
                return entries.firstOrNull { it.id.equals(value, ignoreCase = true) || it.name.equals(value, ignoreCase = true) }
                    ?: NONE
            }
        }
    }

    /**
     * Calculates the total height in Dp for the keyboard given a base height, scale factor, and chin offset.
     */
    fun calculateEffectiveHeight(baseHeightDp: Float, scale: HeightScale, chinOffset: ChinOffset): Float {
        val scaled = (baseHeightDp * scale.scaleFactor).coerceAtLeast(180f)
        return scaled + chinOffset.offsetDp
    }
}
