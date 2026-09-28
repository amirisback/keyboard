package com.frogobox.appkeyboard.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogobox.appkeyboard.ui.theme.compose.FrogoPrimary
import com.frogobox.appkeyboard.ui.theme.compose.FrogoStatusSuccess

/**
 * Interactive 3-Step Setup & Activation Wizard.
 * Guides user through enabling IME, selecting as default, and testing typing with real-time indicators.
 */
@Composable
fun KeyboardSetupWizardSection(
    status: KeyboardStatus,
    onGoToSettings: () -> Unit,
    onChangeKeyboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var testInputText by remember { mutableStateOf("") }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(FrogoPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = null,
                        tint = FrogoPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Panduan Aktivasi Cepat",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Aktifkan keyboard dalam 3 langkah mudah",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(thickness = 0.8.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(14.dp))

            // Step 1: Enable in Settings
            val isStep1Done = status != KeyboardStatus.NOT_ENABLED
            SetupStepItem(
                stepNumber = "1",
                title = "Aktifkan di Pengaturan Sistem",
                description = "Aktifkan Frogo Keyboard di daftar input sistem Android.",
                isDone = isStep1Done,
                actionButtonText = if (isStep1Done) "Sudah Aktif ✓" else "Buka Pengaturan",
                isActionButtonEnabled = !isStep1Done,
                onActionClick = onGoToSettings
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Step 2: Select as Default IME
            val isStep2Done = status == KeyboardStatus.ACTIVE
            SetupStepItem(
                stepNumber = "2",
                title = "Pilih sebagai Keyboard Utama",
                description = "Jadikan Frogo Keyboard sebagai metode input default.",
                isDone = isStep2Done,
                actionButtonText = if (isStep2Done) "Keyboard Utama ✓" else "Pilih Keyboard",
                isActionButtonEnabled = !isStep2Done,
                onActionClick = onChangeKeyboard
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Step 3: Interactive Playground
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isStep2Done) FrogoStatusSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "3",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isStep2Done) FrogoStatusSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Uji Coba Pengetikan Cepat",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = testInputText,
                    onValueChange = { testInputText = it },
                    placeholder = {
                        Text(
                            text = "Ketik di sini untuk menguji Frogo Keyboard...",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
private fun SetupStepItem(
    stepNumber: String,
    title: String,
    description: String,
    isDone: Boolean,
    actionButtonText: String,
    isActionButtonEnabled: Boolean,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isDone) FrogoStatusSuccess else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            } else {
                Text(
                    text = stepNumber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (isActionButtonEnabled) {
            Button(
                onClick = onActionClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FrogoPrimary
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(text = actionButtonText, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
            }
        } else {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = FrogoStatusSuccess.copy(alpha = 0.12f)
            ) {
                Text(
                    text = actionButtonText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FrogoStatusSuccess,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
            }
        }
    }
}
