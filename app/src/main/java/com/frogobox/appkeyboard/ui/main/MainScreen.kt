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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
    onNavigateTemplateText: (String?) -> Unit,
    onNavigateProductRemote: () -> Unit,
    onNavigateToggle: () -> Unit,
    onNavigateLanguage: () -> Unit,
    onNavigateTheme: () -> Unit,
    onNavigateSound: () -> Unit,
    onNavigateTest: () -> Unit,
    onExportBackup: () -> Unit = {},
    onImportBackup: () -> Unit = {},
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

            // Interactive 3-Step Setup Wizard
            KeyboardSetupWizardSection(
                status = status,
                onGoToSettings = onGoToSettings,
                onChangeKeyboard = onChangeKeyboard
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section Header: Fitur dan Personalisasi
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
                    title = "Product Remote",
                    subtitle = "Katalog data produk remote dari API server",
                    iconRes = R.drawable.ic_menu_ps_sale,
                    onClick = onNavigateProductRemote
                )

                MainMenuItemCard(
                    title = "Auto Text",
                    subtitle = "Template teks cepat dan pintasan ketikan berulang",
                    iconRes = R.drawable.ic_menu_auto_text,
                    onClick = onNavigateAutoText
                )

                TemplateTextMenuItemCard(
                    title = "Template Teks",
                    subtitle = "Kelola template cepat Game, App, Sale, Greeting, dan Love",
                    iconRes = R.drawable.ic_menu_ps_game,
                    onClick = { onNavigateTemplateText(null) },
                    onCategoryClick = { cat -> onNavigateTemplateText(cat) }
                )

                MainMenuItemCard(
                    title = "Toggle Fitur",
                    subtitle = "Kustomisasi tombol pintas dan toolbar keyboard",
                    iconRes = R.drawable.ic_menu_setting,
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
                    iconRes = R.drawable.ic_menu_language,
                    onClick = onNavigateLanguage
                )

                MainMenuItemCard(
                    title = "Tema Keyboard",
                    subtitle = "Pilihan tema warna solid, wallpaper, dan preview interaktif",
                    iconRes = R.drawable.ic_menu_theme,
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

            // Section Header: Pencadangan dan Pemulihan (Bottom section as requested)
            Text(
                text = "Pencadangan dan Pemulihan",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            HomeBackupRestoreCard(
                onExportBackup = onExportBackup,
                onImportBackup = onImportBackup
            )

            Spacer(modifier = Modifier.height(32.dp))
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
    iconRes: Int? = null,
    iconVector: ImageVector? = null,
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                if (iconRes != null) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = title,
                        modifier = Modifier.size(24.dp)
                    )
                } else if (iconVector != null) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = title,
                        tint = FrogoPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

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

@Composable
private fun TemplateTextMenuItemCard(
    title: String,
    subtitle: String,
    iconRes: Int,
    onClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = title,
                        modifier = Modifier.size(24.dp)
                    )
                }

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

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Category Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Pair("🎮", "Game"),
                    Pair("📱", "App"),
                    Pair("💰", "Sale"),
                    Pair("👋", "Greeting"),
                    Pair("❤️", "Love")
                ).forEach { (icon, catName) ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onCategoryClick(catName.uppercase()) }
                    ) {
                        Text(
                            text = "$icon $catName",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeBackupRestoreCard(
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(FrogoPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = FrogoPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cadangkan Data (JSON)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Ekspor atau impor template teks dan data keyboard Anda dalam format JSON.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onExportBackup,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ekspor JSON",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                OutlinedButton(
                    onClick = onImportBackup,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Impor JSON",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
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
            onNavigateTemplateText = {},
            onNavigateProductRemote = {},
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
            onNavigateTemplateText = {},
            onNavigateProductRemote = {},
            onNavigateToggle = {},
            onNavigateLanguage = {},
            onNavigateTheme = {},
            onNavigateSound = {},
            onNavigateTest = {}
        )
    }
}
