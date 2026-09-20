package com.frogobox.appkeyboard.ui.productremote

import androidx.lifecycle.viewModelScope
import com.frogobox.appkeyboard.common.base.BaseViewModel
import com.frogobox.appkeyboard.common.core.Resource
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import com.frogobox.appkeyboard.repository.data.DataApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProductRemoteUiState {
    data object Loading : ProductRemoteUiState
    data class Success(
        val products: List<DataItemResponse>,
        val filteredProducts: List<DataItemResponse>,
        val searchQuery: String = ""
    ) : ProductRemoteUiState
    data class Empty(val message: String = "Belum Ada Produk Remote") : ProductRemoteUiState
    data class Error(val message: String, val isOffline: Boolean = false) : ProductRemoteUiState
}

@HiltViewModel
class ProductRemoteViewModel @Inject constructor(
    private val repository: DataApiRepository
) : BaseViewModel() {

    private val _rawProducts = MutableStateFlow<List<DataItemResponse>>(emptyList())
    val products: StateFlow<List<DataItemResponse>> = _rawProducts.asStateFlow()

    private val _filteredProducts = MutableStateFlow<List<DataItemResponse>>(emptyList())
    val filteredProducts: StateFlow<List<DataItemResponse>> = _filteredProducts.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _uiState = MutableStateFlow<ProductRemoteUiState>(ProductRemoteUiState.Loading)
    val uiState: StateFlow<ProductRemoteUiState> = _uiState.asStateFlow()

    init {
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
                        applyFilter(_searchQuery.value)

                        _uiState.value = if (rawItems.isEmpty()) {
                            ProductRemoteUiState.Empty()
                        } else {
                            ProductRemoteUiState.Success(
                                products = rawItems,
                                filteredProducts = _filteredProducts.value,
                                searchQuery = _searchQuery.value
                            )
                        }
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = resource.message
                        val isOffline = resource.message.contains("Connection refused", ignoreCase = true) ||
                                resource.message.contains("Unable to resolve host", ignoreCase = true) ||
                                resource.message.contains("timeout", ignoreCase = true)
                        _uiState.value = ProductRemoteUiState.Error(
                            message = resource.message,
                            isOffline = isOffline
                        )
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
        applyFilter(query)
        if (_rawProducts.value.isNotEmpty()) {
            _uiState.value = ProductRemoteUiState.Success(
                products = _rawProducts.value,
                filteredProducts = _filteredProducts.value,
                searchQuery = query
            )
        }
    }

    private fun applyFilter(query: String) {
        val allItems = _rawProducts.value
        if (query.isBlank()) {
            _filteredProducts.value = allItems
        } else {
            val trimmed = query.trim().lowercase()
            _filteredProducts.value = allItems.filter { item ->
                item.productName?.lowercase()?.contains(trimmed) == true ||
                item.displayTitle.lowercase().contains(trimmed) ||
                item.caption?.lowercase()?.contains(trimmed) == true ||
                item.originalFileName?.lowercase()?.contains(trimmed) == true ||
                item.statusDownload?.lowercase()?.contains(trimmed) == true ||
                item.displayBody?.lowercase()?.contains(trimmed) == true
            }
        }
    }
}
