package com.frogobox.appkeyboard.ui.toggle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogobox.appkeyboard.R
import com.frogobox.appkeyboard.model.KeyboardFeatureModel
import com.frogobox.appkeyboard.ui.theme.compose.FrogoKeyboardTheme
import com.frogobox.appkeyboard.ui.theme.compose.FrogoStatusSuccess
import com.frogobox.appkeyboard.ui.theme.compose.FrogoTopAppBar

enum class ToggleFilterTab {
    ALL,
    ACTIVE,
    DISABLED
}

@Composable
fun ToggleScreen(
    features: List<KeyboardFeatureModel>,
    getToggleState: (String) -> Boolean,
    onToggleChanged: (String, Boolean) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(ToggleFilterTab.ALL) }

    val activeCount = features.count { getToggleState(it.id) }
    val disabledCount = features.size - activeCount

    val filteredFeatures = when (selectedTab) {
        ToggleFilterTab.ALL -> features
        ToggleFilterTab.ACTIVE -> features.filter { getToggleState(it.id) }
        ToggleFilterTab.DISABLED -> features.filter { !getToggleState(it.id) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            FrogoTopAppBar(
                title = "Toggle Feature",
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
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header summary banner
            item(span = { GridItemSpan(maxLineSpan) }) {
                ToggleHeaderBanner(
                    totalCount = features.size,
                    activeCount = activeCount,
                    onEnableAll = {
                        features.forEach { onToggleChanged(it.id, true) }
                    },
                    onDisableAll = {
                        features.forEach { onToggleChanged(it.id, false) }
                    }
                )
            }

            // Quick Filter Chips
            item(span = { GridItemSpan(maxLineSpan) }) {
                ToggleFilterRow(
                    selectedTab = selectedTab,
                    totalCount = features.size,
                    activeCount = activeCount,
                    disabledCount = disabledCount,
                    onTabSelected = { selectedTab = it }
                )
            }

            if (filteredFeatures.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (selectedTab == ToggleFilterTab.ACTIVE) {
                                    "No active features found"
                                } else {
                                    "No disabled features found"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { selectedTab = ToggleFilterTab.ALL }) {
                                Text(
                                    text = "Show All Features",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            } else {
                items(filteredFeatures, key = { it.id }) { feature ->
                    ToggleFeatureCard(
                        feature = feature,
                        isChecked = getToggleState(feature.id),
                        onCheckedChange = { isChecked ->
                            onToggleChanged(feature.id, isChecked)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ToggleHeaderBanner(
    totalCount: Int,
    activeCount: Int,
    onEnableAll: () -> Unit,
    onDisableAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Keyboard Toolbar",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Manage shortcuts displayed on your keyboard",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "$activeCount/$totalCount Active",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onEnableAll,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Enable All",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                TextButton(
                    onClick = onDisableAll,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Disable All",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ToggleFilterRow(
    selectedTab: ToggleFilterTab,
    totalCount: Int,
    activeCount: Int,
    disabledCount: Int,
    onTabSelected: (ToggleFilterTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToggleFilterChip(
            text = "All ($totalCount)",
            isSelected = selectedTab == ToggleFilterTab.ALL,
            onClick = { onTabSelected(ToggleFilterTab.ALL) }
        )

        ToggleFilterChip(
            text = "Active ($activeCount)",
            isSelected = selectedTab == ToggleFilterTab.ACTIVE,
            onClick = { onTabSelected(ToggleFilterTab.ACTIVE) }
        )

        ToggleFilterChip(
            text = "Disabled ($disabledCount)",
            isSelected = selectedTab == ToggleFilterTab.DISABLED,
            onClick = { onTabSelected(ToggleFilterTab.DISABLED) }
        )
    }
}

@Composable
private fun ToggleFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            }
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp
            ),
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun ToggleFeatureCard(
    feature: KeyboardFeatureModel,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onCheckedChange(!isChecked) },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isChecked) {
                primaryColor.copy(alpha = 0.45f)
            } else {
                outlineVariant.copy(alpha = 0.35f)
            }
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isChecked) {
                primaryColor.copy(alpha = 0.08f)
            } else {
                surfaceColor
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top row with Icon Container and Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isChecked) {
                                primaryColor.copy(alpha = 0.15f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = feature.icon),
                        contentDescription = feature.text,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Switch(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = primaryColor,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        uncheckedBorderColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = feature.text,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = if (isChecked) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(
                            if (isChecked) {
                                FrogoStatusSuccess
                            } else {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            }
                        )
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = if (isChecked) "Active" else "Disabled",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 11.sp
                    ),
                    color = if (isChecked) {
                        FrogoStatusSuccess
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    }
                )
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun ToggleScreenLightPreview() {
    FrogoKeyboardTheme(darkTheme = false) {
        ToggleScreen(
            features = listOf(
                KeyboardFeatureModel(
                    id = "auto_text",
                    text = "Auto Text",
                    icon = R.drawable.ic_menu_auto_text
                ),
                KeyboardFeatureModel(
                    id = "app_review",
                    text = "App Review",
                    icon = R.drawable.ic_menu_ps_app
                ),
                KeyboardFeatureModel(
                    id = "game_review",
                    text = "Game Review",
                    icon = R.drawable.ic_menu_ps_game
                ),
                KeyboardFeatureModel(
                    id = "love_emoji",
                    text = "Love Emoji",
                    icon = R.drawable.ic_menu_ps_love
                )
            ),
            getToggleState = { it == "auto_text" || it == "game_review" },
            onToggleChanged = { _, _ -> },
            onBackClick = {}
        )
    }
}

@Preview(name = "Dark Mode", showBackground = true)
@Composable
fun ToggleScreenDarkPreview() {
    FrogoKeyboardTheme(darkTheme = true) {
        ToggleScreen(
            features = listOf(
                KeyboardFeatureModel(
                    id = "auto_text",
                    text = "Auto Text",
                    icon = R.drawable.ic_menu_auto_text
                ),
                KeyboardFeatureModel(
                    id = "app_review",
                    text = "App Review",
                    icon = R.drawable.ic_menu_ps_app
                ),
                KeyboardFeatureModel(
                    id = "game_review",
                    text = "Game Review",
                    icon = R.drawable.ic_menu_ps_game
                ),
                KeyboardFeatureModel(
                    id = "love_emoji",
                    text = "Love Emoji",
                    icon = R.drawable.ic_menu_ps_love
                )
            ),
            getToggleState = { it == "auto_text" || it == "game_review" },
            onToggleChanged = { _, _ -> },
            onBackClick = {}
        )
    }
}
