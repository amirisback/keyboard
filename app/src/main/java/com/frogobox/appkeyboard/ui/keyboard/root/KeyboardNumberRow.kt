package com.frogobox.appkeyboard.ui.keyboard.root

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Dedicated 10-key numeric row (1-0) rendered above the QWERTY keyboard layout.
 * Conforms to Material 3 Expressive and anti-slop guidelines.
 */
@Composable
fun KeyboardNumberRow(
    onNumberClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    textColor: Color? = null,
    backgroundColor: Color? = null
) {
    val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val defaultKeyBg = backgroundColor ?: if (isDarkTheme) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.50f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.70f)
    }
    val defaultTextColor = textColor ?: MaterialTheme.colorScheme.onSurface

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        numbers.forEach { numStr ->
            val charCode = numStr.first().code
            val interactionSource = remember { MutableInteractionSource() }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                        onClick = { onNumberClick(charCode) }
                    ),
                shape = RoundedCornerShape(6.dp),
                color = defaultKeyBg
            ) {
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = numStr,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = defaultTextColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
