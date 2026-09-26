package com.frogobox.appkeyboard.repository.productremote

import com.frogobox.appkeyboard.common.core.Resource
import com.frogobox.appkeyboard.data.local.productremote.ProductRemoteDao
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import com.frogobox.appkeyboard.model.ProductEntity
import com.frogobox.appkeyboard.model.toProductEntity
import com.frogobox.appkeyboard.repository.data.DataApiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [ProductRemoteRepository] coordinating Room database operations
 * and network API synchronization.
 */
@Singleton
class ProductRemoteRepositoryImpl @Inject constructor(
    private val productRemoteDao: ProductRemoteDao,
    private val dataApiRepository: DataApiRepository
) : ProductRemoteRepository {

    override fun getSavedProductsStream(): Flow<List<ProductEntity>> {
        return productRemoteDao.getAll()
    }

    override suspend fun getSavedProductsSync(): List<ProductEntity> {
        return productRemoteDao.getAllSync()
    }

    override fun getSavedRemoteIdsStream(): Flow<Set<String>> {
        return productRemoteDao.getSavedRemoteIds().map { it.toSet() }
    }

    override fun getProductById(id: Int): Flow<ProductEntity?> {
        return productRemoteDao.getById(id)
    }

    override suspend fun saveProduct(product: ProductEntity): Long {
        return productRemoteDao.insert(product)
    }

    override suspend fun saveProducts(products: List<ProductEntity>) {
        productRemoteDao.insertAll(products)
    }

    override suspend fun updateProduct(product: ProductEntity) {
        val targetId = if (product.id > 0) {
            product.id
        } else if (product.remoteId != null) {
            productRemoteDao.getByRemoteId(product.remoteId!!)?.id ?: 0
        } else {
            0
        }
        if (targetId > 0) {
            productRemoteDao.update(product.copy(id = targetId))
        } else {
            productRemoteDao.insert(product)
        }
    }

    override suspend fun deleteProduct(product: ProductEntity) {
        productRemoteDao.delete(product)
    }

    override suspend fun deleteProductById(id: Int) {
        productRemoteDao.deleteById(id)
    }

    override suspend fun deleteProductByRemoteId(remoteId: String) {
        productRemoteDao.deleteByRemoteId(remoteId)
    }

    override suspend fun nukeAllSavedProducts() {
        productRemoteDao.nukeData()
    }

    override fun syncAllFromRemote(): Flow<Resource<Int>> = flow {
        emit(Resource.Loading)
        when (val result = dataApiRepository.fetchData()) {
            is Resource.Success -> {
                val remoteItems = result.data.items.orEmpty()
                if (remoteItems.isEmpty()) {
                    emit(Resource.Success(0))
                    return@flow
                }

                // Batch-load existing mappings in ONE single query to eliminate N+1 table scans
                val existingLocalMap = productRemoteDao.getAllSync()
                    .filter { it.remoteId != null }
                    .associateBy { it.remoteId!! }

                val entitiesToInsert = remoteItems.map { remoteItem ->
                    val existing = remoteItem.id?.let { existingLocalMap[it] }
                    remoteItem.toProductEntity(localId = existing?.id ?: 0)
                }

                productRemoteDao.insertAll(entitiesToInsert)
                emit(Resource.Success(entitiesToInsert.size))
            }
            is Resource.Error -> {
                emit(Resource.Error(result.message))
            }
            is Resource.Loading -> {
                emit(Resource.Loading)
            }
        }
    }

    override suspend fun toggleSaveRemoteProduct(item: DataItemResponse): Boolean {
        val remoteId = item.id
        if (remoteId != null) {
            val existing = productRemoteDao.getByRemoteId(remoteId)
            if (existing != null) {
                productRemoteDao.delete(existing)
                return false // Now removed from saved list
            }
        }
        val entity = item.toProductEntity()
        productRemoteDao.insert(entity)
        return true // Now saved in Room DB
    }

    override suspend fun isProductSaved(remoteId: String): Boolean {
        return productRemoteDao.getByRemoteId(remoteId) != null
    }

    override suspend fun updateDownloadStatus(remoteId: String?, localId: Int, status: String) {
        if (!remoteId.isNullOrBlank()) {
            productRemoteDao.updateDownloadStatusByRemoteId(remoteId, status)
        } else if (localId > 0) {
            productRemoteDao.updateDownloadStatusById(localId, status)
        }
    }

}
