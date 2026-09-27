package com.frogobox.appkeyboard.ui.productremote

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import com.frogobox.appkeyboard.model.ProductEntity
import com.frogobox.appkeyboard.ui.keyboard.common.AsyncGlideImage
import com.frogobox.appkeyboard.ui.keyboard.productremote.toFormattedCommitText
import com.frogobox.appkeyboard.ui.keyboard.productremote.toProductCaptionCommitText
import com.frogobox.appkeyboard.ui.keyboard.productremote.toProductHookCommitText
import com.frogobox.appkeyboard.ui.keyboard.productremote.toProductLinkCommitText
import com.frogobox.appkeyboard.ui.keyboard.productremote.toProductTitleCommitText
import com.frogobox.appkeyboard.di.NetworkModule
import com.frogobox.appkeyboard.ui.theme.compose.FrogoEmptyView
import com.frogobox.appkeyboard.ui.theme.compose.FrogoPrimary
import com.frogobox.appkeyboard.ui.theme.compose.FrogoStatusFailed
import com.frogobox.appkeyboard.ui.theme.compose.FrogoTopAppBar

/**
 * Stateful entry point collecting flows from [ProductRemoteViewModel].
 */
@Composable
fun ProductRemoteScreen(
    viewModel: ProductRemoteViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.downloadStatusFilter.collectAsState()
    val savedRemoteIds by viewModel.savedRemoteIds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val syncMessage by viewModel.syncMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val downloadStates by viewModel.downloadStates.collectAsState()

    LaunchedEffect(syncMessage) {
        syncMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearSyncMessage()
        }
    }

    ProductRemoteScreen(
        products = products,
        filteredProducts = filteredProducts,
        searchQuery = searchQuery,
        statusFilter = statusFilter,
        savedRemoteIds = savedRemoteIds,
        downloadStates = downloadStates,
        isLoading = isLoading,
        isSyncing = isSyncing,
        errorMessage = errorMessage,
        onSearchQueryChange = viewModel::onSearchQueryChanged,
        onStatusFilterChange = viewModel::onDownloadStatusFilterChanged,
        onSyncAllToRoomDb = viewModel::syncAllToRoomDb,
        onToggleSave = viewModel::toggleSaveProduct,
        onCreateProduct = viewModel::createProduct,
        onUpdateProduct = viewModel::updateProduct,
        onDeleteProduct = viewModel::deleteProduct,
        onNukeAllFavorites = viewModel::nukeAllFavoriteProducts,
        onDownloadVideo = { item -> viewModel.downloadVideo(context, item) },
        onRefresh = viewModel::fetchProducts,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

/**
 * Dedicated In-App screen for inspecting, searching, and managing Product Remote items.
 * Includes Download Status Filter (Sudah/Belum), Room DB Sync, and full CRUD.
 */
@Composable
fun ProductRemoteScreen(
    products: List<DataItemResponse>,
    filteredProducts: List<DataItemResponse>,
    searchQuery: String,
    statusFilter: DownloadStatusFilter,
    savedRemoteIds: Set<String>,
    downloadStates: Map<String, DownloadProgressState> = emptyMap(),
    isLoading: Boolean,
    isSyncing: Boolean,
    errorMessage: String?,
    onSearchQueryChange: (String) -> Unit,
    onStatusFilterChange: (DownloadStatusFilter) -> Unit,
    onSyncAllToRoomDb: () -> Unit,
    onToggleSave: (DataItemResponse) -> Unit,
    onCreateProduct: (ProductEntity) -> Unit,
    onUpdateProduct: (ProductEntity) -> Unit,
    onDeleteProduct: (DataItemResponse) -> Unit,
    onNukeAllFavorites: () -> Unit = {},
    onDownloadVideo: (DataItemResponse) -> Unit = {},
    onRefresh: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var editingItem by remember { mutableStateOf<DataItemResponse?>(null) }
    var itemToDelete by remember { mutableStateOf<DataItemResponse?>(null) }
    var showNukeDialog by remember { mutableStateOf(false) }

    val onCopyCaption: (DataItemResponse) -> Unit = { item ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Caption Produk", item.toProductCaptionCommitText())
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Caption produk berhasil disalin", Toast.LENGTH_SHORT).show()
    }

    val onCopyTitle: (DataItemResponse) -> Unit = { item ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Judul Produk", item.toProductTitleCommitText())
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Judul produk berhasil disalin", Toast.LENGTH_SHORT).show()
    }

    val onCopySnippet: (DataItemResponse) -> Unit = { item ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(item.displayTitle, item.toFormattedCommitText())
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Format chat berhasil disalin", Toast.LENGTH_SHORT).show()
    }

    val onCopyHook: (DataItemResponse) -> Unit = { item ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Hook Produk", item.toProductHookCommitText())
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Hook produk berhasil disalin", Toast.LENGTH_SHORT).show()
    }

    val onCopyDriveLink: (String) -> Unit = { link ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Drive Link", link)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Link Drive berhasil disalin", Toast.LENGTH_SHORT).show()
    }

    val onCopyProductLink: (String) -> Unit = { link ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Link Produk", link)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Link Produk berhasil disalin", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            FrogoTopAppBar(
                title = if (statusFilter == DownloadStatusFilter.FAVORITE) "Produk Favorit (DB)" else "Katalog Product Remote",
                titleStyle = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.5.sp
                ),
                onBackClick = onBackClick,
                actions = {
                    if (statusFilter == DownloadStatusFilter.FAVORITE) {
                        IconButton(
                            onClick = {
                                if (savedRemoteIds.isEmpty()) {
                                    Toast.makeText(context, "Tidak ada produk favorit untuk dihapus", Toast.LENGTH_SHORT).show()
                                } else {
                                    showNukeDialog = true
                                }
                            },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Hapus Semua Produk Favorit (Nuke)",
                                tint = if (savedRemoteIds.isNotEmpty()) FrogoStatusFailed else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            if (statusFilter == DownloadStatusFilter.FAVORITE) {
                                onStatusFilterChange(DownloadStatusFilter.ALL)
                            } else {
                                onStatusFilterChange(DownloadStatusFilter.FAVORITE)
                            }
                        },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = if (statusFilter == DownloadStatusFilter.FAVORITE) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Menu List Favorit",
                            tint = if (statusFilter == DownloadStatusFilter.FAVORITE) FrogoPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            try {
                                val chuckerIntent = com.chuckerteam.chucker.api.Chucker.getLaunchIntent(context)
                                context.startActivity(chuckerIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Chucker belum tersedia: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = "Buka Chucker Interceptor",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onRefresh,
                        enabled = !isLoading,
                        modifier = Modifier.size(38.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = FrogoPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Segarkan data produk",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Sticky Search Bar Container & Filter Chips
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Cari produk remote...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cari",
                            tint = FrogoPrimary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Hapus pencarian",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FrogoPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Download Status Filter Chips (Semua, Favorit, Sudah, Belum)
                DownloadStatusFilterChips(
                    selectedFilter = statusFilter,
                    totalCount = products.size,
                    favoriteCount = savedRemoteIds.size,
                    downloadedCount = products.count { it.isDownloaded },
                    notDownloadedCount = products.count { !it.isDownloaded },
                    onFilterSelected = onStatusFilterChange
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Metric & Sync Summary Banner
                if (products.isNotEmpty()) {
                    ProductRemoteSummaryBanner(
                        totalCount = products.size,
                        filteredCount = filteredProducts.size,
                        savedCount = savedRemoteIds.size,
                        isSearching = searchQuery.isNotBlank(),
                        isSyncing = isSyncing,
                        onSyncAll = onSyncAllToRoomDb
                    )
                }
            }

            // 4. Main Content Area (State Management)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when {
                    statusFilter == DownloadStatusFilter.FAVORITE && filteredProducts.isEmpty() && searchQuery.isBlank() -> {
                        FrogoEmptyView(
                            title = "Belum Ada Produk Favorit",
                            subtitle = "Simpan produk dari katalog remote agar muncul di daftar favorit dan keyboard.",
                            actionButtonText = "Lihat Semua Katalog",
                            onActionClick = { onStatusFilterChange(DownloadStatusFilter.ALL) },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    statusFilter == DownloadStatusFilter.FAVORITE -> {
                        if (filteredProducts.isEmpty()) {
                            ProductRemoteEmptySearchState(
                                query = searchQuery,
                                onResetSearch = {
                                    onSearchQueryChange("")
                                },
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                itemsIndexed(
                                    items = filteredProducts,
                                    key = { index, item -> "${item.id ?: "fav"}_${item.displayIndex}_$index" }
                                ) { _, item ->
                                    val isSaved = savedRemoteIds.contains(item.id)
                                    val itemId = item.id ?: item.productName ?: item.rowIndex?.toString() ?: ""
                                    val itemDownloadState = downloadStates[itemId]
                                    ProductRemoteInAppCard(
                                        item = item,
                                        isSavedInRoomDb = isSaved,
                                        downloadState = itemDownloadState,
                                        onToggleSave = { onToggleSave(item) },
                                        onEditClick = { editingItem = item },
                                        onDeleteClick = { itemToDelete = item },
                                        onCopyCaption = { onCopyCaption(item) },
                                        onCopyTitle = { onCopyTitle(item) },
                                        onCopyHook = { onCopyHook(item) },
                                        onCopySnippet = { onCopySnippet(item) },
                                        onCopyDriveLink = { item.driveLink?.let(onCopyDriveLink) },
                                        onCopyProductLink = { item.linkProduct?.let(onCopyProductLink) ?: item.toProductLinkCommitText().takeIf { it.isNotBlank() }?.let(onCopyProductLink) ?: onCopyProductLink("") },
                                        onDownloadVideo = { onDownloadVideo(item) }
                                    )
                                }
                            }
                        }
                    }

                    isLoading && products.isEmpty() -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = FrogoPrimary,
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Memuat data produk remote...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    errorMessage != null && products.isEmpty() -> {
                        ProductRemoteInAppErrorView(
                            errorMessage = errorMessage,
                            onRetry = onRefresh,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    products.isEmpty() -> {
                        FrogoEmptyView(
                            title = "Belum Ada Produk Remote",
                            subtitle = "Server belum memiliki data katalog produk aktif. Periksa kembali nanti.",
                            actionButtonText = "Segarkan",
                            onActionClick = onRefresh,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    filteredProducts.isEmpty() -> {
                        ProductRemoteEmptySearchState(
                            query = searchQuery,
                            onResetSearch = {
                                onSearchQueryChange("")
                                onStatusFilterChange(DownloadStatusFilter.ALL)
                            },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            itemsIndexed(
                                items = filteredProducts,
                                key = { index, item -> "${item.id ?: "prod"}_${item.displayIndex}_$index" }
                            ) { _, item ->
                                val isSaved = savedRemoteIds.contains(item.id)
                                val itemId = item.id ?: item.productName ?: item.rowIndex?.toString() ?: ""
                                val itemDownloadState = downloadStates[itemId]
                                ProductRemoteInAppCard(
                                    item = item,
                                    isSavedInRoomDb = isSaved,
                                    downloadState = itemDownloadState,
                                    onToggleSave = { onToggleSave(item) },
                                    onEditClick = { editingItem = item },
                                    onDeleteClick = { itemToDelete = item },
                                    onCopyCaption = { onCopyCaption(item) },
                                    onCopyTitle = { onCopyTitle(item) },
                                    onCopyHook = { onCopyHook(item) },
                                    onCopySnippet = { onCopySnippet(item) },
                                    onCopyDriveLink = { item.driveLink?.let(onCopyDriveLink) },
                                    onCopyProductLink = { item.linkProduct?.let(onCopyProductLink) ?: item.toProductLinkCommitText().takeIf { it.isNotBlank() }?.let(onCopyProductLink) ?: onCopyProductLink("") },
                                    onDownloadVideo = { onDownloadVideo(item) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    editingItem?.let { item ->
        ProductFormDialog(
            initialProduct = item,
            onDismiss = { editingItem = null },
            onSave = { updatedProduct ->
                onUpdateProduct(updatedProduct)
                editingItem = null
            }
        )
    }

    // Delete Confirmation Dialog
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = {
                Text(
                    text = "Hapus Produk?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus \"${item.displayTitle}\" dari database lokal? Produk ini tidak akan tampil lagi di keyboard.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteProduct(item)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Nuke All Favorites Confirmation Dialog
    if (showNukeDialog) {
        AlertDialog(
            onDismissRequest = { showNukeDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = null,
                    tint = FrogoStatusFailed,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Hapus Semua Favorit?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus semua (${savedRemoteIds.size}) produk favorit dari database lokal? Data ini akan dihapus dari Room DB dan tidak lagi muncul di keyboard.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onNukeAllFavorites()
                        showNukeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FrogoStatusFailed
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Hapus Semua",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showNukeDialog = false },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

/**
 * Filter Chips Bar for download status filtering.
 */
@Composable
private fun DownloadStatusFilterChips(
    selectedFilter: DownloadStatusFilter,
    totalCount: Int,
    favoriteCount: Int,
    downloadedCount: Int,
    notDownloadedCount: Int,
    onFilterSelected: (DownloadStatusFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            val isSelected = selectedFilter == DownloadStatusFilter.ALL
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) FrogoPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(
                    1.dp,
                    if (isSelected) FrogoPrimary else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onFilterSelected(DownloadStatusFilter.ALL) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Semua ($totalCount)",
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            val isSelected = selectedFilter == DownloadStatusFilter.FAVORITE
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) FrogoPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(
                    1.dp,
                    if (isSelected) FrogoPrimary else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onFilterSelected(DownloadStatusFilter.FAVORITE) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = if (isSelected) Color.White else FrogoPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Favorit ($favoriteCount)",
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            val isSelected = selectedFilter == DownloadStatusFilter.DOWNLOADED
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) Color(0xFF1B5E20) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(
                    1.dp,
                    if (isSelected) Color(0xFF81C784) else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onFilterSelected(DownloadStatusFilter.DOWNLOADED) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (isSelected) Color.White else Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sudah Diunduh ($downloadedCount)",
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            val isSelected = selectedFilter == DownloadStatusFilter.NOT_DOWNLOADED
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) Color(0xFFE65100) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(
                    1.dp,
                    if (isSelected) Color(0xFFFFB74D) else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onFilterSelected(DownloadStatusFilter.NOT_DOWNLOADED) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassEmpty,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = if (isSelected) Color.White else Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Belum Diunduh ($notDownloadedCount)",
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Metric summary banner showing catalog status, item count, and Room DB sync trigger.
 */
@Composable
private fun ProductRemoteSummaryBanner(
    totalCount: Int,
    filteredCount: Int,
    savedCount: Int,
    isSearching: Boolean,
    isSyncing: Boolean,
    endpoint: String = NetworkModule.BASE_URL.removePrefix("http://").removePrefix("https://").removeSuffix("/"),
    onSyncAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(FrogoPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = FrogoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = if (isSearching) "Hasil Pencarian ($filteredCount/$totalCount)" else "Katalog Server",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val context = LocalContext.current
                    Text(
                        text = "Endpoint: $endpoint • $savedCount di DB",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val fullEndpoint = "${NetworkModule.BASE_URL}api/data.json"
                            val clip = ClipData.newPlainText("API Endpoint", fullEndpoint)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Endpoint disalin: $fullEndpoint", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onSyncAll,
                enabled = !isSyncing,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FrogoPrimary),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(13.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Sync...",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Sync ke DB",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Rich In-App Product Card with media thumbnail, status badges, Room DB indicator, and CRUD controls.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProductRemoteInAppCard(
    item: DataItemResponse,
    isSavedInRoomDb: Boolean,
    downloadState: DownloadProgressState? = null,
    onToggleSave: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCopyCaption: () -> Unit,
    onCopyTitle: () -> Unit,
    onCopyHook: () -> Unit,
    onCopySnippet: () -> Unit,
    onCopyDriveLink: () -> Unit,
    onCopyProductLink: () -> Unit,
    onDownloadVideo: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val hasThumbnail = !item.thumbnailUrl.isNullOrBlank()
    val hasDriveLink = !item.driveLink.isNullOrBlank()
    val hasProductLink = !item.linkProduct.isNullOrBlank()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Media Header (9:16 Vertical Ratio)
            if (hasThumbnail) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(9f / 16f)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                ) {
                    AsyncGlideImage(
                        url = item.thumbnailUrl,
                        contentDescription = item.displayTitle,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Video Indicator Badge
                    if (item.isVideo == true) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Black.copy(alpha = 0.72f),
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
                                    modifier = Modifier.size(12.dp)
                                )
                                if (!item.fileSize.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = item.fileSize,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Body Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                // Status Badges & Room DB Indicator Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        // Download Status Badge
                        if (!item.statusDownload.isNullOrBlank()) {
                            val isDownloaded = item.isDownloaded
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isDownloaded) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = BorderStroke(
                                    0.5.dp,
                                    if (isDownloaded) Color(0xFF81C784) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                                )
                            ) {
                                Text(
                                    text = if (isDownloaded) "✓ Terunduh" else "Belum",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDownloaded) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Room DB Saved Badge
                        if (isSavedInRoomDb) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = FrogoPrimary.copy(alpha = 0.12f),
                                border = BorderStroke(0.5.dp, FrogoPrimary.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = null,
                                        tint = FrogoPrimary,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "DB",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FrogoPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "#${item.displayIndex}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Title
                Text(
                    text = item.displayTitle,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        lineHeight = 17.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Caption
                val captionText = item.caption
                if (!captionText.isNullOrBlank() && captionText != item.displayTitle) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Caption: $captionText",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Hook snippet
                val hookText = item.hook
                if (!hookText.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "🪝 Hook: $hookText",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.tertiary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Product Link
                val productLinkText = item.linkProduct
                if (!productLinkText.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "🔗 Link: $productLinkText",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Body Description
                val bodyText = item.displayBody
                if (!bodyText.isNullOrBlank() && bodyText != item.displayTitle && bodyText != captionText && bodyText != hookText) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = bodyText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Metadata Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = item.originalFileName ?: "File #${item.displayIndex}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    val timestamp = item.displayTimestamp
                    if (!timestamp.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = timestamp,
                                fontSize = 9.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Primary Output Row: Salin Caption, Salin Judul, Salin Hook
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onCopyCaption,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 2.dp)
                    ) {
                        Text(
                            text = "Caption",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = onCopyTitle,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 2.dp)
                    ) {
                        Text(
                            text = "Judul",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = onCopyHook,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 2.dp)
                    ) {
                        Text(
                            text = "🪝 Hook",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Secondary Row: Link Produk, Drive, & Chat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onCopyProductLink,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (hasProductLink) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (hasProductLink) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        contentPadding = PaddingValues(vertical = 2.dp, horizontal = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Link",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (hasDriveLink) {
                        val isDownloading = downloadState is DownloadProgressState.Downloading
                        val progress = (downloadState as? DownloadProgressState.Downloading)?.progress ?: 0
                        val isDownloaded = item.isDownloaded || downloadState is DownloadProgressState.Success

                        val containerColor = when {
                            isDownloading -> FrogoPrimary.copy(alpha = 0.85f)
                            isDownloaded -> Color(0xFF2E7D32)
                            else -> FrogoPrimary
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = containerColor,
                            modifier = Modifier
                                .weight(1f)
                                .height(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .combinedClickable(
                                    onClick = {
                                        if (!isDownloading) {
                                            onDownloadVideo()
                                        }
                                    },
                                    onLongClick = onCopyDriveLink
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                when {
                                    isDownloading -> {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            strokeWidth = 1.5.dp,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = if (progress >= 0) "$progress%" else "Unduh",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    isDownloaded -> {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Sudah Diunduh",
                                            tint = Color.White,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "Video",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    else -> {
                                        Icon(
                                            imageVector = Icons.Default.CloudDownload,
                                            contentDescription = "Unduh Video Drive",
                                            tint = Color.White,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "Drive",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = onCopySnippet,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp),
                        contentPadding = PaddingValues(vertical = 2.dp, horizontal = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Chat",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Room DB Sync & CRUD Operations Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Toggle Save / Unsave Room DB Button
                    OutlinedButton(
                        onClick = onToggleSave,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSavedInRoomDb) FrogoPrimary.copy(alpha = 0.1f) else Color.Transparent,
                            contentColor = if (isSavedInRoomDb) FrogoPrimary else MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSavedInRoomDb) FrogoPrimary else MaterialTheme.colorScheme.outlineVariant
                        ),
                        contentPadding = PaddingValues(vertical = 2.dp, horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isSavedInRoomDb) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = if (isSavedInRoomDb) FrogoPrimary else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isSavedInRoomDb) "Tersimpan" else "Simpan",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Edit Button
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                                RoundedCornerShape(6.dp)
                            )
                            .clickable(
                                role = Role.Button,
                                onClick = onEditClick
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit produk",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Delete Button
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.error.copy(alpha = 0.35f),
                                RoundedCornerShape(6.dp)
                            )
                            .clickable(
                                role = Role.Button,
                                onClick = onDeleteClick
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Hapus produk",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Product Form Dialog for Create & Update operations.
 */
@Composable
private fun ProductFormDialog(
    initialProduct: DataItemResponse?,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    var productName by remember { mutableStateOf(initialProduct?.productName ?: initialProduct?.displayTitle ?: "") }
    var caption by remember { mutableStateOf(initialProduct?.caption ?: "") }
    var hook by remember { mutableStateOf(initialProduct?.hook ?: "") }
    var linkProduct by remember { mutableStateOf(initialProduct?.linkProduct ?: "") }
    var statusDownload by remember { mutableStateOf(initialProduct?.statusDownload ?: "Belum") }
    var driveLink by remember { mutableStateOf(initialProduct?.driveLink ?: "") }
    var originalFileName by remember { mutableStateOf(initialProduct?.originalFileName ?: "") }
    var fileSize by remember { mutableStateOf(initialProduct?.fileSize ?: "") }

    var isError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (initialProduct == null) "Tambah Produk Baru" else "Edit Produk",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = productName,
                    onValueChange = {
                        productName = it
                        if (it.isNotBlank()) isError = false
                    },
                    label = { Text("Nama Produk *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError && productName.isBlank(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = caption,
                    onValueChange = {
                        caption = it
                        if (it.isNotBlank()) isError = false
                    },
                    label = { Text("Caption Promosi *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError && caption.isBlank(),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = hook,
                    onValueChange = { hook = it },
                    label = { Text("Hook Promosi (Opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = linkProduct,
                    onValueChange = { linkProduct = it },
                    label = { Text("Link Produk (Shopee/Tokopedia/Web) (Opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Download Status Selection
                Text(
                    text = "Status Unduh",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isBelum = statusDownload.equals("Belum", ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isBelum) Color(0xFFE65100) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, if (isBelum) Color(0xFFFFB74D) else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { statusDownload = "Belum" }
                    ) {
                        Text(
                            text = "Belum Diunduh",
                            fontSize = 12.sp,
                            fontWeight = if (isBelum) FontWeight.Bold else FontWeight.Medium,
                            color = if (isBelum) Color.White else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    val isSudah = statusDownload.equals("Sudah", ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSudah) Color(0xFF1B5E20) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, if (isSudah) Color(0xFF81C784) else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { statusDownload = "Sudah" }
                    ) {
                        Text(
                            text = "✓ Sudah Diunduh",
                            fontSize = 12.sp,
                            fontWeight = if (isSudah) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSudah) Color.White else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = driveLink,
                    onValueChange = { driveLink = it },
                    label = { Text("Link Google Drive (Opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = originalFileName,
                        onValueChange = { originalFileName = it },
                        label = { Text("Nama File") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = fileSize,
                        onValueChange = { fileSize = it },
                        label = { Text("Ukuran File") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                if (isError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nama produk dan caption wajib diisi",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (productName.isBlank() || caption.isBlank()) {
                                isError = true
                                return@Button
                            }
                            val localId = initialProduct?.displayIndex ?: 0
                            val entity = ProductEntity(
                                id = localId,
                                remoteId = initialProduct?.id,
                                uploadTimestamp = initialProduct?.uploadTimestamp,
                                productName = productName.trim(),
                                caption = caption.trim(),
                                hook = hook.trim().takeIf { it.isNotBlank() },
                                originalFileName = originalFileName.takeIf { it.isNotBlank() },
                                fileSize = fileSize.takeIf { it.isNotBlank() },
                                fileType = initialProduct?.fileType,
                                driveLink = driveLink.takeIf { it.isNotBlank() },
                                linkProduct = linkProduct.trim().takeIf { it.isNotBlank() },
                                driveFileId = initialProduct?.driveFileId,
                                thumbnailUrl = initialProduct?.thumbnailUrl,
                                previewUrl = initialProduct?.previewUrl,
                                isVideo = initialProduct?.isVideo ?: false,
                                statusDownload = statusDownload,
                                rowIndex = initialProduct?.rowIndex
                            )
                            onSave(entity)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FrogoPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Simpan ke DB",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Empty Search State when query yields 0 matching products.
 */
@Composable
private fun ProductRemoteEmptySearchState(
    query: String,
    onResetSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Produk Tidak Ditemukan",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (query.isNotBlank()) "Tidak ada produk yang cocok dengan kata kunci \"$query\"." else "Tidak ada produk dengan filter ini.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                lineHeight = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onResetSearch,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Reset Pencarian",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * In-App Network Error View with tactile retry action.
 */
@Composable
private fun ProductRemoteInAppErrorView(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(FrogoStatusFailed.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = null,
                tint = FrogoStatusFailed,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Koneksi Server Terputus",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                lineHeight = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FrogoPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Coba Lagi",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
