package com.frogobox.appkeyboard.repository.clipboard

import com.frogobox.appkeyboard.model.ClipboardItem
import kotlinx.coroutines.flow.Flow

interface ClipboardRepository {
    fun getClipboardItems(): Flow<List<ClipboardItem>>
    fun getLatestClip(): Flow<ClipboardItem?>
    suspend fun addClip(text: String)
    suspend fun togglePin(id: String)
    suspend fun deleteClip(id: String)
    suspend fun clearHistory(keepPinned: Boolean = true)
}
