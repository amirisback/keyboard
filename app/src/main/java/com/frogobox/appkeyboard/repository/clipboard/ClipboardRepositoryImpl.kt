package com.frogobox.appkeyboard.repository.clipboard

import com.frogobox.appkeyboard.model.ClipboardItem
import com.frogobox.sdk.delegate.preference.PreferenceDelegates
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClipboardRepositoryImpl(
    private val pref: PreferenceDelegates,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ClipboardRepository {

    @Inject
    constructor(pref: PreferenceDelegates) : this(pref, Dispatchers.IO)

    companion object {
        const val PREF_KEY_CLIPBOARD_ITEMS = "PREF_KEY_CLIPBOARD_ITEMS"
        const val MAX_UNPINNED_ITEMS = 40
        const val MAX_TEXT_LENGTH = 5000
    }

    private val gson = Gson()
    private val _itemsFlow = MutableStateFlow<List<ClipboardItem>>(emptyList())

    init {
        loadInitialItems()
    }

    private fun loadInitialItems() {
        val rawJson = pref.getPrefString(PREF_KEY_CLIPBOARD_ITEMS, "")
        val items = deserializeList(rawJson)
        _itemsFlow.value = sortItems(items)
    }

    override fun getClipboardItems(): Flow<List<ClipboardItem>> {
        return _itemsFlow.asStateFlow()
    }

    override fun getLatestClip(): Flow<ClipboardItem?> {
        return _itemsFlow.map { list ->
            list.maxByOrNull { it.timestamp }
        }
    }

    override suspend fun addClip(text: String): Unit = withContext(dispatcher) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return@withContext

        val safeText = if (trimmed.length > MAX_TEXT_LENGTH) trimmed.take(MAX_TEXT_LENGTH) else trimmed
        val currentList = _itemsFlow.value.toMutableList()

        val existingIndex = currentList.indexOfFirst { it.text == safeText }
        if (existingIndex != -1) {
            val existing = currentList.removeAt(existingIndex)
            val updated = existing.copy(timestamp = System.currentTimeMillis())
            currentList.add(0, updated)
        } else {
            val newItem = ClipboardItem(
                id = UUID.randomUUID().toString(),
                text = safeText,
                timestamp = System.currentTimeMillis(),
                isPinned = false
            )
            currentList.add(0, newItem)
        }

        val sorted = sortItems(enforceLimits(currentList))
        saveAndEmit(sorted)
    }

    override suspend fun togglePin(id: String): Unit = withContext(dispatcher) {
        val currentList = _itemsFlow.value.map { item ->
            if (item.id == id) {
                item.copy(isPinned = !item.isPinned)
            } else {
                item
            }
        }
        val sorted = sortItems(currentList)
        saveAndEmit(sorted)
    }

    override suspend fun deleteClip(id: String): Unit = withContext(dispatcher) {
        val currentList = _itemsFlow.value.filterNot { it.id == id }
        saveAndEmit(currentList)
    }

    override suspend fun clearHistory(keepPinned: Boolean): Unit = withContext(dispatcher) {
        val currentList = if (keepPinned) {
            _itemsFlow.value.filter { it.isPinned }
        } else {
            emptyList()
        }
        saveAndEmit(currentList)
    }

    private fun enforceLimits(items: List<ClipboardItem>): List<ClipboardItem> {
        val pinned = items.filter { it.isPinned }
        val unpinned = items.filterNot { it.isPinned }.take(MAX_UNPINNED_ITEMS)
        return pinned + unpinned
    }

    private fun sortItems(items: List<ClipboardItem>): List<ClipboardItem> {
        val pinned = items.filter { it.isPinned }.sortedByDescending { it.timestamp }
        val unpinned = items.filterNot { it.isPinned }.sortedByDescending { it.timestamp }
        return pinned + unpinned
    }

    private fun saveAndEmit(items: List<ClipboardItem>) {
        pref.savePrefString(PREF_KEY_CLIPBOARD_ITEMS, serializeList(items))
        _itemsFlow.value = items
    }

    private fun serializeList(items: List<ClipboardItem>): String {
        return gson.toJson(items)
    }

    private fun deserializeList(json: String): List<ClipboardItem> {
        if (json.isBlank()) return emptyList()
        return try {
            val type = object : TypeToken<List<ClipboardItem>>() {}.type
            gson.fromJson<List<ClipboardItem>>(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
}
