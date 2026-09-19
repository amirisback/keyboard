package com.frogobox.appkeyboard.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogobox.appkeyboard.R
import com.frogobox.appkeyboard.model.KeyboardThemeModel
import com.frogobox.appkeyboard.model.ThemeType
import com.frogobox.appkeyboard.ui.theme.compose.FrogoKeyboardTheme
import com.frogobox.appkeyboard.ui.theme.compose.FrogoPrimary
import com.frogobox.appkeyboard.ui.theme.compose.FrogoTopAppBar

@Composable
fun ThemeScreen(
    themeList: List<KeyboardThemeModel>,
    checkIsActive: (KeyboardThemeModel) -> Boolean,
    onApplyTheme: (KeyboardThemeModel) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedThemeForDialog by remember { mutableStateOf<KeyboardThemeModel?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            FrogoTopAppBar(
                title = "Setup Theme",
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(themeList, key = { it.name }) { theme ->
                ThemeCard(
                    theme = theme,
                    isSelected = checkIsActive(theme),
                    onClick = {
                        selectedThemeForDialog = theme
                    }
                )
            }
        }
    }

    selectedThemeForDialog?.let { theme ->
        AlertDialog(
            onDismissRequest = { selectedThemeForDialog = null },
            title = {
                Text(
                    text = "Change Theme",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text("Are you sure you want to change keyboard theme to ${theme.name}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val current = theme
                        selectedThemeForDialog = null
                        onApplyTheme(current)
                    }
                ) {
                    Text(
                        text = "Apply",
                        color = FrogoPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { selectedThemeForDialog = null }
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

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

@Composable
private fun ThemeCard(
    theme: KeyboardThemeModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Thumbnail Preview based on ThemeType
            when (theme.themType) {
                ThemeType.COLOR -> {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(colorResource(id = theme.background))
                            .border(
                                width = 1.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )
                }
                ThemeType.IMAGE -> {
                    Image(
                        painter = painterResource(id = theme.background),
                        contentDescription = theme.name,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .border(
                                width = 1.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                shape = CircleShape
                            ),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = theme.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = theme.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isSelected) {
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Active Theme",
                    tint = FrogoPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeScreenPreview() {
    FrogoKeyboardTheme(darkTheme = false) {
        ThemeScreen(
            themeList = listOf(
                KeyboardThemeModel(
                    name = "Default",
                    themType = ThemeType.COLOR,
                    background = R.color.color_bg_keyboard_default,
                    description = "Default standard keyboard theme"
                ),
                KeyboardThemeModel(
                    name = "Wallpaper",
                    themType = ThemeType.IMAGE,
                    background = R.drawable.ic_wallpaper_dummy,
                    description = "Custom image wallpaper"
                )
            ),
            checkIsActive = { it.themType == ThemeType.COLOR },
            onApplyTheme = {},
            onBackClick = {}
        )
    }
}
