package com.frogobox.appkeyboard.ui.test

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.ui.theme.compose.FrogoKeyboardTheme
import com.frogobox.appkeyboard.ui.theme.compose.FrogoPrimary
import com.frogobox.appkeyboard.ui.theme.compose.FrogoStatusSuccess
import com.frogobox.appkeyboard.ui.theme.compose.FrogoStatusWarning
import com.frogobox.appkeyboard.ui.theme.compose.FrogoTopAppBar

/**
 * Modern Material 3 Keyboard Testing Playground & Sandbox
 * Redesigned for TASK-010 by Tim Mobile
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    sandboxText: String,
    metrics: TypingMetrics,
    activeTab: Int,
    autoTextList: List<AutoTextEntity>,
    isKeyboardActive: Boolean,
    dummyOptions: List<String> = emptyList(),
    onTextChanged: (String) -> Unit,
    onInsertText: (String) -> Unit,
    onClearText: () -> Unit,
    onResetTimer: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onChangeKeyboard: () -> Unit,
    onGoToSettings: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Local states for specialized input tab
    var searchInput by remember { mutableStateOf("") }
    var numberInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var autoDropdownText by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val filteredDropdownOptions = remember(autoDropdownText, dummyOptions) {
        if (autoDropdownText.isBlank()) {
            dummyOptions
        } else {
            dummyOptions.filter { it.contains(autoDropdownText, ignoreCase = true) }
        }
    }

    FrogoKeyboardTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                FrogoTopAppBar(
                    title = "Playground Uji Coba",
                    onBackClick = onBackClick
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Active Keyboard Status Card
                KeyboardStatusCard(
                    isFrogoActive = isKeyboardActive,
                    onChangeKeyboard = onChangeKeyboard,
                    onGoToSettings = onGoToSettings
                )

                // 2. Real-time Live Typing Metrics Card
                LiveMetricsCard(metrics = metrics)

                // 3. Tab Navigation Row (3 Tabs)
                PrimaryTabRow(
                    selectedTabIndex = activeTab.coerceIn(0, 2),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = FrogoPrimary
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { onTabSelected(0) },
                        text = { Text("Sandbox", fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp)) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { onTabSelected(1) },
                        text = { Text("Input Khusus", fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.Keyboard, contentDescription = null, modifier = Modifier.size(20.dp)) }
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { onTabSelected(2) },
                        text = { Text("AutoText", fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, modifier = Modifier.size(20.dp)) }
                    )
                }

                // 4. Tab Body Content
                when (activeTab) {
                    0 -> SandboxTabContent(
                        text = sandboxText,
                        onTextChanged = onTextChanged,
                        onInsertText = onInsertText,
                        onClearText = onClearText,
                        onResetTimer = onResetTimer,
                        onCopyText = {
                            if (sandboxText.isNotEmpty()) {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Sandbox Text", sandboxText))
                                Toast.makeText(context, "Teks disalin ke clipboard", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    1 -> SpecializedInputsTabContent(
                        searchInput = searchInput,
                        onSearchChange = { searchInput = it },
                        numberInput = numberInput,
                        onNumberChange = { numberInput = it },
                        phoneInput = phoneInput,
                        onPhoneChange = { phoneInput = it },
                        passwordInput = passwordInput,
                        onPasswordChange = { passwordInput = it },
                        passwordVisible = passwordVisible,
                        onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                        autoDropdownText = autoDropdownText,
                        onDropdownTextChange = { autoDropdownText = it },
                        dropdownExpanded = dropdownExpanded,
                        onDropdownExpandedChange = { dropdownExpanded = it },
                        dropdownOptions = filteredDropdownOptions
                    )
                    2 -> AutoTextTabContent(
                        snippets = autoTextList,
                        onSnippetClicked = { snippet ->
                            onInsertText(snippet.body)
                            Toast.makeText(context, "Disisipkan '${snippet.title}' ke sandbox", Toast.LENGTH_SHORT).show()
                            onTabSelected(0)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * 1. Active Keyboard Status Card Component
 */
@Composable
private fun KeyboardStatusCard(
    isFrogoActive: Boolean,
    onChangeKeyboard: () -> Unit,
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = if (isFrogoActive) FrogoStatusSuccess else FrogoStatusWarning
    val statusTitle = if (isFrogoActive) "Frogo Keyboard Aktif" else "Keyboard Lain Aktif"
    val statusDesc = if (isFrogoActive) {
        "Frogo Keyboard aktif sebagai input default. Anda sedang menguji tata letak asli."
    } else {
        "Metode input lain sedang aktif. Ganti untuk menguji fitur Frogo Keyboard."
    }
    val icon = if (isFrogoActive) Icons.Default.CheckCircle else Icons.Default.Warning

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = statusTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = statusDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onChangeKeyboard,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FrogoPrimary)
                ) {
                    Icon(Icons.Default.Keyboard, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ganti Keyboard", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onGoToSettings,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pengaturan", fontSize = 13.sp)
                }
            }
        }
    }
}

/**
 * 2. Real-time Live Typing Metrics Card Component
 */
@Composable
private fun LiveMetricsCard(
    metrics: TypingMetrics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Typing Analytics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${metrics.elapsedSeconds}s elapsed",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(label = "Characters", value = "${metrics.characterCount}", modifier = Modifier.weight(1f))
                MetricItem(label = "Words", value = "${metrics.wordCount}", modifier = Modifier.weight(1f))
                MetricItem(label = "Lines", value = "${metrics.lineCount}", modifier = Modifier.weight(1f))
                MetricItem(
                    label = "Typing Speed",
                    value = "${metrics.wordsPerMinute} WPM",
                    highlight = true,
                    modifier = Modifier.weight(1.2f)
                )
            }
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    highlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = if (highlight) FrogoPrimary else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 3. Tab 0: Freeform Sandbox & Preset Pangrams
 */
@Composable
private fun SandboxTabContent(
    text: String,
    onTextChanged: (String) -> Unit,
    onInsertText: (String) -> Unit,
    onClearText: () -> Unit,
    onResetTimer: () -> Unit,
    onCopyText: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pangramsScroll = rememberScrollState()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Preset Pangrams horizontal chips
        Text(
            text = "Quick Presets & Pangrams",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(pangramsScroll),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                onClick = { onInsertText("The quick brown fox jumps over the lazy dog") },
                label = { Text("English Pangram") }
            )
            AssistChip(
                onClick = { onInsertText("Keluarga Jefri bernomor fax tujuh belas") },
                label = { Text("Indonesian Pangram") }
            )
            AssistChip(
                onClick = { onInsertText("1234567890 !@#$%^&*()_+") },
                label = { Text("Numbers & Symbols") }
            )
            AssistChip(
                onClick = { onInsertText("😀🚀✨🎉🔥👍💯❤️") },
                label = { Text("Emoji Palette") }
            )
        }

        // Multiline Editor Area
        OutlinedTextField(
            value = text,
            onValueChange = onTextChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            placeholder = { Text("Ketik di area ini dengan keyboard Anda untuk menguji metrik pengetikan, kecepatan, dan responsivitas tombol...") },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FrogoPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Action Toolbar: Copy, Clear, Reset Timer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCopyText,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Salin", fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = onResetTimer,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset", fontSize = 13.sp)
            }

            Button(
                onClick = onClearText,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Hapus", fontSize = 13.sp)
            }
        }
    }
}

/**
 * 4. Tab 1: Specialized Input Types (Search, Number, Phone, Password, AutoComplete)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpecializedInputsTabContent(
    searchInput: String,
    onSearchChange: (String) -> Unit,
    numberInput: String,
    onNumberChange: (String) -> Unit,
    phoneInput: String,
    onPhoneChange: (String) -> Unit,
    passwordInput: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    autoDropdownText: String,
    onDropdownTextChange: (String) -> Unit,
    dropdownExpanded: Boolean,
    onDropdownExpandedChange: (Boolean) -> Unit,
    dropdownOptions: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search Input
        OutlinedTextField(
            value = searchInput,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search Input (ImeAction.Search)") },
            placeholder = { Text("Search keywords...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchInput.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            )
        )

        // Number Input
        OutlinedTextField(
            value = numberInput,
            onValueChange = onNumberChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Input Angka (KeyboardType.Number)") },
            placeholder = { Text("12345678") },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )

        // Phone Input
        OutlinedTextField(
            value = phoneInput,
            onValueChange = onPhoneChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Input Telepon (KeyboardType.Phone)") },
            placeholder = { Text("+62 812-3456-7890") },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
        )

        // Password Input
        OutlinedTextField(
            value = passwordInput,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password (Toggle Visibilitas)") },
            placeholder = { Text("Masukkan kata sandi...") },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(icon, contentDescription = if (passwordVisible) "Sembunyikan password" else "Tampilkan password")
                }
            }
        )

        // AutoComplete Dropdown
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = onDropdownExpandedChange,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = autoDropdownText,
                onValueChange = {
                    onDropdownTextChange(it)
                    onDropdownExpandedChange(true)
                },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                    .fillMaxWidth(),
                label = { Text("Dropdown Pencarian") },
                placeholder = { Text("Ketik nama kota...") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                shape = RoundedCornerShape(8.dp),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            if (dropdownOptions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { onDropdownExpandedChange(false) }
                ) {
                    dropdownOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onDropdownTextChange(option)
                                onDropdownExpandedChange(false)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }
}

/**
 * 5. Tab 2: AutoText Snippets Showcase (Tap to Insert)
 */
@Composable
private fun AutoTextTabContent(
    snippets: List<AutoTextEntity>,
    onSnippetClicked: (AutoTextEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Sentuh salah satu template teks untuk menyisipkannya ke sandbox:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        snippets.forEach { snippet ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSnippetClicked(snippet) },
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
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(FrogoPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Notes,
                            contentDescription = null,
                            tint = FrogoPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = snippet.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = snippet.label.name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = snippet.body,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
