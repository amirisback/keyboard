package com.frogobox.appkeyboard.ui.keyboard.textedit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class TextEditAction {
    MOVE_LEFT,
    MOVE_RIGHT,
    MOVE_UP,
    MOVE_DOWN,
    MOVE_HOME,
    MOVE_END,
    TOGGLE_SELECT,
    SELECT_ALL,
    CUT,
    COPY,
    PASTE,
    DELETE,
    ENTER
}

@Composable
fun TextEditKeyboardScreen(
    isSelectionMode: Boolean,
    onAction: (TextEditAction) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(270.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali ke Keyboard",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Edit Teks",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                // Selection Mode Status Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelectionMode) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    },
                    border = BorderStroke(
                        0.8.dp,
                        if (isSelectionMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Text(
                        text = if (isSelectionMode) "Mode Seleksi: AKTIF" else "Mode Seleksi: Nonaktif",
                        fontSize = 10.sp,
                        fontWeight = if (isSelectionMode) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelectionMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            HorizontalDivider(
                thickness = 0.8.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )

            // 3-Column Ergonomic Controller Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Column 1: Selection & Clipboard Controls
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    ControllerButton(
                        label = if (isSelectionMode) "✓ Memilih" else "Pilih",
                        onClick = { onAction(TextEditAction.TOGGLE_SELECT) },
                        isActive = isSelectionMode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    ControllerButton(
                        label = "Pilih Semua",
                        onClick = { onAction(TextEditAction.SELECT_ALL) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    ControllerButton(
                        label = "Potong",
                        onClick = { onAction(TextEditAction.CUT) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    ControllerButton(
                        label = "Salin",
                        onClick = { onAction(TextEditAction.COPY) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }

                // Column 2: Navigation D-Pad & Line Jump Controls
                Column(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top: Home / Jump to line start
                    ControllerButton(
                        label = "↖ Awal Kalimat",
                        onClick = { onAction(TextEditAction.MOVE_HOME) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.9f)
                    )

                    // D-Pad Up
                    ControllerButton(
                        label = "▲",
                        onClick = { onAction(TextEditAction.MOVE_UP) },
                        isArrow = true,
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .weight(1f)
                    )

                    // D-Pad Left / Center / Right Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ControllerButton(
                            label = "◀",
                            onClick = { onAction(TextEditAction.MOVE_LEFT) },
                            isArrow = true,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        Box(
                            modifier = Modifier
                                .weight(0.6f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                modifier = Modifier.size(10.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            ) {}
                        }
                        ControllerButton(
                            label = "▶",
                            onClick = { onAction(TextEditAction.MOVE_RIGHT) },
                            isArrow = true,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }

                    // D-Pad Down
                    ControllerButton(
                        label = "▼",
                        onClick = { onAction(TextEditAction.MOVE_DOWN) },
                        isArrow = true,
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .weight(1f)
                    )

                    // Bottom: End / Jump to line end
                    ControllerButton(
                        label = "↘ Akhir Kalimat",
                        onClick = { onAction(TextEditAction.MOVE_END) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.9f)
                    )
                }

                // Column 3: Insertion & Deletion Controls
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    ControllerButton(
                        label = "Tempel",
                        onClick = { onAction(TextEditAction.PASTE) },
                        isActive = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.3f)
                    )
                    ControllerButton(
                        label = "⌫ Hapus",
                        onClick = { onAction(TextEditAction.DELETE) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.3f)
                    )
                    ControllerButton(
                        label = "↵ Enter",
                        onClick = { onAction(TextEditAction.ENTER) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ControllerButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    isArrow: Boolean = false
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (isActive) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        },
        border = BorderStroke(
            1.dp,
            if (isActive) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            }
        ),
        shadowElevation = if (isActive) 2.dp else 1.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = if (isArrow) 18.sp else 11.5.sp,
                fontWeight = if (isActive || isArrow) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isActive) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.Center
            )
        }
    }
}
