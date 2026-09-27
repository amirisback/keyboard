package com.frogobox.appkeyboard.repository.templatetext

import android.content.Context
import com.frogobox.appkeyboard.data.local.templatetext.TemplateTextDao
import com.frogobox.appkeyboard.model.TemplateCategoryType
import com.frogobox.appkeyboard.model.TemplateTextEntity
import com.frogobox.sdk.ext.getDataFromJsonAsset
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TemplateTextRepositoryImpl @Inject constructor(
    private val dao: TemplateTextDao,
    @ApplicationContext private val context: Context
) : TemplateTextRepository {

    private fun getAssetFileNameForCategory(category: String): String {
        return when (TemplateCategoryType.fromKey(category)) {
            TemplateCategoryType.GAME -> "text/text_game.json"
            TemplateCategoryType.APP -> "text/text_app.json"
            TemplateCategoryType.SALE -> "text/text_sale.json"
            TemplateCategoryType.GREETING -> "text/text_greeting.json"
            TemplateCategoryType.LOVE -> "text/text_love.json"
        }
    }

    override fun getAll(): Flow<List<TemplateTextEntity>> {
        return dao.getAllFlow().flowOn(Dispatchers.IO)
    }

    override fun getByCategory(category: String): Flow<List<TemplateTextEntity>> {
        val normalizedCategory = TemplateCategoryType.fromKey(category).key
        return dao.getByCategoryFlow(normalizedCategory).flowOn(Dispatchers.IO)
    }

    override suspend fun getByCategorySync(category: String): List<TemplateTextEntity> {
        return withContext(Dispatchers.IO) {
            val normalizedCategory = TemplateCategoryType.fromKey(category).key
            var items = dao.getByCategory(normalizedCategory)
            if (items.isEmpty()) {
                seedCategory(normalizedCategory)
                items = dao.getByCategory(normalizedCategory)
            }
            items
        }
    }

    override suspend fun getAllSync(): List<TemplateTextEntity> {
        return withContext(Dispatchers.IO) {
            seedDefaultsIfEmpty()
            dao.getAll()
        }
    }

    override suspend fun insertTemplate(category: String, text: String): Long {
        return withContext(Dispatchers.IO) {
            val normalizedCategory = TemplateCategoryType.fromKey(category).key
            val now = System.currentTimeMillis()
            dao.insert(
                TemplateTextEntity(
                    category = normalizedCategory,
                    text = text.trim(),
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    override suspend fun updateTemplate(id: Int, category: String, text: String): Boolean {
        return withContext(Dispatchers.IO) {
            val existing = dao.getById(id) ?: return@withContext false
            val normalizedCategory = TemplateCategoryType.fromKey(category).key
            existing.category = normalizedCategory
            existing.text = text.trim()
            existing.updatedAt = System.currentTimeMillis()
            dao.update(existing) > 0
        }
    }

    override suspend fun deleteTemplate(id: Int): Boolean {
        return withContext(Dispatchers.IO) {
            dao.deleteById(id) > 0
        }
    }

    override suspend fun deleteByCategory(category: String): Boolean {
        return withContext(Dispatchers.IO) {
            val normalizedCategory = TemplateCategoryType.fromKey(category).key
            dao.deleteByCategory(normalizedCategory) > 0
        }
    }

    private suspend fun seedCategory(category: String) {
        val fileName = getAssetFileNameForCategory(category)
        val defaultStrings: List<String> = try {
            context.getDataFromJsonAsset(fileName)
        } catch (_: Exception) {
            emptyList()
        }
        if (defaultStrings.isNotEmpty()) {
            val now = System.currentTimeMillis()
            val entities = defaultStrings.map { str ->
                TemplateTextEntity(
                    category = category,
                    text = str,
                    createdAt = now,
                    updatedAt = now
                )
            }
            dao.insertAll(entities)
        }
    }

    override suspend fun seedDefaultsIfEmpty() {
        withContext(Dispatchers.IO) {
            TemplateCategoryType.entries.forEach { cat ->
                if (dao.countByCategory(cat.key) == 0) {
                    seedCategory(cat.key)
                }
            }
        }
    }

    override suspend fun resetCategoryToDefaults(category: String) {
        withContext(Dispatchers.IO) {
            val normalizedCategory = TemplateCategoryType.fromKey(category).key
            dao.deleteByCategory(normalizedCategory)
            seedCategory(normalizedCategory)
        }
    }

    override suspend fun resetAllToDefaults() {
        withContext(Dispatchers.IO) {
            dao.deleteAll()
            TemplateCategoryType.entries.forEach { cat ->
                seedCategory(cat.key)
            }
        }
    }
}
