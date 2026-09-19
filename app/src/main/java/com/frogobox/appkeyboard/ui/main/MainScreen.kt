package com.frogobox.appkeyboard.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogobox.appkeyboard.R
import com.frogobox.appkeyboard.ui.theme.compose.FrogoKeyboardTheme
import com.frogobox.appkeyboard.ui.theme.compose.FrogoPrimary
import com.frogobox.appkeyboard.ui.theme.compose.FrogoStatusFailed
import com.frogobox.appkeyboard.ui.theme.compose.FrogoStatusSuccess
import com.frogobox.appkeyboard.ui.theme.compose.FrogoStatusWarning
import com.frogobox.appkeyboard.ui.theme.compose.FrogoTopAppBar

enum class KeyboardStatus(
    val title: String,
    val description: String,
    val badge: String,
    val color: Color,
    val icon: ImageVector
) {
    ACTIVE(
        title = "Frogo Keyboard Active",
        description = "Keyboard is activated and set as default input method.",
        badge = "ACTIVE",
        color = FrogoStatusSuccess,
        icon = Icons.Default.CheckCircle
    ),
    NOT_DEFAULT(
        title = "Not Default Keyboard",
        description = "Keyboard is enabled, but another keyboard is currently selected.",
        badge = "SELECT FROGO",
        color = FrogoStatusWarning,
        icon = Icons.Default.Warning
    ),
    NOT_ENABLED(
        title = "Frogo Keyboard Inactive",
        description = "Enable Frogo Keyboard in Android system settings to get started.",
        badge = "NOT ENABLED",
        color = FrogoStatusFailed,
        icon = Icons.Default.Info
    )
}

@Composable
fun MainScreen(
    status: KeyboardStatus,
    onGoToSettings: () -> Unit,
    onChangeKeyboard: () -> Unit,
    onNavigateAutoText: () -> Unit,
    onNavigateToggle: () -> Unit,
    onNavigateLanguage: () -> Unit,
    onNavigateTheme: () -> Unit,
    onNavigateSound: () -> Unit,
    onNavigateTest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            FrogoTopAppBar(
                title = "Frogo Keyboard"
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Header Description
            Text(
                text = "Enable and set as default keyboard to start typing with custom features and personalized tools.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Reactive Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = status.color.copy(alpha = 0.12f)
                ),
                border = BorderStroke(1.dp, status.color.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(status.color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = status.icon,
                            contentDescription = null,
                            tint = status.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = status.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = status.color
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = status.color.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = status.badge,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = status.color,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = status.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Activation Steps Row (Step 1: Settings, Step 2: Switch Keyboard)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActivationCard(
                    stepNumber = "1",
                    title = "Enable Keyboard",
                    subtitle = "System Settings",
                    iconRes = R.drawable.ic_menu_setting,
                    onClick = onGoToSettings,
                    modifier = Modifier.weight(1f)
                )

                ActivationCard(
                    stepNumber = "2",
                    title = "Set Default",
                    subtitle = "Switch Keyboard",
                    iconRes = R.drawable.ic_menu_keyboard,
                    onClick = onChangeKeyboard,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section Header
            Text(
                text = "Features & Customization",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Feature Menu List (Material 3 Cards with Icon, Subtitle, and Chevron)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MainMenuItemCard(
                    title = "Auto Text",
                    subtitle = "Quick snippets & repetitive text templates",
                    iconRes = R.drawable.ic_menu_auto_text,
                    onClick = onNavigateAutoText
                )

                MainMenuItemCard(
                    title = "Toggle Function",
                    subtitle = "Keyboard shortcuts & toolbar switches",
                    iconRes = R.drawable.ic_menu_form,
                    onClick = onNavigateToggle
                )

                MainMenuItemCard(
                    title = "Sound & Haptics",
                    subtitle = "Mechanical switch sounds & key vibration",
                    iconRes = R.drawable.ic_menu_sound,
                    onClick = onNavigateSound
                )

                MainMenuItemCard(
                    title = "Keyboard Language",
                    subtitle = "Multilingual input languages & layouts",
                    iconRes = R.drawable.ic_menu_website,
                    onClick = onNavigateLanguage
                )

                MainMenuItemCard(
                    title = "Keyboard Theme",
                    subtitle = "Personalized colors, themes & wallpapers",
                    iconRes = R.drawable.ic_menu_ps_game,
                    onClick = onNavigateTheme
                )

                MainMenuItemCard(
                    title = "Do Some Test",
                    subtitle = "Interactive typing test & keyboard sandbox",
                    iconRes = R.drawable.ic_menu_keyboard,
                    onClick = onNavigateTest
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ActivationCard(
    stepNumber: String,
    title: String,
    subtitle: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(36.dp)
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = FrogoPrimary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "STEP $stepNumber",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = FrogoPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MainMenuItemCard(
    title: String,
    subtitle: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(name = "Light Mode - Active", showBackground = true)
@Composable
fun MainScreenActivePreview() {
    FrogoKeyboardTheme(darkTheme = false) {
        MainScreen(
            status = KeyboardStatus.ACTIVE,
            onGoToSettings = {},
            onChangeKeyboard = {},
            onNavigateAutoText = {},
            onNavigateToggle = {},
            onNavigateLanguage = {},
            onNavigateTheme = {},
            onNavigateSound = {},
            onNavigateTest = {}
        )
    }
}

@Preview(name = "Dark Mode - Inactive", showBackground = true)
@Composable
fun MainScreenInactiveDarkPreview() {
    FrogoKeyboardTheme(darkTheme = true) {
        MainScreen(
            status = KeyboardStatus.NOT_ENABLED,
            onGoToSettings = {},
            onChangeKeyboard = {},
            onNavigateAutoText = {},
            onNavigateToggle = {},
            onNavigateLanguage = {},
            onNavigateTheme = {},
            onNavigateSound = {},
            onNavigateTest = {}
        )
    }
}
