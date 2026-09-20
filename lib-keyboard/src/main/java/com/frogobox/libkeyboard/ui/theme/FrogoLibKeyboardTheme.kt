package com.frogobox.libkeyboard.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val KeyboardTealPrimary = Color(0xFF2563EB)
val KeyboardTealPrimaryDark = Color(0xFF3B82F6)
val KeyboardTealPrimaryContainer = Color(0xFFDBEAFE)
val KeyboardTealPrimaryContainerDark = Color(0xFF1E3A8A)

val KeyboardSurfaceLight = Color(0xFFFFFFFF)
val KeyboardSurfaceDark = Color(0xFF0F172A)
val KeyboardBackgroundLight = Color(0xFFF8FAFC)
val KeyboardBackgroundDark = Color(0xFF020617)

val KeypadLight = Color(0xFFF1F5F9)
val KeypadDark = Color(0xFF1E293B)
val KeypadActionLight = Color(0xFFE2E8F0)
val KeypadActionDark = Color(0xFF334155)

private val DarkColorScheme = darkColorScheme(
    primary = KeyboardTealPrimaryDark,
    onPrimary = Color(0xFF020617),
    primaryContainer = KeyboardTealPrimaryContainerDark,
    onPrimaryContainer = Color(0xFFDBEAFE),
    surface = KeyboardSurfaceDark,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    background = KeyboardBackgroundDark,
    onBackground = Color(0xFFF8FAFC),
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF1E293B)
)

private val LightColorScheme = lightColorScheme(
    primary = KeyboardTealPrimary,
    onPrimary = Color.White,
    primaryContainer = KeyboardTealPrimaryContainer,
    onPrimaryContainer = Color(0xFF1E3A8A),
    surface = KeyboardSurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B),
    background = KeyboardBackgroundLight,
    onBackground = Color(0xFF0F172A),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0)
)

@Composable
fun FrogoLibKeyboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
