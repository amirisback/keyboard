package com.frogobox.appkeyboard.data.local.productremote

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.frogobox.appkeyboard.model.ProductEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for local Product Remote table.
 * Supports CRUD, batch synchronization, reactive flows, and search filtering.
 */
@Dao
interface ProductRemoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Update
    suspend fun update(product: ProductEntity)

    @Delete
    suspend fun delete(product: ProductEntity)

    @Query("DELETE FROM product_remote WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM product_remote WHERE remoteId = :remoteId")
    suspend fun deleteByRemoteId(remoteId: String)

    @Query("DELETE FROM product_remote WHERE id IN (:idList)")
    suspend fun delete(idList: List<Int>)

    @Query("DELETE FROM product_remote")
    suspend fun nukeData()

    @Query("SELECT * FROM product_remote ORDER BY id DESC")
    fun getAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM product_remote ORDER BY id DESC")
    suspend fun getAllSync(): List<ProductEntity>

    @Query("SELECT * FROM product_remote WHERE id = :id")
    fun getById(id: Int): Flow<ProductEntity?>

    @Query("SELECT * FROM product_remote WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: String): ProductEntity?

    @Query("SELECT remoteId FROM product_remote WHERE remoteId IS NOT NULL")
    fun getSavedRemoteIds(): Flow<List<String>>

    @Query("SELECT * FROM product_remote WHERE statusDownload = :status ORDER BY id DESC")
    fun getByStatusDownload(status: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM product_remote WHERE (productName LIKE '%' || :query || '%' OR caption LIKE '%' || :query || '%' OR originalFileName LIKE '%' || :query || '%') ORDER BY id DESC")
    fun search(query: String): Flow<List<ProductEntity>>

}
