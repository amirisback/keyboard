package com.frogobox.appkeyboard.ui.theme.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = FrogoPrimaryDark,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = FrogoPrimaryContainerDark,
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = FrogoSecondaryDark,
    onSecondary = Color(0xFF0F172A),
    tertiary = FrogoPrimaryDark,
    onTertiary = Color(0xFF0F172A),
    background = FrogoBackgroundDark,
    onBackground = FrogoTextDark,
    surface = FrogoSurfaceDark,
    onSurface = FrogoTextDark,
    surfaceVariant = FrogoSurfaceVariantDark,
    onSurfaceVariant = FrogoTextSecondaryDark,
    outline = FrogoBorderDark,
    outlineVariant = FrogoDividerDark
)

private val LightColorScheme = lightColorScheme(
    primary = FrogoPrimary,
    onPrimary = Color.White,
    primaryContainer = FrogoPrimaryContainer,
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = FrogoSecondary,
    onSecondary = Color.White,
    tertiary = FrogoPrimaryVariant,
    onTertiary = Color.White,
    background = FrogoBackground,
    onBackground = FrogoText,
    surface = FrogoSurface,
    onSurface = FrogoText,
    surfaceVariant = FrogoSurfaceVariantLight,
    onSurfaceVariant = FrogoTextSecondary,
    outline = FrogoBorder,
    outlineVariant = FrogoDivider
)

@Composable
fun FrogoKeyboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
