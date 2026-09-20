package com.frogobox.libkeyboard.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogobox.libkeyboard.ui.theme.FrogoLibKeyboardTheme

/**
 * Modern Jetpack Compose Mini Keyboard Popup component replacing legacy keyboard_main_mini.xml.
 * Renders a horizontal capsule strip containing alternate characters on key long-press.
 */
@Composable
fun MiniKeyboardPopup(
    characters: List<String>,
    onKeySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    selectedIndex: Int = -1,
    isDarkTheme: Boolean = false
) {
    val containerColor = if (isDarkTheme) Color(0xFF2B2B2C) else Color(0xFFECEFF1)
    val selectedItemColor = MaterialTheme.colorScheme.primaryContainer
    val defaultItemColor = Color.Transparent
    val defaultTextColor = if (isDarkTheme) Color.White else Color(0xFF1E1E1E)
    val selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer

    Surface(
        modifier = modifier
            .wrapContentWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        shadowElevation = 8.dp,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            characters.forEachIndexed { index, char ->
                val isSelected = index == selectedIndex
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) selectedItemColor else defaultItemColor,
                    animationSpec = tween(durationMillis = 120),
                    label = "MiniKeyHighlight"
                )
                val textColor = if (isSelected) selectedTextColor else defaultTextColor
                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 38.dp)
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(backgroundColor)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = ripple(bounded = true),
                            onClick = { onKeySelected(char) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char,
                        fontSize = 18.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
            }
        }
    }
}

@Preview(name = "Mini Keyboard Light", showBackground = true)
@Composable
private fun MiniKeyboardPopupLightPreview() {
    FrogoLibKeyboardTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            MiniKeyboardPopup(
                characters = listOf("á", "à", "â", "ä", "ã", "å"),
                selectedIndex = 2,
                onKeySelected = {},
                isDarkTheme = false
            )
        }
    }
}

@Preview(name = "Mini Keyboard Dark", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun MiniKeyboardPopupDarkPreview() {
    FrogoLibKeyboardTheme(darkTheme = true) {
        Box(modifier = Modifier.padding(16.dp)) {
            MiniKeyboardPopup(
                characters = listOf("1", "!", "@", "#", "$"),
                selectedIndex = 0,
                onKeySelected = {},
                isDarkTheme = true
            )
        }
    }
}
