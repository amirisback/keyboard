package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.common.core.Resource
import com.frogobox.appkeyboard.data.remote.model.DataApiResponse
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import com.frogobox.appkeyboard.model.ProductEntity
import com.frogobox.appkeyboard.model.toProductEntity
import com.frogobox.appkeyboard.repository.data.DataApiRepository
import com.frogobox.appkeyboard.repository.productremote.ProductRemoteRepository
import com.frogobox.appkeyboard.ui.productremote.DownloadStatusFilter
import com.frogobox.appkeyboard.ui.productremote.ProductRemoteUiState
import com.frogobox.appkeyboard.ui.productremote.ProductRemoteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Modern Coroutine unit tests for ProductRemoteViewModel.
 * Validates reactive UI states, Room DB sync, CRUD operations, and download status filtering (Sudah/Belum).
 * Complies strictly with NO SUPPRESSION and ALWAYS MIGRATE policies.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProductRemoteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeDataApiRepository : DataApiRepository {
        var flowToReturn: Flow<Resource<DataApiResponse>> = emptyFlow()
        var directResultToReturn: Resource<DataApiResponse> = Resource.Success(DataApiResponse(success = true))

        override fun fetchDataStream(): Flow<Resource<DataApiResponse>> = flowToReturn
        override suspend fun fetchData(): Resource<DataApiResponse> = directResultToReturn
    }

    private class FakeProductRemoteRepository : ProductRemoteRepository {
        val savedEntities = mutableListOf<ProductEntity>()
        val savedRemoteIdsFlow = MutableStateFlow<Set<String>>(emptySet())
        val savedProductsFlow = MutableStateFlow<List<ProductEntity>>(emptyList())

        fun updateFlows() {
            savedProductsFlow.value = savedEntities.toList()
            savedRemoteIdsFlow.value = savedEntities.mapNotNull { it.remoteId }.toSet()
        }

        override fun getSavedProductsStream(): Flow<List<ProductEntity>> = savedProductsFlow
        override suspend fun getSavedProductsSync(): List<ProductEntity> = savedEntities.toList()
        override fun getSavedRemoteIdsStream(): Flow<Set<String>> = savedRemoteIdsFlow
        override fun getProductById(id: Int): Flow<ProductEntity?> = flow {
            emit(savedEntities.find { it.id == id })
        }
        override suspend fun saveProduct(product: ProductEntity): Long {
            savedEntities.add(product)
            updateFlows()
            return product.id.toLong()
        }
        override suspend fun saveProducts(products: List<ProductEntity>) {
            savedEntities.addAll(products)
            updateFlows()
        }
        override suspend fun updateProduct(product: ProductEntity) {
            val index = savedEntities.indexOfFirst {
                it.id == product.id || (product.remoteId != null && it.remoteId == product.remoteId)
            }
            if (index >= 0) {
                savedEntities[index] = product
            }
            updateFlows()
        }
        override suspend fun deleteProduct(product: ProductEntity) {
            savedEntities.removeIf { it.id == product.id }
            updateFlows()
        }
        override suspend fun deleteProductById(id: Int) {
            savedEntities.removeIf { it.id == id }
            updateFlows()
        }
        override suspend fun deleteProductByRemoteId(remoteId: String) {
            savedEntities.removeIf { it.remoteId == remoteId }
            updateFlows()
        }
        override suspend fun nukeAllSavedProducts() {
            savedEntities.clear()
            updateFlows()
        }
        override fun syncAllFromRemote(): Flow<Resource<Int>> = flow {
            emit(Resource.Loading)
            emit(Resource.Success(savedEntities.size))
        }
        override suspend fun toggleSaveRemoteProduct(item: DataItemResponse): Boolean {
            val exists = savedEntities.any { it.remoteId == item.id }
            return if (exists) {
                savedEntities.removeIf { it.remoteId == item.id }
                updateFlows()
                false
            } else {
                savedEntities.add(item.toProductEntity())
                updateFlows()
                true
            }
        }
        override suspend fun isProductSaved(remoteId: String): Boolean {
            return savedEntities.any { it.remoteId == remoteId }
        }
    }

    private val dummyProducts = listOf(
        DataItemResponse(
            id = 1,
            productName = "Kemeja Flannel Pria",
            caption = "Kemeja Flannel Pria Casual Lengan Panjang",
            driveLink = "https://drive.google.com/file/1",
            originalFileName = "flannel.mp4",
            isVideo = true,
            fileSize = "14.2 MB",
            statusDownload = "Sudah"
        ),
        DataItemResponse(
            id = 2,
            productName = "Celana Chino Slim Fit",
            caption = "Celana Chino Pria Slim Fit Stretch",
            driveLink = "https://drive.google.com/file/2",
            originalFileName = "chino.mp4",
            isVideo = false,
            statusDownload = "Belum"
        ),
        DataItemResponse(
            id = 3,
            productName = "Hijab Paris Premium",
            caption = "Hijab Segiempat Paris Premium Voal",
            driveLink = null,
            originalFileName = "hijab.mp4",
            isVideo = false,
            statusDownload = "Belum"
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialFetch_success_transitionsToSuccessWithItems() = runTest(testDispatcher) {
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Loading)
                emit(Resource.Success(DataApiResponse(success = true, items = dummyProducts)))
            }
        }
        val fakeLocalRepo = FakeProductRemoteRepository()

        val viewModel = ProductRemoteViewModel(fakeRepo, fakeLocalRepo)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.errorMessage.value)
        assertEquals(3, viewModel.products.value.size)
        assertEquals(3, viewModel.filteredProducts.value.size)

        val uiState = viewModel.uiState.value
        assertTrue(uiState is ProductRemoteUiState.Success)
        val successState = uiState as ProductRemoteUiState.Success
        assertEquals(3, successState.products.size)
        assertEquals(3, successState.filteredProducts.size)
        assertEquals("", successState.searchQuery)
        assertEquals(DownloadStatusFilter.ALL, successState.statusFilter)
    }

    @Test
    fun testInitialFetch_empty_transitionsToEmpty() = runTest(testDispatcher) {
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Loading)
                emit(Resource.Success(DataApiResponse(success = true, items = emptyList())))
            }
        }
        val fakeLocalRepo = FakeProductRemoteRepository()

        val viewModel = ProductRemoteViewModel(fakeRepo, fakeLocalRepo)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.products.value.isEmpty())
        assertTrue(viewModel.filteredProducts.value.isEmpty())
        assertTrue(viewModel.uiState.value is ProductRemoteUiState.Empty)
    }

    @Test
    fun testInitialFetch_error_transitionsToError() = runTest(testDispatcher) {
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Loading)
                emit(Resource.Error("Connection refused: 192.168.100.6:3000"))
            }
        }
        val fakeLocalRepo = FakeProductRemoteRepository()

        val viewModel = ProductRemoteViewModel(fakeRepo, fakeLocalRepo)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertEquals("Connection refused: 192.168.100.6:3000", viewModel.errorMessage.value)

        val uiState = viewModel.uiState.value
        assertTrue(uiState is ProductRemoteUiState.Error)
        val errorState = uiState as ProductRemoteUiState.Error
        assertEquals("Connection refused: 192.168.100.6:3000", errorState.message)
        assertTrue(errorState.isOffline)
    }

    @Test
    fun testRetry_fetchesAgainAndRecovers() = runTest(testDispatcher) {
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Loading)
                emit(Resource.Error("Failed to connect to host"))
            }
        }
        val fakeLocalRepo = FakeProductRemoteRepository()

        val viewModel = ProductRemoteViewModel(fakeRepo, fakeLocalRepo)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is ProductRemoteUiState.Error)

        // Simulate retry recovery
        fakeRepo.flowToReturn = flow {
            emit(Resource.Loading)
            emit(Resource.Success(DataApiResponse(success = true, items = dummyProducts)))
        }

        viewModel.retry()
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.errorMessage.value)
        assertEquals(3, viewModel.products.value.size)
        assertTrue(viewModel.uiState.value is ProductRemoteUiState.Success)
    }

    @Test
    fun testSearchFiltering_byProductName() = runTest(testDispatcher) {
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Success(DataApiResponse(success = true, items = dummyProducts)))
            }
        }
        val fakeLocalRepo = FakeProductRemoteRepository()

        val viewModel = ProductRemoteViewModel(fakeRepo, fakeLocalRepo)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Flannel")
        advanceUntilIdle()

        assertEquals("Flannel", viewModel.searchQuery.value)
        assertEquals(1, viewModel.filteredProducts.value.size)
        assertEquals("Kemeja Flannel Pria", viewModel.filteredProducts.value.first().productName)
    }

    @Test
    fun testDownloadStatusFilter_downloaded_showsOnlySudah() = runTest(testDispatcher) {
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Success(DataApiResponse(success = true, items = dummyProducts)))
            }
        }
        val fakeLocalRepo = FakeProductRemoteRepository()

        val viewModel = ProductRemoteViewModel(fakeRepo, fakeLocalRepo)
        advanceUntilIdle()

        viewModel.onDownloadStatusFilterChanged(DownloadStatusFilter.DOWNLOADED)
        advanceUntilIdle()

        assertEquals(DownloadStatusFilter.DOWNLOADED, viewModel.downloadStatusFilter.value)
        assertEquals(1, viewModel.filteredProducts.value.size)
        assertEquals("Kemeja Flannel Pria", viewModel.filteredProducts.value.first().productName)
        assertTrue(viewModel.filteredProducts.value.first().isDownloaded)
    }

    @Test
    fun testDownloadStatusFilter_notDownloaded_showsOnlyBelum() = runTest(testDispatcher) {
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Success(DataApiResponse(success = true, items = dummyProducts)))
            }
        }
        val fakeLocalRepo = FakeProductRemoteRepository()

        val viewModel = ProductRemoteViewModel(fakeRepo, fakeLocalRepo)
        advanceUntilIdle()

        viewModel.onDownloadStatusFilterChanged(DownloadStatusFilter.NOT_DOWNLOADED)
        advanceUntilIdle()

        assertEquals(DownloadStatusFilter.NOT_DOWNLOADED, viewModel.downloadStatusFilter.value)
        assertEquals(2, viewModel.filteredProducts.value.size)
        assertTrue(viewModel.filteredProducts.value.all { !it.isDownloaded })
    }

    @Test
    fun testDownloadStatusFilter_combinedWithSearchQuery() = runTest(testDispatcher) {
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Success(DataApiResponse(success = true, items = dummyProducts)))
            }
        }
        val fakeLocalRepo = FakeProductRemoteRepository()

        val viewModel = ProductRemoteViewModel(fakeRepo, fakeLocalRepo)
        advanceUntilIdle()

        // Filter: Belum + Search: "Hijab"
        viewModel.onDownloadStatusFilterChanged(DownloadStatusFilter.NOT_DOWNLOADED)
        viewModel.onSearchQueryChanged("Hijab")
        advanceUntilIdle()

        assertEquals(1, viewModel.filteredProducts.value.size)
        assertEquals("Hijab Paris Premium", viewModel.filteredProducts.value.first().productName)

        // Filter: Sudah + Search: "Hijab" -> should yield 0 matches
        viewModel.onDownloadStatusFilterChanged(DownloadStatusFilter.DOWNLOADED)
        advanceUntilIdle()

        assertEquals(0, viewModel.filteredProducts.value.size)
    }

    @Test
    fun testToggleSaveProduct_savesAndDeletesInRoomDb() = runTest(testDispatcher) {
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Success(DataApiResponse(success = true, items = dummyProducts)))
            }
        }
        val fakeLocalRepo = FakeProductRemoteRepository()

        val viewModel = ProductRemoteViewModel(fakeRepo, fakeLocalRepo)
        advanceUntilIdle()

        val itemToToggle = dummyProducts.first()

        // 1. Toggle Save: Should save to Room DB
        viewModel.toggleSaveProduct(itemToToggle)
        advanceUntilIdle()

        assertEquals(1, fakeLocalRepo.savedEntities.size)
        assertEquals(itemToToggle.id, fakeLocalRepo.savedEntities.first().remoteId)
        assertTrue(viewModel.savedRemoteIds.value.contains(itemToToggle.id))
        assertNotNull(viewModel.syncMessage.value)

        // 2. Toggle Save again: Should remove from Room DB
        viewModel.toggleSaveProduct(itemToToggle)
        advanceUntilIdle()

        assertEquals(0, fakeLocalRepo.savedEntities.size)
        assertFalse(viewModel.savedRemoteIds.value.contains(itemToToggle.id))
    }

    @Test
    fun testCreateProduct_manualCrud() = runTest(testDispatcher) {
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Success(DataApiResponse(success = true, items = dummyProducts)))
            }
        }
        val fakeLocalRepo = FakeProductRemoteRepository()

        val viewModel = ProductRemoteViewModel(fakeRepo, fakeLocalRepo)
        advanceUntilIdle()

        val newProduct = ProductEntity(
            id = 99,
            productName = "Sepatu Sneakers Lokal",
            caption = "Sneakers Kanvas Casual Keren",
            statusDownload = "Sudah"
        )

        viewModel.createProduct(newProduct)
        advanceUntilIdle()

        assertEquals(1, fakeLocalRepo.savedEntities.size)
        assertEquals(4, viewModel.products.value.size)
        assertEquals("Sepatu Sneakers Lokal", viewModel.products.value.first().productName)
    }

    @Test
    fun testDeleteProduct_manualCrud() = runTest(testDispatcher) {
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Success(DataApiResponse(success = true, items = dummyProducts)))
            }
        }
        val fakeLocalRepo = FakeProductRemoteRepository()

        val viewModel = ProductRemoteViewModel(fakeRepo, fakeLocalRepo)
        advanceUntilIdle()

        val itemToDelete = dummyProducts.first()
        viewModel.deleteProduct(itemToDelete)
        advanceUntilIdle()

        assertEquals(2, viewModel.products.value.size)
        assertFalse(viewModel.products.value.any { it.id == itemToDelete.id })
    }
}
