package com.frogobox.appkeyboard.repository.templatetext

import com.frogobox.appkeyboard.model.TemplateTextEntity
import kotlinx.coroutines.flow.Flow

interface TemplateTextRepository {
    fun getAll(): Flow<List<TemplateTextEntity>>
    fun getByCategory(category: String): Flow<List<TemplateTextEntity>>
    suspend fun getByCategorySync(category: String): List<TemplateTextEntity>
    suspend fun getAllSync(): List<TemplateTextEntity>
    suspend fun insertTemplate(category: String, text: String): Long
    suspend fun updateTemplate(id: Int, category: String, text: String): Boolean
    suspend fun deleteTemplate(id: Int): Boolean
    suspend fun deleteByCategory(category: String): Boolean
    suspend fun seedDefaultsIfEmpty()
    suspend fun resetCategoryToDefaults(category: String)
    suspend fun resetAllToDefaults()
}
