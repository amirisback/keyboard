package com.frogobox.appkeyboard.data.local.autotext

/**
 * Created by Faisal Amir on 10/03/23
 * https://github.com/amirisback
 */

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.AutoTextLabelType
import kotlinx.coroutines.flow.Flow

@Dao
interface AutoTextDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(autoText: AutoTextEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(autoTexts: List<AutoTextEntity>)

    @Update
    suspend fun update(autoText: AutoTextEntity)

    @Delete
    suspend fun delete(autoText: AutoTextEntity)

    @Query("DELETE FROM auto_text WHERE id IN (:idList)")
    suspend fun delete(idList: List<Int>)

    @Query("DELETE FROM auto_text")
    suspend fun nukeData()

    @Query("SELECT * FROM auto_text ORDER BY id ASC")
    fun getAll(): Flow<List<AutoTextEntity>>

    @Query("SELECT * FROM auto_text WHERE id = :id")
    fun getById(id: Int): Flow<AutoTextEntity?>

    @Query("SELECT * FROM auto_text WHERE title LIKE '%' || :search || '%' ORDER BY id ASC")
    fun getByTitle(search: String): Flow<List<AutoTextEntity>>

    @Query("SELECT * FROM auto_text WHERE label = :label ORDER BY id ASC")
    fun getByLabel(label: AutoTextLabelType): Flow<List<AutoTextEntity>>

    @Query("SELECT * FROM auto_text WHERE body LIKE '%' || :search || '%' ORDER BY id ASC")
    fun getByBody(search: String): Flow<List<AutoTextEntity>>

    @Query("SELECT * FROM auto_text WHERE (title LIKE '%' || :search || '%' OR body LIKE '%' || :search || '%') ORDER BY id ASC")
    fun getByTitleOrBody(search: String): Flow<List<AutoTextEntity>>

}