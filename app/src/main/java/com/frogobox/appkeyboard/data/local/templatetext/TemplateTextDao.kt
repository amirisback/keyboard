package com.frogobox.appkeyboard.data.local.templatetext

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.frogobox.appkeyboard.model.TemplateTextEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateTextDao {

    @Query("SELECT * FROM template_text ORDER BY id DESC")
    fun getAllFlow(): Flow<List<TemplateTextEntity>>

    @Query("SELECT * FROM template_text WHERE category = :category ORDER BY id DESC")
    fun getByCategoryFlow(category: String): Flow<List<TemplateTextEntity>>

    @Query("SELECT * FROM template_text WHERE category = :category ORDER BY id DESC")
    suspend fun getByCategory(category: String): List<TemplateTextEntity>

    @Query("SELECT * FROM template_text ORDER BY id DESC")
    suspend fun getAll(): List<TemplateTextEntity>

    @Query("SELECT * FROM template_text WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): TemplateTextEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TemplateTextEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<TemplateTextEntity>): List<Long>

    @Update
    suspend fun update(entity: TemplateTextEntity): Int

    @Delete
    suspend fun delete(entity: TemplateTextEntity): Int

    @Query("DELETE FROM template_text WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    @Query("DELETE FROM template_text WHERE category = :category")
    suspend fun deleteByCategory(category: String): Int

    @Query("DELETE FROM template_text")
    suspend fun deleteAll(): Int

    @Query("SELECT COUNT(*) FROM template_text WHERE category = :category")
    suspend fun countByCategory(category: String): Int

    @Query("SELECT COUNT(*) FROM template_text")
    suspend fun countAll(): Int
}
