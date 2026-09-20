package com.frogobox.libkeyboard.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.viewinterop.AndroidView
import com.frogobox.libkeyboard.ui.theme.FrogoLibKeyboardTheme
import com.frogobox.libkeyboard.ui.theme.KeypadActionDark
import com.frogobox.libkeyboard.ui.theme.KeypadActionLight
import com.frogobox.libkeyboard.ui.theme.KeypadDark
import com.frogobox.libkeyboard.ui.theme.KeypadLight

/**
 * Jetpack Compose interoperability wrapper for hosting [MainKeyboard] within Compose UI trees.
 */
@Composable
fun MainKeyboardView(
    keyboard: ItemMainKeyboard?,
    onActionListener: OnKeyboardActionListener?,
    modifier: Modifier = Modifier,
    onInit: (MainKeyboard) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MainKeyboard(context, null).apply {
                this.mOnKeyboardActionListener = onActionListener
                keyboard?.let { setKeyboard(it) }
                onInit(this)
            }
        },
        update = { view ->
            view.mOnKeyboardActionListener = onActionListener
            if (keyboard != null) {
                view.setKeyboard(keyboard)
            }
        }
    )
}

/**
 * Declarative Jetpack Compose component rendering keyboard keys based on [ItemMainKeyboard].
 */
@Composable
fun MainKeyboardComposable(
    keyboard: ItemMainKeyboard,
    onKeyPress: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onKeyActionUp: () -> Unit = {},
    isDarkTheme: Boolean = false
) {
    val defaultKeyColor = if (isDarkTheme) KeypadDark else KeypadLight
    val actionKeyColor = if (isDarkTheme) KeypadActionDark else KeypadActionLight

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Group keys by their approximate row position
        val keys = keyboard.mKeys
        if (keys.isNotEmpty()) {
            val rowYPositions = keys.map { it.y }.distinct().sorted()

            for (rowY in rowYPositions) {
                val rowKeys = keys.filter { it.y == rowY }.sortedBy { it.x }
                val totalRowWidth = rowKeys.sumOf { it.width }.coerceAtLeast(1)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (key in rowKeys) {
                        val weight = (key.width.toFloat() / totalRowWidth.toFloat()).coerceAtLeast(0.01f)
                        val isActionKey = when (key.code) {
                            ItemMainKeyboard.KEYCODE_SHIFT,
                            ItemMainKeyboard.KEYCODE_DELETE,
                            ItemMainKeyboard.KEYCODE_ENTER,
                            ItemMainKeyboard.KEYCODE_MODE_CHANGE,
                            ItemMainKeyboard.KEYCODE_TAB -> true
                            else -> false
                        }

                        val keyBackground = if (isActionKey) actionKeyColor else defaultKeyColor

                        KeyItemView(
                            label = key.label.toString(),
                            code = key.code,
                            weight = weight,
                            backgroundColor = keyBackground,
                            isActionKey = isActionKey,
                            onClick = {
                                onKeyPress(key.code)
                                onKeyActionUp()
                            },
                            modifier = Modifier.weight(weight)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyItemView(
    label: String,
    code: Int,
    weight: Float,
    backgroundColor: Color,
    isActionKey: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                onClick = onClick
            ),
        shape = RoundedCornerShape(6.dp),
        color = backgroundColor,
        shadowElevation = if (isActionKey) 1.dp else 1.5.dp
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val displayLabel = when (code) {
                ItemMainKeyboard.KEYCODE_SHIFT -> "⇧"
                ItemMainKeyboard.KEYCODE_DELETE -> "⌫"
                ItemMainKeyboard.KEYCODE_ENTER -> "↵"
                ItemMainKeyboard.KEYCODE_SPACE -> " "
                ItemMainKeyboard.KEYCODE_MODE_CHANGE -> "?123"
                ItemMainKeyboard.KEYCODE_EMOJI -> "🙂"
                ItemMainKeyboard.KEYCODE_TAB -> "⇥"
                else -> label
            }

            Text(
                text = displayLabel,
                fontSize = if (displayLabel.length > 2) 13.sp else 18.sp,
                fontWeight = if (displayLabel.length > 2 || isActionKey) FontWeight.SemiBold else FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}
