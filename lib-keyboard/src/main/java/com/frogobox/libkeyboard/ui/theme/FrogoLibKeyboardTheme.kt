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

val KeyboardTealPrimary = Color(0xFF009688)
val KeyboardTealPrimaryDark = Color(0xFF80CBC4)
val KeyboardTealPrimaryContainer = Color(0xFFB2DFDB)
val KeyboardTealPrimaryContainerDark = Color(0xFF004D40)

val KeyboardSurfaceLight = Color(0xFFFFFFFF)
val KeyboardSurfaceDark = Color(0xFF1E1E1E)
val KeyboardBackgroundLight = Color(0xFFF5F5F5)
val KeyboardBackgroundDark = Color(0xFF121212)

val KeypadLight = Color(0xFFE3E5E8)
val KeypadDark = Color(0xFF2C2C2C)
val KeypadActionLight = Color(0xFFCFD4D9)
val KeypadActionDark = Color(0xFF3A3A3A)

private val DarkColorScheme = darkColorScheme(
    primary = KeyboardTealPrimaryDark,
    onPrimary = Color(0xFF003731),
    primaryContainer = KeyboardTealPrimaryContainerDark,
    onPrimaryContainer = Color(0xFFB2DFDB),
    surface = KeyboardSurfaceDark,
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFB0B0B0),
    background = KeyboardBackgroundDark,
    onBackground = Color(0xFFE0E0E0),
    outline = Color(0xFF383838)
)

private val LightColorScheme = lightColorScheme(
    primary = KeyboardTealPrimary,
    onPrimary = Color.White,
    primaryContainer = KeyboardTealPrimaryContainer,
    onPrimaryContainer = Color(0xFF004D40),
    surface = KeyboardSurfaceLight,
    onSurface = Color(0xFF212121),
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF616161),
    background = KeyboardBackgroundLight,
    onBackground = Color(0xFF212121),
    outline = Color(0xFFE0E0E0)
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
