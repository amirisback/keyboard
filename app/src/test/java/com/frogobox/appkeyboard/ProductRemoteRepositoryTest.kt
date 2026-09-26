package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.common.core.Resource
import com.frogobox.appkeyboard.data.local.productremote.ProductRemoteDao
import com.frogobox.appkeyboard.data.remote.model.DataApiResponse
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import com.frogobox.appkeyboard.model.ProductEntity
import com.frogobox.appkeyboard.repository.data.DataApiRepository
import com.frogobox.appkeyboard.repository.productremote.ProductRemoteRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for ProductRemoteRepositoryImpl.
 * Verifies Room DAO interactions, toggle save behavior, and remote sync batch orchestration.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProductRemoteRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeDao : ProductRemoteDao {
        val list = mutableListOf<ProductEntity>()
        val flow = MutableStateFlow<List<ProductEntity>>(emptyList())
        val idsFlow = MutableStateFlow<List<String>>(emptyList())

        fun sync() {
            flow.value = list.toList()
            idsFlow.value = list.mapNotNull { it.remoteId }
        }

        override suspend fun insert(product: ProductEntity): Long {
            list.add(product)
            sync()
            return product.id.toLong()
        }

        override suspend fun insertAll(products: List<ProductEntity>) {
            list.addAll(products)
            sync()
        }

        override suspend fun update(product: ProductEntity) {
            val idx = list.indexOfFirst { it.id == product.id || (product.remoteId != null && it.remoteId == product.remoteId) }
            if (idx >= 0) list[idx] = product
            sync()
        }

        override suspend fun delete(product: ProductEntity) {
            list.removeIf { it.id == product.id }
            sync()
        }

        override suspend fun deleteById(id: Int) {
            list.removeIf { it.id == id }
            sync()
        }

        override suspend fun deleteByRemoteId(remoteId: String) {
            list.removeIf { it.remoteId == remoteId }
            sync()
        }

        override suspend fun delete(idList: List<Int>) {
            list.removeIf { it.id in idList }
            sync()
        }

        override suspend fun nukeData() {
            list.clear()
            sync()
        }

        override fun getAll(): Flow<List<ProductEntity>> = flow

        override suspend fun getAllSync(): List<ProductEntity> = list.toList()

        override fun getById(id: Int): Flow<ProductEntity?> = flow {
            emit(list.find { it.id == id })
        }

        override suspend fun getByRemoteId(remoteId: String): ProductEntity? =
            list.find { it.remoteId == remoteId }

        override fun getSavedRemoteIds(): Flow<List<String>> = idsFlow

        override fun getByStatusDownload(status: String): Flow<List<ProductEntity>> = flow {
            emit(list.filter { it.statusDownload.equals(status, ignoreCase = true) })
        }

        override fun search(query: String): Flow<List<ProductEntity>> = flow {
            emit(list.filter { it.productName.contains(query, ignoreCase = true) })
        }

        override suspend fun updateDownloadStatusByRemoteId(remoteId: String, status: String, updatedAt: Long) {
            val idx = list.indexOfFirst { it.remoteId == remoteId }
            if (idx >= 0) {
                list[idx] = list[idx].copy(statusDownload = status, updatedAt = updatedAt)
            }
            sync()
        }

        override suspend fun updateDownloadStatusById(id: Int, status: String, updatedAt: Long) {
            val idx = list.indexOfFirst { it.id == id }
            if (idx >= 0) {
                list[idx] = list[idx].copy(statusDownload = status, updatedAt = updatedAt)
            }
            sync()
        }
    }

    private class FakeApi : DataApiRepository {
        var itemsToReturn: List<DataItemResponse> = emptyList()

        override fun fetchDataStream(): Flow<Resource<DataApiResponse>> = emptyFlow()

        override suspend fun fetchData(): Resource<DataApiResponse> {
            return Resource.Success(DataApiResponse(success = true, items = itemsToReturn))
        }
    }

    @Test
    fun testSaveAndGetSavedProducts() = runTest(testDispatcher) {
        val fakeDao = FakeDao()
        val fakeApi = FakeApi()
        val repository = ProductRemoteRepositoryImpl(fakeDao, fakeApi)

        val entity = ProductEntity(id = 1, productName = "Kaos Polos", caption = "Kaos Cotton Combed")
        repository.saveProduct(entity)

        val saved = repository.getSavedProductsStream().first()
        assertEquals(1, saved.size)
        assertEquals("Kaos Polos", saved.first().productName)
    }

    @Test
    fun testToggleSaveRemoteProduct() = runTest(testDispatcher) {
        val fakeDao = FakeDao()
        val fakeApi = FakeApi()
        val repository = ProductRemoteRepositoryImpl(fakeDao, fakeApi)

        val remoteItem = DataItemResponse(id = 10, productName = "Jaket Hoodie")

        // 1. First toggle -> Saved
        val savedResult = repository.toggleSaveRemoteProduct(remoteItem)
        assertTrue(savedResult)
        assertEquals(1, fakeDao.list.size)
        assertEquals("10", fakeDao.list.first().remoteId)

        // 2. Second toggle -> Removed
        val removedResult = repository.toggleSaveRemoteProduct(remoteItem)
        assertFalse(removedResult)
        assertEquals(0, fakeDao.list.size)
    }

    @Test
    fun testSyncAllFromRemote() = runTest(testDispatcher) {
        val fakeDao = FakeDao()
        val fakeApi = FakeApi().apply {
            itemsToReturn = listOf(
                DataItemResponse(id = 1, productName = "Produk A"),
                DataItemResponse(id = 2, productName = "Produk B")
            )
        }
        val repository = ProductRemoteRepositoryImpl(fakeDao, fakeApi)

        val resultFlow = repository.syncAllFromRemote()
        val results = mutableListOf<Resource<Int>>()
        resultFlow.collect { results.add(it) }

        assertTrue(results.any { it is Resource.Success && it.data == 2 })
        assertEquals(2, fakeDao.list.size)
    }

    @Test
    fun testUpdateDownloadStatus() = runTest(testDispatcher) {
        val fakeDao = FakeDao()
        val fakeApi = FakeApi()
        val repository = ProductRemoteRepositoryImpl(fakeDao, fakeApi)

        val product = ProductEntity(
            id = 1,
            remoteId = "rem_123",
            productName = "Video Speaker",
            statusDownload = "Belum"
        )
        fakeDao.list.add(product)

        repository.updateDownloadStatus("rem_123", 1, "Sudah")

        assertEquals("Sudah", fakeDao.list.first().statusDownload)
    }
}
