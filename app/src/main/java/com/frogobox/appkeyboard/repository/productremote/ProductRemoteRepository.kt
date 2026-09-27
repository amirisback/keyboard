package com.frogobox.appkeyboard.repository.productremote

import com.frogobox.appkeyboard.common.core.Resource
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import com.frogobox.appkeyboard.model.ProductEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing Product Remote data across local Room DB and Remote API.
 * Provides offline-first reactivity, full CRUD operations, and sync orchestration.
 */
interface ProductRemoteRepository {

    fun getSavedProductsStream(): Flow<List<ProductEntity>>

    suspend fun getSavedProductsSync(): List<ProductEntity>

    fun getSavedRemoteIdsStream(): Flow<Set<String>>

    fun getProductById(id: Int): Flow<ProductEntity?>

    suspend fun saveProduct(product: ProductEntity): Long

    suspend fun saveProducts(products: List<ProductEntity>)

    suspend fun updateProduct(product: ProductEntity)

    suspend fun deleteProduct(product: ProductEntity)

    suspend fun deleteProductById(id: Int)

    suspend fun deleteProductByRemoteId(remoteId: String)

    suspend fun nukeAllSavedProducts()

    fun syncAllFromRemote(): Flow<Resource<Int>>

    suspend fun toggleSaveRemoteProduct(item: DataItemResponse): Boolean

    suspend fun isProductSaved(remoteId: String): Boolean

    suspend fun updateDownloadStatus(remoteId: String?, localId: Int, status: String)

}
