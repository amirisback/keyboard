package com.frogobox.appkeyboard.ui.keyboard.productremote

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import com.frogobox.appkeyboard.ui.keyboard.common.AsyncGlideImage
import com.frogobox.appkeyboard.ui.keyboard.common.KeyboardFeatureToolbar
import com.frogobox.appkeyboard.ui.theme.compose.FrogoEmptyView
import com.frogobox.appkeyboard.ui.theme.compose.FrogoStatusFailed

/**
 * Modern Jetpack Compose screen for Product Remote keyboard panel.
 * Complies with Material 3, Anti-Slop principles, and 540.dp (2x enlarged) IME height constraints.
 */
@Composable
fun ProductRemoteKeyboardScreen(
    items: List<DataItemResponse>,
    isLoading: Boolean,
    errorMessage: String?,
    onCommitText: (String) -> Unit,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onManageClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // 1. Fixed Height Toolbar Header (50.dp)
        KeyboardFeatureToolbar(
            title = "Product Remote",
            subtitle = "Katalog produk tersimpan di Room DB",
            onBackClick = onBackClick,
            action = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (onManageClick != null) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable { onManageClick() }
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = "Buka Katalog",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable(enabled = !isLoading) { onRefresh() }
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh product remote data",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        )

        var selectedOutputMode by rememberSaveable { mutableStateOf(ProductRemoteOutputMode.CAPTION) }

        // 2. Output Mode Selector Bar (Caption vs Judul Produk)
        ProductRemoteModeSelector(
            selectedMode = selectedOutputMode,
            onModeSelected = { selectedOutputMode = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )

        // 3. Animated Content Viewport
        Crossfade(
            targetState = when {
                isLoading && items.isEmpty() -> ProductKeyboardState.LOADING
                errorMessage != null && items.isEmpty() -> ProductKeyboardState.ERROR
                items.isEmpty() -> ProductKeyboardState.EMPTY
                else -> ProductKeyboardState.SUCCESS
            },
            animationSpec = tween(durationMillis = 180),
            label = "ProductRemoteKeyboardCrossfade"
        ) { state ->
            when (state) {
                ProductKeyboardState.LOADING -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                    }
                }

                ProductKeyboardState.ERROR -> {
                    ProductRemoteErrorView(
                        errorMessage = errorMessage ?: "Koneksi bermasalah",
                        onRetry = onRefresh,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                ProductKeyboardState.EMPTY -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FrogoEmptyView(
                            title = "Belum Ada Produk Tersimpan",
                            subtitle = "Simpan produk di katalog remote agar muncul di keyboard.",
                            actionButtonText = if (onManageClick != null) "Buka Katalog" else "Segarkan",
                            onActionClick = { onManageClick?.invoke() ?: onRefresh() }
                        )
                    }
                }

                ProductKeyboardState.SUCCESS -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(
                            items = items,
                            key = { index, item -> "${item.id ?: "item"}_${item.displayIndex}_$index" }
                        ) { _, item ->
                            ProductRemoteCard(
                                item = item,
                                currentMode = selectedOutputMode,
                                onClick = { onCommitText(item.toCommitTextByMode(selectedOutputMode)) },
                                onTitleClick = { onCommitText(item.toProductTitleCommitText()) },
                                onCaptionClick = { onCommitText(item.toProductCaptionCommitText()) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private enum class ProductKeyboardState {
    LOADING,
    ERROR,
    EMPTY,
    SUCCESS
}

/**
 * Tactile segmented mode selector for switching between Caption and Judul output.
 */
@Composable
fun ProductRemoteModeSelector(
    selectedMode: ProductRemoteOutputMode,
    onModeSelected: (ProductRemoteOutputMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(34.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ProductRemoteOutputMode.entries.forEach { mode ->
                val isSelected = mode == selectedMode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else Color.Transparent
                        )
                        .clickable { onModeSelected(mode) }
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode.displayName,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Tactile Product Remote Card inside keyboard 2-column grid.
 */
@Composable
fun ProductRemoteCard(
    item: DataItemResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    currentMode: ProductRemoteOutputMode = ProductRemoteOutputMode.CAPTION,
    onTitleClick: () -> Unit = onClick,
    onCaptionClick: () -> Unit = onClick
) {
    val hasThumbnail = !item.thumbnailUrl.isNullOrBlank()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (hasThumbnail) {
                // Media Thumbnail Header (9:16 Vertical Ratio)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(9f / 16f)
                ) {
                    AsyncGlideImage(
                        url = item.thumbnailUrl,
                        contentDescription = item.displayTitle,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        overrideWidth = 360,
                        overrideHeight = 640
                    )

                    // Video / File Size Badge Overlay
                    if (item.isVideo == true) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Black.copy(alpha = 0.68f),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Video product",
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                                if (!item.fileSize.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = item.fileSize,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Card Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Download Status Chip (if no thumbnail)
                if (!hasThumbnail && !item.statusDownload.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (item.isDownloaded) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = if (item.isDownloaded) "✓ Terunduh" else "Belum",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.isDownloaded) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                // Title
                Text(
                    text = item.displayTitle,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Body snippet
                val bodyText = item.displayBody
                if (!bodyText.isNullOrBlank()) {
                    Text(
                        text = bodyText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            lineHeight = 14.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // File Name Tag Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = item.originalFileName ?: "File #${item.displayIndex}",
                        fontSize = 9.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Dual Quick Actions: Judul & Caption
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (currentMode == ProductRemoteOutputMode.TITLE) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        },
                        border = BorderStroke(
                            0.5.dp,
                            if (currentMode == ProductRemoteOutputMode.TITLE) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(onClick = onTitleClick)
                    ) {
                        Text(
                            text = "Judul",
                            fontSize = 10.sp,
                            fontWeight = if (currentMode == ProductRemoteOutputMode.TITLE) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (currentMode == ProductRemoteOutputMode.TITLE) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (currentMode == ProductRemoteOutputMode.CAPTION) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        },
                        border = BorderStroke(
                            0.5.dp,
                            if (currentMode == ProductRemoteOutputMode.CAPTION) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(onClick = onCaptionClick)
                    ) {
                        Text(
                            text = "Caption",
                            fontSize = 10.sp,
                            fontWeight = if (currentMode == ProductRemoteOutputMode.CAPTION) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (currentMode == ProductRemoteOutputMode.CAPTION) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Ergonomic Error View for Product Remote keyboard panel.
 */
@Composable
fun ProductRemoteErrorView(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = "Koneksi bermasalah",
                    tint = FrogoStatusFailed,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Gagal Memuat Produk",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Coba Lagi",
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp)
                    )
                }
            }
        }
    }
}

/**
 * Output mode options for Product Remote keyboard snippets:
 * 1. CAPTION: Commits product promotional caption / description
 * 2. TITLE: Commits product title
 */
enum class ProductRemoteOutputMode(val displayName: String, val shortLabel: String) {
    CAPTION("📝 Caption", "Caption"),
    TITLE("🏷️ Judul Produk", "Judul")
}

/**
 * Text formatting contract for inserting product caption into chat input.
 * Prioritizes caption, fallback to productName, or item index.
 */
fun DataItemResponse.toProductCaptionCommitText(): String {
    return caption?.takeIf { it.isNotBlank() }
        ?: productName?.takeIf { it.isNotBlank() }
        ?: "Item #$displayIndex"
}

/**
 * Text formatting contract for inserting product title into chat input.
 * Prioritizes productName, fallback to caption, or item index.
 */
fun DataItemResponse.toProductTitleCommitText(): String {
    return productName?.takeIf { it.isNotBlank() }
        ?: caption?.takeIf { it.isNotBlank() }
        ?: "Produk #$displayIndex"
}

/**
 * Maps selected mode to the formatted commit text.
 */
fun DataItemResponse.toCommitTextByMode(mode: ProductRemoteOutputMode): String {
    return when (mode) {
        ProductRemoteOutputMode.CAPTION -> toProductCaptionCommitText()
        ProductRemoteOutputMode.TITLE -> toProductTitleCommitText()
    }
}

/**
 * Full formatting contract for inserting product snippets into chat input.
 */
fun DataItemResponse.toFormattedCommitText(): String {
    val headline = displayTitle
    val content = displayBody.orEmpty()
    val drive = driveLink?.takeIf { it.isNotBlank() }
    val file = originalFileName?.takeIf { it.isNotBlank() }

    return buildString {
        append(headline)
        if (content.isNotBlank() && content != headline) {
            append("\n")
            append(content)
        }
        if (!drive.isNullOrBlank()) {
            append("\n\nLink: ")
            append(drive)
        }
        if (!file.isNullOrBlank()) {
            append("\nFile: ")
            append(file)
        }
    }
}
