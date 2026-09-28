package com.frogobox.appkeyboard.ui.keyboard.clipboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogobox.appkeyboard.R
import com.frogobox.appkeyboard.model.ClipboardItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ClipboardCategoryFilter(val label: String) {
    ALL("Semua"),
    PINNED("Disematkan"),
    TEXT("Teks"),
    NUMBER_CODE("Angka/Kode")
}

object ClipboardFilterHelper {
    fun isNumberOrCode(text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return false
        val digitCount = trimmed.count { it.isDigit() }
        if (digitCount == 0) return false

        // 1. Mostly digits (phone, account, pure number)
        val isMostlyDigits = (digitCount.toDouble() / trimmed.length >= 0.4)

        // 2. Contains standalone numeric code of 4-12 digits (e.g. OTP, verification code)
        val hasStandaloneCode = Regex("\\b\\d{4,12}\\b").containsMatchIn(trimmed)

        // 3. Phone number format (+62..., 08...)
        val isPhone = Regex("^[+]?[0-9\\s\\-()]{7,20}$").matches(trimmed)

        // 4. Contains financial or code keywords with digits
        val hasCodeKeyword = Regex("(?i)\\b(kode|otp|pin|rekening|bca|bri|mandiri|bni|va)\\b").containsMatchIn(trimmed) && digitCount >= 4

        return isMostlyDigits || hasStandaloneCode || isPhone || hasCodeKeyword
    }

    fun filterItems(
        items: List<ClipboardItem>,
        query: String,
        filter: ClipboardCategoryFilter
    ): List<ClipboardItem> {
        return items.filter { item ->
            val matchesQuery = query.isBlank() || item.text.contains(query, ignoreCase = true)
            val matchesCategory = when (filter) {
                ClipboardCategoryFilter.ALL -> true
                ClipboardCategoryFilter.PINNED -> item.isPinned
                ClipboardCategoryFilter.TEXT -> !isNumberOrCode(item.text)
                ClipboardCategoryFilter.NUMBER_CODE -> isNumberOrCode(item.text)
            }
            matchesQuery && matchesCategory
        }
    }
}

@Composable
fun ClipboardKeyboardScreen(
    items: List<ClipboardItem>,
    onCommitText: (String) -> Unit,
    onTogglePin: (String) -> Unit,
    onDeleteClip: (String) -> Unit,
    onClearHistory: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    isAlwaysShow: Boolean = false,
    onToggleAlwaysShow: (() -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(ClipboardCategoryFilter.ALL) }

    val filteredItems = remember(items, searchQuery, selectedFilter) {
        ClipboardFilterHelper.filterItems(items, searchQuery, selectedFilter)
    }

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
                    .height(40.dp)
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
                    text = "Papan Klip",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                if (onToggleAlwaysShow != null) {
                    com.frogobox.appkeyboard.ui.keyboard.common.PinFeatureButton(
                        isPinned = isAlwaysShow,
                        onTogglePin = onToggleAlwaysShow
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }

                if (items.any { !it.isPinned }) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onClearHistory),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus Riwayat",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Hapus Riwayat",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }

            // Search Bar & Filter Chips (Only shown when clipboard has items or searching)
            if (items.isNotEmpty() || searchQuery.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    // Search Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .clip(RoundedCornerShape(17.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cari",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Box(modifier = Modifier.weight(1f)) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Cari riwayat klip...",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                textStyle = TextStyle(
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Hapus Teks Pencarian",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Filter Chips Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ClipboardCategoryFilter.entries.forEach { category ->
                            val isSelected = category == selectedFilter
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedFilter = category },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                },
                                border = if (isSelected) {
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                                } else {
                                    BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                                }
                            ) {
                                Text(
                                    text = category.label,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(
                thickness = 0.8.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                modifier = Modifier.padding(top = 2.dp)
            )

            // Content Area
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu_clipboard),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum Ada Teks di Papan Klip",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Teks yang Anda salin dari aplikasi mana pun akan muncul di sini untuk ditempel secara instan.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tidak Ada Klip Ditemukan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "Tidak ada hasil untuk \"$searchQuery\" pada kategori ${selectedFilter.label}." else "Tidak ada klip pada kategori ${selectedFilter.label}.",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                val pinnedList = remember(filteredItems) { filteredItems.filter { it.isPinned } }
                val unpinnedList = remember(filteredItems) { filteredItems.filterNot { it.isPinned } }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    if (pinnedList.isNotEmpty()) {
                        item(key = "header_pinned") {
                            Text(
                                text = "DISEMATKAN (${pinnedList.size})",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706),
                                letterSpacing = 0.8.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 2.dp)
                            )
                        }

                        items(pinnedList, key = { it.id }) { item ->
                            ClipboardCard(
                                item = item,
                                onClick = { onCommitText(item.text) },
                                onTogglePin = { onTogglePin(item.id) },
                                onDelete = { onDeleteClip(item.id) }
                            )
                        }
                    }

                    if (unpinnedList.isNotEmpty()) {
                        item(key = "header_recent") {
                            Text(
                                text = "TERBARU (${unpinnedList.size})",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.8.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 2.dp)
                            )
                        }

                        items(unpinnedList, key = { it.id }) { item ->
                            ClipboardCard(
                                item = item,
                                onClick = { onCommitText(item.text) },
                                onTogglePin = { onTogglePin(item.id) },
                                onDelete = { onDeleteClip(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClipboardCard(
    item: ClipboardItem,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormatted = remember(item.timestamp) {
        val now = System.currentTimeMillis()
        val diff = now - item.timestamp
        when {
            diff < 60_000 -> "Baru saja"
            diff < 3600_000 -> "${diff / 60_000} mnt lalu"
            diff < 86400_000 -> "${diff / 3600_000} jam lalu"
            else -> SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(item.timestamp))
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isPinned) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        border = if (item.isPinned) {
            BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.45f))
        } else {
            BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 7.dp)
        ) {
            Text(
                text = item.text,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeFormatted,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.weight(1f))

                // Pin / Unpin button
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onTogglePin),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (item.isPinned) "📌" else "📍",
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Delete single clip button
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onDelete),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Hapus Klip",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}
