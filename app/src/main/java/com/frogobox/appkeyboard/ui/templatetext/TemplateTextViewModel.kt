package com.frogobox.appkeyboard.ui.templatetext

import androidx.lifecycle.viewModelScope
import com.frogobox.appkeyboard.common.base.BaseViewModel
import com.frogobox.appkeyboard.model.TemplateCategoryType
import com.frogobox.appkeyboard.model.TemplateTextEntity
import com.frogobox.appkeyboard.repository.templatetext.TemplateTextRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplateTextViewModel @Inject constructor(
    private val repository: TemplateTextRepository
) : BaseViewModel() {

    private val _templateList = MutableStateFlow<List<TemplateTextEntity>>(emptyList())
    val templateList: StateFlow<List<TemplateTextEntity>> = _templateList.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null) // null = ALL
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _eventMessage = MutableStateFlow<String?>(null)
    val eventMessage: StateFlow<String?> = _eventMessage.asStateFlow()

    private var collectJob: Job? = null

    init {
        viewModelScope.launch {
            repository.seedDefaultsIfEmpty()
            loadTemplates()
        }
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
        loadTemplates()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun loadTemplates() {
        collectJob?.cancel()
        val category = _selectedCategory.value
        val flow = if (category.isNullOrBlank()) {
            repository.getAll()
        } else {
            val normalizedKey = TemplateCategoryType.fromKey(category).key
            repository.getByCategory(normalizedKey)
        }

        collectJob = viewModelScope.launch {
            flow.onStart { _isLoading.value = true }
                .catch {
                    _isLoading.value = false
                    _eventMessage.value = "Gagal memuat template teks"
                }
                .collect { items ->
                    _isLoading.value = false
                    _templateList.value = items
                }
        }
    }

    fun insertTemplate(category: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val normalizedKey = TemplateCategoryType.fromKey(category).key
            val id = repository.insertTemplate(normalizedKey, text.trim())
            if (id > 0) {
                _eventMessage.value = "Template berhasil ditambahkan"
            } else {
                _eventMessage.value = "Gagal menambahkan template"
            }
        }
    }

    fun updateTemplate(id: Int, category: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val normalizedKey = TemplateCategoryType.fromKey(category).key
            val success = repository.updateTemplate(id, normalizedKey, text.trim())
            if (success) {
                _eventMessage.value = "Template berhasil diperbarui"
            } else {
                _eventMessage.value = "Gagal memperbarui template"
            }
        }
    }

    fun deleteTemplate(id: Int) {
        viewModelScope.launch {
            val success = repository.deleteTemplate(id)
            if (success) {
                _eventMessage.value = "Template berhasil dihapus"
            } else {
                _eventMessage.value = "Gagal menghapus template"
            }
        }
    }

    fun resetCategoryToDefaults(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val normalizedKey = TemplateCategoryType.fromKey(category).key
            repository.resetCategoryToDefaults(normalizedKey)
            _isLoading.value = false
            _eventMessage.value = "Template kategori $normalizedKey berhasil direset ke default"
        }
    }

    fun resetAllToDefaults() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.resetAllToDefaults()
            _isLoading.value = false
            _eventMessage.value = "Semua template berhasil direset ke default"
        }
    }

    fun clearEventMessage() {
        _eventMessage.value = null
    }
}
