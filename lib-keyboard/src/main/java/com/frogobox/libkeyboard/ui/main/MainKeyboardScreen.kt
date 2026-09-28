package com.frogobox.libkeyboard.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Popup
import com.frogobox.libkeyboard.R
import com.frogobox.libkeyboard.ui.theme.KeypadActionDark
import com.frogobox.libkeyboard.ui.theme.KeypadActionLight
import com.frogobox.libkeyboard.ui.theme.KeypadDark
import com.frogobox.libkeyboard.ui.theme.KeypadLight
import com.frogobox.sdk.ext.getColorExt

/**
 * Jetpack Compose interoperability wrapper for hosting [MainKeyboard] within Compose UI trees.
 */
@Composable
fun MainKeyboardView(
    keyboard: ItemMainKeyboard?,
    onActionListener: OnKeyboardActionListener?,
    modifier: Modifier = Modifier,
    textColor: Int? = null,
    actionTextColor: Int? = null,
    keyColor: Int? = null,
    actionKeyColor: Int? = null,
    isDarkTheme: Boolean = false,
    onInit: (MainKeyboard) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MainKeyboard(context, null).apply {
                if (background == null) {
                    setBackgroundColor(context.getColorExt(R.color.keyboard_board))
                }
                if (textColor != null) {
                    setKeyboardTheme(
                        textColor = textColor,
                        actionTextColor = actionTextColor ?: textColor,
                        keyColor = keyColor,
                        actionKeyColor = actionKeyColor,
                        isDark = isDarkTheme
                    )
                }
                this.mOnKeyboardActionListener = onActionListener
                keyboard?.let { setKeyboard(it) }
                onInit(this)
            }
        },
        update = { view ->
            view.mOnKeyboardActionListener = onActionListener
            if (textColor != null) {
                view.setKeyboardTheme(
                    textColor = textColor,
                    actionTextColor = actionTextColor ?: textColor,
                    keyColor = keyColor,
                    actionKeyColor = actionKeyColor,
                    isDark = isDarkTheme
                )
            }
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
    isDarkTheme: Boolean = false,
    onMoveCursorLeft: () -> Unit = {},
    onMoveCursorRight: () -> Unit = {},
    onDeleteWords: (Int) -> Unit = {},
    isSplitMode: Boolean = false
) {
    val defaultKeyColor = if (isDarkTheme) KeypadDark else KeypadLight
    val actionKeyColor = if (isDarkTheme) KeypadActionDark else KeypadActionLight

    var activePopupKey by remember { mutableStateOf<ItemMainKeyboard.Key?>(null) }

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
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
                    val midpoint = rowKeys.size / 2

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for ((index, key) in rowKeys.withIndex()) {
                            if (isSplitMode && index == midpoint) {
                                androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(0.18f))
                            }
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
                            val hint = when {
                                key.topSmallNumber.isNotEmpty() -> key.topSmallNumber
                                key.popupCharacters != null && key.popupCharacters!!.isNotEmpty() -> {
                                    val firstChar = key.popupCharacters!!.first().toString()
                                    if (firstChar.all { it.isLetterOrDigit() || it in "@#$%&*+-=()!\"':;/?.,~`" }) firstChar else null
                                }
                                else -> null
                            }

                            KeyItemView(
                                label = key.label.toString(),
                                code = key.code,
                                hintLabel = hint,
                                backgroundColor = keyBackground,
                                isActionKey = isActionKey,
                                onClick = {
                                    onKeyPress(key.code)
                                    onKeyActionUp()
                                },
                                onLongClick = if (key.popupCharacters != null && key.popupCharacters!!.isNotEmpty()) {
                                    { activePopupKey = key }
                                } else null,
                                onDragLeft = onMoveCursorLeft,
                                onDragRight = onMoveCursorRight,
                                onSwipeDelete = onDeleteWords,
                                modifier = Modifier.weight(weight)
                            )
                        }
                    }
                }
            }
        }

        // Render Compose MiniKeyboardPopup overlay on long-press
        activePopupKey?.let { popupKey ->
            val chars = popupKey.popupCharacters?.map { it.toString() } ?: emptyList()
            if (chars.isNotEmpty()) {
                Popup(
                    alignment = Alignment.TopCenter,
                    onDismissRequest = { activePopupKey = null }
                ) {
                    Box(modifier = Modifier.padding(top = 8.dp)) {
                        MiniKeyboardPopup(
                            characters = chars,
                            onKeySelected = { selectedChar ->
                                val code = selectedChar.firstOrNull()?.code ?: 0
                                if (code != 0) {
                                    onKeyPress(code)
                                    onKeyActionUp()
                                }
                                activePopupKey = null
                            },
                            isDarkTheme = isDarkTheme
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun KeyItemView(
    label: String,
    code: Int,
    backgroundColor: Color,
    isActionKey: Boolean,
    onClick: () -> Unit,
    hintLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onDragLeft: (() -> Unit)? = null,
    onDragRight: (() -> Unit)? = null,
    onSwipeDelete: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isSlidingSpace by remember { mutableStateOf(false) }
    var accumulatedSpaceDrag by remember { mutableFloatStateOf(0f) }
    var accumulatedDeleteDrag by remember { mutableFloatStateOf(0f) }

    val gestureModifier = when (code) {
        ItemMainKeyboard.KEYCODE_SPACE -> {
            Modifier.pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        accumulatedSpaceDrag = 0f
                        isSlidingSpace = false
                    },
                    onDragEnd = {
                        isSlidingSpace = false
                        accumulatedSpaceDrag = 0f
                    },
                    onDragCancel = {
                        isSlidingSpace = false
                        accumulatedSpaceDrag = 0f
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        accumulatedSpaceDrag += dragAmount
                        val threshold = 35f
                        if (!isSlidingSpace && kotlin.math.abs(accumulatedSpaceDrag) > threshold) {
                            isSlidingSpace = true
                        }
                        if (accumulatedSpaceDrag > threshold) {
                            onDragRight?.invoke()
                            accumulatedSpaceDrag -= threshold
                        } else if (accumulatedSpaceDrag < -threshold) {
                            onDragLeft?.invoke()
                            accumulatedSpaceDrag += threshold
                        }
                    }
                )
            }
        }
        ItemMainKeyboard.KEYCODE_DELETE -> {
            Modifier.pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { accumulatedDeleteDrag = 0f },
                    onDragEnd = {
                        if (accumulatedDeleteDrag < -60f) {
                            val words = kotlin.math.max(1, ((-accumulatedDeleteDrag) / 90f).toInt())
                            onSwipeDelete?.invoke(words)
                        }
                        accumulatedDeleteDrag = 0f
                    },
                    onDragCancel = { accumulatedDeleteDrag = 0f },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        accumulatedDeleteDrag += dragAmount
                    }
                )
            }
        }
        else -> Modifier
    }

    Surface(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(6.dp))
            .then(gestureModifier)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                onClick = {
                    if (!isSlidingSpace) {
                        onClick()
                    }
                },
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(6.dp),
        color = if (code == ItemMainKeyboard.KEYCODE_SPACE && isSlidingSpace) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
        } else backgroundColor,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        shadowElevation = if (isActionKey) 1.dp else 1.5.dp
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val displayLabel = when {
                code == ItemMainKeyboard.KEYCODE_SPACE && isSlidingSpace -> "‹ KURSOR TRACKPAD ›"
                code == ItemMainKeyboard.KEYCODE_SHIFT -> "⇧"
                code == ItemMainKeyboard.KEYCODE_DELETE -> "⌫"
                code == ItemMainKeyboard.KEYCODE_ENTER -> "↵"
                code == ItemMainKeyboard.KEYCODE_SPACE -> " "
                code == ItemMainKeyboard.KEYCODE_MODE_CHANGE -> "?123"
                code == ItemMainKeyboard.KEYCODE_EMOJI -> "🙂"
                code == ItemMainKeyboard.KEYCODE_TAB -> "⇥"
                else -> label
            }

            Text(
                text = displayLabel,
                fontSize = if (displayLabel.length > 2) 12.sp else 18.sp,
                fontWeight = if (displayLabel.length > 2 || isActionKey) FontWeight.SemiBold else FontWeight.Medium,
                color = if (code == ItemMainKeyboard.KEYCODE_SPACE && isSlidingSpace) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            if (!hintLabel.isNullOrBlank() && !isActionKey && code != ItemMainKeyboard.KEYCODE_SPACE) {
                Text(
                    text = hintLabel,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 2.dp, end = 3.dp)
                )
            }
        }
    }
}
