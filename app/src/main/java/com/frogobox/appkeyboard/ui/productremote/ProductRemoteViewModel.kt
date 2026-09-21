package com.frogobox.appkeyboard.ui.productremote

import androidx.lifecycle.viewModelScope
import com.frogobox.appkeyboard.common.base.BaseViewModel
import com.frogobox.appkeyboard.common.core.Resource
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import com.frogobox.appkeyboard.model.ProductEntity
import com.frogobox.appkeyboard.repository.data.DataApiRepository
import com.frogobox.appkeyboard.repository.productremote.ProductRemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class DownloadStatusFilter(val label: String) {
    ALL("Semua"),
    FAVORITE("Favorit"),
    DOWNLOADED("Sudah"),
    NOT_DOWNLOADED("Belum")
}

sealed interface ProductRemoteUiState {
    data object Loading : ProductRemoteUiState
    data class Success(
        val products: List<DataItemResponse>,
        val filteredProducts: List<DataItemResponse>,
        val searchQuery: String = "",
        val statusFilter: DownloadStatusFilter = DownloadStatusFilter.ALL
    ) : ProductRemoteUiState
    data class Empty(val message: String = "Belum Ada Produk Remote") : ProductRemoteUiState
    data class Error(val message: String, val isOffline: Boolean = false) : ProductRemoteUiState
}

@HiltViewModel
class ProductRemoteViewModel @Inject constructor(
    private val repository: DataApiRepository,
    private val productRemoteRepository: ProductRemoteRepository
) : BaseViewModel() {

    private val _rawProducts = MutableStateFlow<List<DataItemResponse>>(emptyList())
    val products: StateFlow<List<DataItemResponse>> = _rawProducts.asStateFlow()

    private val _filteredProducts = MutableStateFlow<List<DataItemResponse>>(emptyList())
    val filteredProducts: StateFlow<List<DataItemResponse>> = _filteredProducts.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _downloadStatusFilter = MutableStateFlow(DownloadStatusFilter.ALL)
    val downloadStatusFilter: StateFlow<DownloadStatusFilter> = _downloadStatusFilter.asStateFlow()

    private val _savedRemoteIds = MutableStateFlow<Set<String>>(emptySet())
    val savedRemoteIds: StateFlow<Set<String>> = _savedRemoteIds.asStateFlow()

    private val _savedProducts = MutableStateFlow<List<ProductEntity>>(emptyList())
    val savedProducts: StateFlow<List<ProductEntity>> = _savedProducts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _uiState = MutableStateFlow<ProductRemoteUiState>(ProductRemoteUiState.Loading)
    val uiState: StateFlow<ProductRemoteUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            productRemoteRepository.getSavedRemoteIdsStream().collect { ids ->
                _savedRemoteIds.value = ids
            }
        }
        viewModelScope.launch {
            productRemoteRepository.getSavedProductsStream().collect { list ->
                _savedProducts.value = list
                if (_downloadStatusFilter.value == DownloadStatusFilter.FAVORITE) {
                    applyFilter()
                    updateSuccessUiState()
                }
            }
        }
        fetchProducts()
    }

    fun fetchProducts() {
        _isLoading.value = true
        _errorMessage.value = null
        _uiState.value = ProductRemoteUiState.Loading

        viewModelScope.launch {
            repository.fetchDataStream().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                    is Resource.Success -> {
                        _isLoading.value = false
                        _errorMessage.value = null
                        val rawItems = resource.data.items.orEmpty()
                        _rawProducts.value = rawItems
                        applyFilter()

                        _uiState.value = if (rawItems.isEmpty() && _downloadStatusFilter.value != DownloadStatusFilter.FAVORITE) {
                            ProductRemoteUiState.Empty()
                        } else {
                            ProductRemoteUiState.Success(
                                products = if (_downloadStatusFilter.value == DownloadStatusFilter.FAVORITE) {
                                    _savedProducts.value.map { it.toDataItemResponse() }
                                } else {
                                    rawItems
                                },
                                filteredProducts = _filteredProducts.value,
                                searchQuery = _searchQuery.value,
                                statusFilter = _downloadStatusFilter.value
                            )
                        }
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = resource.message
                        val isOffline = resource.message.contains("Connection refused", ignoreCase = true) ||
                                resource.message.contains("Unable to resolve host", ignoreCase = true) ||
                                resource.message.contains("timeout", ignoreCase = true)
                        if (_downloadStatusFilter.value == DownloadStatusFilter.FAVORITE) {
                            applyFilter()
                            _uiState.value = ProductRemoteUiState.Success(
                                products = _savedProducts.value.map { it.toDataItemResponse() },
                                filteredProducts = _filteredProducts.value,
                                searchQuery = _searchQuery.value,
                                statusFilter = _downloadStatusFilter.value
                            )
                        } else {
                            _uiState.value = ProductRemoteUiState.Error(
                                message = resource.message,
                                isOffline = isOffline
                            )
                        }
                    }
                }
            }
        }
    }

    fun retry() {
        fetchProducts()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        applyFilter()
        updateSuccessUiState()
    }

    fun onDownloadStatusFilterChanged(filter: DownloadStatusFilter) {
        _downloadStatusFilter.value = filter
        applyFilter()
        updateSuccessUiState()
    }

    fun setInitialFilter(filter: DownloadStatusFilter) {
        _downloadStatusFilter.value = filter
        applyFilter()
        updateSuccessUiState()
    }

    private fun updateSuccessUiState() {
        if (_downloadStatusFilter.value == DownloadStatusFilter.FAVORITE || _rawProducts.value.isNotEmpty()) {
            _uiState.value = ProductRemoteUiState.Success(
                products = if (_downloadStatusFilter.value == DownloadStatusFilter.FAVORITE) {
                    _savedProducts.value.map { it.toDataItemResponse() }
                } else {
                    _rawProducts.value
                },
                filteredProducts = _filteredProducts.value,
                searchQuery = _searchQuery.value,
                statusFilter = _downloadStatusFilter.value
            )
        }
    }

    private fun applyFilter() {
        val query = _searchQuery.value.trim().lowercase()
        val statusFilter = _downloadStatusFilter.value
        val sourceItems = if (statusFilter == DownloadStatusFilter.FAVORITE) {
            _savedProducts.value.map { it.toDataItemResponse() }
        } else {
            _rawProducts.value
        }

        _filteredProducts.value = sourceItems.filter { item ->
            val matchesStatus = when (statusFilter) {
                DownloadStatusFilter.ALL -> true
                DownloadStatusFilter.FAVORITE -> true
                DownloadStatusFilter.DOWNLOADED -> item.isDownloaded
                DownloadStatusFilter.NOT_DOWNLOADED -> !item.isDownloaded
            }

            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                item.productName?.lowercase()?.contains(query) == true ||
                item.displayTitle.lowercase().contains(query) ||
                item.caption?.lowercase()?.contains(query) == true ||
                item.originalFileName?.lowercase()?.contains(query) == true ||
                item.statusDownload?.lowercase()?.contains(query) == true ||
                item.displayBody?.lowercase()?.contains(query) == true
            }

            matchesStatus && matchesQuery
        }
    }

    fun syncAllToRoomDb() {
        viewModelScope.launch {
            _isSyncing.value = true
            productRemoteRepository.syncAllFromRemote().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _isSyncing.value = true
                    }
                    is Resource.Success -> {
                        _isSyncing.value = false
                        _syncMessage.value = "${resource.data} produk berhasil disimpan ke Room DB"
                    }
                    is Resource.Error -> {
                        _isSyncing.value = false
                        _syncMessage.value = "Gagal sinkronisasi: ${resource.message}"
                    }
                }
            }
        }
    }

    fun toggleSaveProduct(item: DataItemResponse) {
        viewModelScope.launch {
            val isNowSaved = productRemoteRepository.toggleSaveRemoteProduct(item)
            _syncMessage.value = if (isNowSaved) {
                "\"${item.displayTitle}\" disimpan ke Room DB (muncul di keyboard)"
            } else {
                "\"${item.displayTitle}\" dihapus dari Room DB"
            }
            if (_downloadStatusFilter.value == DownloadStatusFilter.FAVORITE) {
                applyFilter()
                updateSuccessUiState()
            }
        }
    }

    fun createProduct(product: ProductEntity) {
        viewModelScope.launch {
            productRemoteRepository.saveProduct(product)
            _syncMessage.value = "Produk \"${product.productName}\" berhasil ditambahkan"
            // Re-fetch or refresh list to reflect new product
            val asResponse = product.toDataItemResponse()
            val currentList = _rawProducts.value.toMutableList()
            currentList.add(0, asResponse)
            _rawProducts.value = currentList
            applyFilter()
            updateSuccessUiState()
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            productRemoteRepository.updateProduct(product)
            _syncMessage.value = "Produk \"${product.productName}\" berhasil diperbarui"
            // Update in raw list if matching
            val asResponse = product.toDataItemResponse()
            val currentList = _rawProducts.value.map {
                if ((product.remoteId != null && it.id == product.remoteId) || it.displayIndex == product.id) {
                    asResponse
                } else {
                    it
                }
            }
            _rawProducts.value = currentList
            applyFilter()
            updateSuccessUiState()
        }
    }

    fun deleteProduct(item: DataItemResponse) {
        viewModelScope.launch {
            val remoteId = item.id
            if (remoteId != null) {
                productRemoteRepository.deleteProductByRemoteId(remoteId)
            }
            val localId = item.displayIndex
            productRemoteRepository.deleteProductById(localId)

            _syncMessage.value = "Produk \"${item.displayTitle}\" dihapus dari database"
            val currentList = _rawProducts.value.filterNot { it.id == item.id && it.displayIndex == item.displayIndex }
            _rawProducts.value = currentList
            applyFilter()
            updateSuccessUiState()
        }
    }

    fun nukeAllFavoriteProducts() {
        viewModelScope.launch {
            productRemoteRepository.nukeAllSavedProducts()
            _syncMessage.value = "Semua produk favorit berhasil dihapus dari Room DB"
            if (_downloadStatusFilter.value == DownloadStatusFilter.FAVORITE) {
                applyFilter()
                updateSuccessUiState()
            }
        }
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }

}
