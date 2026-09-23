package com.frogobox.libkeyboard.ui.emoji

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.emoji2.text.EmojiCompat
import com.frogobox.libkeyboard.R
import com.frogobox.libkeyboard.ui.theme.FrogoLibKeyboardTheme

val EmojiCategoryType.displayName: String
    get() = when (this) {
        EmojiCategoryType.RECENT -> "Recent"
        EmojiCategoryType.GENERAL -> "General"
        EmojiCategoryType.ACTIVITIES -> "Activities"
        EmojiCategoryType.ANIMAL_NATURE -> "Animals & Nature"
        EmojiCategoryType.FLAG -> "Flags"
        EmojiCategoryType.FOOD_DRINK -> "Food & Drink"
        EmojiCategoryType.OBJECTS -> "Objects"
        EmojiCategoryType.PEOPLE_BODY -> "People & Body"
        EmojiCategoryType.SMILEYS_EMOTION -> "Smileys & Emotion"
        EmojiCategoryType.SYMBOLS -> "Symbols"
        EmojiCategoryType.TRAVEL_PLACES -> "Travel & Places"
    }

/**
 * Modern Jetpack Compose Screen for browsing and selecting emojis with real-time bilingual search.
 */
@Composable
fun EmojiKeyboardScreen(
    emojis: List<String>,
    selectedCategory: EmojiCategoryType,
    categories: List<EmojiCategory>,
    onCategorySelected: (EmojiCategoryType) -> Unit,
    onEmojiClicked: (String) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    isLoading: Boolean = false,
    onDeleteClicked: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val displayedEmojis = remember(searchQuery, isSearchActive, emojis) {
        if (isSearchActive && searchQuery.isNotBlank()) {
            val results = EmojiSearchEngine.search(searchQuery)
            if (results.isNotEmpty()) results else emptyList()
        } else {
            emojis
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Toolbar with Toggleable Search Bar
            EmojiHeaderToolbar(
                selectedCategory = selectedCategory,
                isSearchActive = isSearchActive,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onToggleSearch = {
                    isSearchActive = !isSearchActive
                    if (!isSearchActive) searchQuery = ""
                },
                onBackClicked = {
                    if (isSearchActive) {
                        isSearchActive = false
                        searchQuery = ""
                    } else {
                        onBackClicked()
                    }
                },
                onDeleteClicked = onDeleteClicked
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            // Emoji Grid or Loading / Empty Search State
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                } else if (displayedEmojis.isEmpty()) {
                    Text(
                        text = if (isSearchActive && searchQuery.isNotBlank()) {
                            "Emoji tidak ditemukan untuk \"$searchQuery\""
                        } else {
                            "No emojis found"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 42.dp),
                        state = lazyGridState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(displayedEmojis, key = { it }) { emoji ->
                            EmojiGridItem(
                                emoji = emoji,
                                onClick = { onEmojiClicked(emoji) }
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            // Category Selector Bar (or Search Result Count Pill)
            if (isSearchActive && searchQuery.isNotBlank()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${displayedEmojis.size} emoji ditemukan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Bilingual ID/EN",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                EmojiCategoryBar(
                    selectedCategory = selectedCategory,
                    categories = categories,
                    onCategorySelected = { cat ->
                        isSearchActive = false
                        searchQuery = ""
                        onCategorySelected(cat)
                    }
                )
            }
        }
    }
}

@Composable
fun EmojiHeaderToolbar(
    selectedCategory: EmojiCategoryType,
    isSearchActive: Boolean = false,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onToggleSearch: () -> Unit = {},
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteClicked: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClicked,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.back),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        if (isSearchActive) {
            // Interactive Search TextField
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Cari emoji (ID/EN)...",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                                )
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Hapus pencarian",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.emojis),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = selectedCategory.displayName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onToggleSearch,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari emoji",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        IconButton(
            onClick = onDeleteClicked,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_keyboard_backspace),
                contentDescription = stringResource(id = R.string.keycode_delete),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun EmojiGridItem(
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayEmoji = remember(emoji) {
        if (EmojiCompat.isConfigured()) {
            try {
                EmojiCompat.get().process(emoji).toString()
            } catch (e: Exception) {
                emoji
            }
        } else {
            emoji
        }
    }

    Box(
        modifier = modifier
            .size(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayEmoji,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun EmojiCategoryBar(
    selectedCategory: EmojiCategoryType,
    categories: List<EmojiCategory>,
    onCategorySelected: (EmojiCategoryType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(categories) { category ->
            val type = EmojiCategoryType.entries.firstOrNull { it.name == category.name } ?: EmojiCategoryType.GENERAL
            val isSelected = type == selectedCategory

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onCategorySelected(type) },
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    Color.Transparent
                },
                border = if (isSelected) {
                    BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                } else null
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.icon,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun EmojiKeyboardScreenPreviewLight() {
    FrogoLibKeyboardTheme(darkTheme = false) {
        EmojiKeyboardScreen(
            emojis = listOf("😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇", "🙂", "🙃", "😉", "😍"),
            selectedCategory = EmojiCategoryType.SMILEYS_EMOTION,
            categories = getEmojiCategory(),
            onCategorySelected = {},
            onEmojiClicked = {},
            onBackClicked = {}
        )
    }
}

@Preview(name = "Dark Mode", showBackground = true)
@Composable
private fun EmojiKeyboardScreenPreviewDark() {
    FrogoLibKeyboardTheme(darkTheme = true) {
        EmojiKeyboardScreen(
            emojis = listOf("😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇", "🙂", "🙃", "😉", "😍"),
            selectedCategory = EmojiCategoryType.SMILEYS_EMOTION,
            categories = getEmojiCategory(),
            onCategorySelected = {},
            onEmojiClicked = {},
            onBackClicked = {}
        )
    }
}
