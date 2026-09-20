package com.frogobox.appkeyboard.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogobox.appkeyboard.R
import com.frogobox.appkeyboard.model.KeyboardThemeModel
import com.frogobox.appkeyboard.model.ThemeType
import com.frogobox.appkeyboard.ui.theme.compose.FrogoKeyboardTheme
import com.frogobox.appkeyboard.ui.theme.compose.FrogoPrimary
import com.frogobox.appkeyboard.ui.theme.compose.FrogoStatusSuccess
import com.frogobox.appkeyboard.ui.theme.compose.FrogoTopAppBar

enum class ThemeCategory(val title: String) {
    ALL("All"),
    SOLID("Solid Colors"),
    WALLPAPER("Wallpapers")
}

@Composable
fun ThemeScreen(
    themeList: List<KeyboardThemeModel>,
    checkIsActive: (KeyboardThemeModel) -> Boolean,
    onApplyTheme: (KeyboardThemeModel) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(ThemeCategory.ALL) }
    var previewedTheme by remember { mutableStateOf<KeyboardThemeModel?>(null) }
    var confirmApplyDialogTheme by remember { mutableStateOf<KeyboardThemeModel?>(null) }

    // Synchronize preview with active theme on initial load or change
    LaunchedEffect(themeList) {
        if (previewedTheme == null && themeList.isNotEmpty()) {
            previewedTheme = themeList.firstOrNull { checkIsActive(it) } ?: themeList.first()
        }
    }

    // Filter themes based on selected category chip
    val filteredThemes = remember(themeList, selectedCategory) {
        when (selectedCategory) {
            ThemeCategory.ALL -> themeList
            ThemeCategory.SOLID -> themeList.filter { it.themType == ThemeType.COLOR }
            ThemeCategory.WALLPAPER -> themeList.filter { it.themType == ThemeType.IMAGE }
        }
    }

    val activeTheme = themeList.firstOrNull { checkIsActive(it) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            FrogoTopAppBar(
                title = "Keyboard Themes",
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Live Interactive Keyboard Preview Hero Section (Spans all columns)
            item(span = { GridItemSpan(2) }) {
                val currentPreview = previewedTheme ?: activeTheme ?: themeList.firstOrNull()
                currentPreview?.let { theme ->
                    val isPreviewActive = checkIsActive(theme)
                    ThemePreviewHero(
                        theme = theme,
                        isActive = isPreviewActive,
                        onApplyClick = {
                            confirmApplyDialogTheme = theme
                        }
                    )
                }
            }

            // 2. Category Filter Chips Section (Spans all columns)
            item(span = { GridItemSpan(2) }) {
                ThemeCategoryFilterRow(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    totalAllCount = themeList.size,
                    solidCount = themeList.count { it.themType == ThemeType.COLOR },
                    wallpaperCount = themeList.count { it.themType == ThemeType.IMAGE }
                )
            }

            // 3. Section Title (Spans all columns)
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Theme Gallery (${filteredThemes.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    activeTheme?.let {
                        Text(
                            text = "Active: ${it.name}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = FrogoPrimary
                            )
                        )
                    }
                }
            }

            // 4. 2-Column Responsive Theme Cards
            items(filteredThemes, key = { it.name }) { theme ->
                val isActive = checkIsActive(theme)
                val isPreviewed = previewedTheme?.name == theme.name

                ThemeGridCard(
                    theme = theme,
                    isActive = isActive,
                    isPreviewed = isPreviewed,
                    onCardClick = {
                        previewedTheme = theme
                    },
                    onApplyClick = {
                        confirmApplyDialogTheme = theme
                    }
                )
            }

            // Bottom Spacer for edge-to-edge breathing room
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Confirmation Dialog before applying theme
    confirmApplyDialogTheme?.let { theme ->
        AlertDialog(
            onDismissRequest = { confirmApplyDialogTheme = null },
            icon = {
                Surface(
                    shape = CircleShape,
                    color = FrogoPrimary.copy(alpha = 0.12f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = FrogoPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = "Apply Theme",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "Set \"${theme.name}\" as your active keyboard theme? All keyboard keys and panels will adopt this style.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val current = theme
                        confirmApplyDialogTheme = null
                        onApplyTheme(current)
                        previewedTheme = current
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FrogoPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Terapkan Sekarang",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { confirmApplyDialogTheme = null }
                ) {
                    Text("Batal")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/**
 * Backwards-compatible overload matching previous signature
 */
@Composable
fun ThemeScreen(
    themeList: List<KeyboardThemeModel>,
    activeThemeColor: Int,
    onApplyTheme: (KeyboardThemeModel) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ThemeScreen(
        themeList = themeList,
        checkIsActive = { it.background == activeThemeColor },
        onApplyTheme = onApplyTheme,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

/**
 * 1. Live Interactive Keyboard Preview Hero Component
 */
@Composable
private fun ThemePreviewHero(
    theme: KeyboardThemeModel,
    isActive: Boolean,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isActive) 1.5.dp else 1.dp,
            color = if (isActive) FrogoPrimary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Theme Label + Status / Apply Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = FrogoPrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = FrogoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = theme.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = if (theme.themType == ThemeType.COLOR) "Color" else "Wallpaper",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = theme.description,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isActive) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FrogoStatusSuccess.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, FrogoStatusSuccess.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Aktif",
                                tint = FrogoStatusSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Aktif",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FrogoStatusSuccess,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onApplyClick,
                        colors = ButtonDefaults.buttonColors(containerColor = FrogoPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = "Terapkan",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Realistic Keyboard Mockup Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(14.dp)
                    )
            ) {
                // Background Layer (Color or Wallpaper)
                when (theme.themType) {
                    ThemeType.COLOR -> {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(colorResource(id = theme.background))
                        )
                    }
                    ThemeType.IMAGE -> {
                        Image(
                            painter = painterResource(id = theme.background),
                            contentDescription = theme.name,
                            modifier = Modifier.matchParentSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Keyboard Layout Overlay (Utility Strip + Keycaps)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Top Suggestion & Toolbar Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MockToolIcon(Icons.Default.SentimentSatisfied)
                            MockToolIcon(Icons.Default.TextFields)
                            MockToolIcon(Icons.Default.Language)
                            MockToolIcon(Icons.Default.Settings)
                        }

                        Text(
                            text = "Frogo Keyboard",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        )
                    }

                    // Keycap Row 1: Q W E R T Y U I O P (10 keys)
                    MockKeyRow(keys = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"))

                    // Keycap Row 2: A S D F G H J K L (9 keys with inset margins)
                    MockKeyRow(
                        keys = listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
                        horizontalPadding = 12.dp
                    )

                    // Keycap Row 3: Shift, Z X C V B N M, Backspace
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MockTextKey(
                            text = "⇧",
                            weight = 1.3f
                        )
                        listOf("Z", "X", "C", "V", "B", "N", "M").forEach { letter ->
                            MockKey(
                                text = letter,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        MockSpecialKey(
                            icon = Icons.AutoMirrored.Filled.Backspace,
                            weight = 1.3f
                        )
                    }

                    // Keycap Row 4: ?123, Language, Spacebar, Enter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MockTextKey(text = "?123", weight = 1.4f)
                        MockIconKey(icon = Icons.Default.Language, weight = 1f)
                        MockSpacebarKey(text = "English", weight = 4.2f)
                        MockActionKey(icon = Icons.AutoMirrored.Filled.KeyboardReturn, weight = 1.6f)
                    }
                }
            }
        }
    }
}

/**
 * 2. Category Filter Chips Row Component
 */
@Composable
private fun ThemeCategoryFilterRow(
    selectedCategory: ThemeCategory,
    onCategorySelected: (ThemeCategory) -> Unit,
    totalAllCount: Int,
    solidCount: Int,
    wallpaperCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ThemeCategory.entries.forEach { category ->
            val count = when (category) {
                ThemeCategory.ALL -> totalAllCount
                ThemeCategory.SOLID -> solidCount
                ThemeCategory.WALLPAPER -> wallpaperCount
            }
            val isSelected = selectedCategory == category

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = "${category.title} ($count)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                },
                leadingIcon = {
                    when (category) {
                        ThemeCategory.ALL -> Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        ThemeCategory.SOLID -> Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        ThemeCategory.WALLPAPER -> Icon(
                            imageVector = Icons.Default.SentimentSatisfied,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = FrogoPrimary.copy(alpha = 0.15f),
                    selectedLabelColor = FrogoPrimary,
                    selectedLeadingIconColor = FrogoPrimary
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

/**
 * 3. 2-Column Visual Theme Card Component
 */
@Composable
private fun ThemeGridCard(
    theme: KeyboardThemeModel,
    isActive: Boolean,
    isPreviewed: Boolean,
    onCardClick: () -> Unit,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            isActive -> FrogoPrimary
            isPreviewed -> FrogoPrimary.copy(alpha = 0.6f)
            else -> MaterialTheme.colorScheme.outlineVariant
        },
        animationSpec = tween(durationMillis = 200),
        label = "cardBorderColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCardClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isActive || isPreviewed) 1.5.dp else 1.dp,
            color = borderColor
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Preview Swatch / Wallpaper Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
            ) {
                when (theme.themType) {
                    ThemeType.COLOR -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colorResource(id = theme.background))
                        )
                    }
                    ThemeType.IMAGE -> {
                        Image(
                            painter = painterResource(id = theme.background),
                            contentDescription = theme.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Mini Keycap Silhouette Overlay for Authentic Keyboard Feel
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        repeat(10) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color.White.copy(alpha = 0.28f))
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        repeat(9) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color.White.copy(alpha = 0.28f))
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White.copy(alpha = 0.35f))
                        )
                        repeat(7) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color.White.copy(alpha = 0.28f))
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White.copy(alpha = 0.35f))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White.copy(alpha = 0.28f))
                        )
                        Box(
                            modifier = Modifier
                                .weight(4.5f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White.copy(alpha = 0.38f))
                        )
                        Box(
                            modifier = Modifier
                                .weight(2f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(FrogoPrimary.copy(alpha = 0.75f))
                        )
                    }
                }

                // Floating Active Badge (Top Right)
                if (isActive) {
                    Surface(
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        color = FrogoStatusSuccess,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Aktif",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Aktif",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color.White
                                )
                            )
                        }
                    }
                } else if (isPreviewed) {
                    Surface(
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        color = FrogoPrimary,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "Preview",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Bottom Info Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = theme.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = theme.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// --------------------------------------------------------------------------------
// KEYBOARD MOCKUP HELPER COMPOSABLES
// --------------------------------------------------------------------------------

@Composable
private fun MockToolIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.25f),
        modifier = modifier.size(20.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
private fun MockKeyRow(
    keys: List<String>,
    horizontalPadding: androidx.compose.ui.unit.Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        keys.forEach { letter ->
            MockKey(
                text = letter,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MockKey(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(5.dp),
        color = Color.White.copy(alpha = 0.22f),
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.35f)),
        modifier = modifier.height(26.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
private fun MockSpecialKey(
    icon: ImageVector,
    weight: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(5.dp),
        color = Color.Black.copy(alpha = 0.25f),
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.3f)),
        modifier = modifier
            .height(26.dp)
            .then(Modifier.run { this })
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun MockTextKey(
    text: String,
    weight: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(5.dp),
        color = Color.Black.copy(alpha = 0.25f),
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.3f)),
        modifier = modifier.height(26.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 6.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
private fun MockIconKey(
    icon: ImageVector,
    weight: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(5.dp),
        color = Color.Black.copy(alpha = 0.25f),
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.3f)),
        modifier = modifier.height(26.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
private fun MockSpacebarKey(
    text: String,
    weight: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(5.dp),
        color = Color.White.copy(alpha = 0.28f),
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.4f)),
        modifier = modifier.height(26.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            )
        }
    }
}

@Composable
private fun MockActionKey(
    icon: ImageVector,
    weight: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(5.dp),
        color = FrogoPrimary,
        modifier = modifier.height(26.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "ThemeScreen Light Preview")
@Composable
fun ThemeScreenPreview() {
    FrogoKeyboardTheme(darkTheme = false) {
        ThemeScreen(
            themeList = listOf(
                KeyboardThemeModel("Default", "Classic Adaptive", ThemeType.COLOR, R.color.color_bg_keyboard_default),
                KeyboardThemeModel("Frogo Purple", "Signature Brand", ThemeType.COLOR, R.color.color_bg_keyboard_purple),
                KeyboardThemeModel("Midnight AMOLED", "Deep OLED Black", ThemeType.COLOR, R.color.color_bg_keyboard_dark),
                KeyboardThemeModel("Ocean Blue", "Calm & Focused", ThemeType.COLOR, R.color.color_bg_keyboard_blue),
                KeyboardThemeModel("Forest Emerald", "Natural Harmony", ThemeType.COLOR, R.color.color_bg_keyboard_green),
                KeyboardThemeModel("Crimson Sunset", "Vibrant Warmth", ThemeType.COLOR, R.color.color_bg_keyboard_red),
                KeyboardThemeModel("Sunset Orange", "Energetic Twilight", ThemeType.COLOR, R.color.color_bg_keyboard_orange),
                KeyboardThemeModel("Nordic Cyan", "Fresh & Clean", ThemeType.COLOR, R.color.color_bg_keyboard_cyan),
                KeyboardThemeModel("Sakura Pink", "Aesthetic Pastel", ThemeType.COLOR, R.color.color_bg_keyboard_pink),
                KeyboardThemeModel("Amber Gold", "Golden Accent", ThemeType.COLOR, R.color.color_bg_keyboard_yellow),
                KeyboardThemeModel("Wallpaper", "Sample Artwork", ThemeType.IMAGE, R.drawable.ic_wallpaper_dummy)
            ),
            checkIsActive = { it.name == "Frogo Purple" },
            onApplyTheme = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "ThemeScreen Dark Preview")
@Composable
fun ThemeScreenDarkPreview() {
    FrogoKeyboardTheme(darkTheme = true) {
        ThemeScreen(
            themeList = listOf(
                KeyboardThemeModel("Default", "Classic Adaptive", ThemeType.COLOR, R.color.color_bg_keyboard_default),
                KeyboardThemeModel("Frogo Purple", "Signature Brand", ThemeType.COLOR, R.color.color_bg_keyboard_purple),
                KeyboardThemeModel("Midnight AMOLED", "Deep OLED Black", ThemeType.COLOR, R.color.color_bg_keyboard_dark),
                KeyboardThemeModel("Wallpaper", "Sample Artwork", ThemeType.IMAGE, R.drawable.ic_wallpaper_dummy)
            ),
            checkIsActive = { it.name == "Midnight AMOLED" },
            onApplyTheme = {},
            onBackClick = {}
        )
    }
}
