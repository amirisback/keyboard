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
import androidx.compose.material3.HorizontalDivider
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
        title = "Keyboard Siap Digunakan",
        description = "Frogo Keyboard aktif dan terpilih sebagai metode input utama.",
        badge = "AKTIF",
        color = FrogoStatusSuccess,
        icon = Icons.Default.CheckCircle
    ),
    NOT_DEFAULT(
        title = "Bukan Keyboard Utama",
        description = "Keyboard sudah aktif di sistem, namun belum dipilih sebagai input default.",
        badge = "PILIH FROGO",
        color = FrogoStatusWarning,
        icon = Icons.Default.Warning
    ),
    NOT_ENABLED(
        title = "Keyboard Belum Aktif",
        description = "Aktifkan Frogo Keyboard melalui Pengaturan Sistem Android.",
        badge = "BELUM AKTIF",
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Header Description
            Text(
                text = "Konfigurasi metode input, kelola pintasan teks cepat, suara mekanik, dan personalisasi tema keyboard Anda.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tactile Status Card (Hardware-inspired Precision System Status)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, status.color.copy(alpha = 0.5f)),
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
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(status.color.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = status.icon,
                            contentDescription = null,
                            tint = status.color,
                            modifier = Modifier.size(22.dp)
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
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = status.color.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = status.badge,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = status.color,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = status.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Activation Steps Row (Tactile Hardware Steps)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActivationCard(
                    stepNumber = "1",
                    title = "Aktifkan Keyboard",
                    subtitle = "Pengaturan Sistem",
                    iconRes = R.drawable.ic_menu_setting,
                    onClick = onGoToSettings,
                    modifier = Modifier.weight(1f)
                )

                ActivationCard(
                    stepNumber = "2",
                    title = "Pilih Default",
                    subtitle = "Ganti Input Aktif",
                    iconRes = R.drawable.ic_menu_keyboard,
                    onClick = onChangeKeyboard,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Section Header
            Text(
                text = "Fitur dan Personalisasi",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Feature Menu List (Tactile 1dp Border Cards)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MainMenuItemCard(
                    title = "Auto Text",
                    subtitle = "Template teks cepat dan pintasan ketikan berulang",
                    iconRes = R.drawable.ic_menu_auto_text,
                    onClick = onNavigateAutoText
                )

                MainMenuItemCard(
                    title = "Toggle Fitur",
                    subtitle = "Kustomisasi tombol pintas dan toolbar keyboard",
                    iconRes = R.drawable.ic_menu_form,
                    onClick = onNavigateToggle
                )

                MainMenuItemCard(
                    title = "Suara dan Haptic",
                    subtitle = "Suara switch mechanical keyboard dan feedback getaran",
                    iconRes = R.drawable.ic_menu_sound,
                    onClick = onNavigateSound
                )

                MainMenuItemCard(
                    title = "Bahasa Keyboard",
                    subtitle = "Dukungan multibahasa dan layout QWERTY internasional",
                    iconRes = R.drawable.ic_menu_website,
                    onClick = onNavigateLanguage
                )

                MainMenuItemCard(
                    title = "Tema Keyboard",
                    subtitle = "Pilihan tema warna solid, wallpaper, dan preview interaktif",
                    iconRes = R.drawable.ic_menu_ps_game,
                    onClick = onNavigateTheme
                )

                MainMenuItemCard(
                    title = "Playground Uji Coba",
                    subtitle = "Uji kecepatan ketik (WPM), akurasi, dan sandbox keyboard",
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
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
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
                    modifier = Modifier.size(32.dp)
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = FrogoPrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "LANGKAH $stepNumber",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = FrogoPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
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
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(32.dp)
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
                        fontSize = 12.sp,
                        lineHeight = 16.sp
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
