package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.common.core.Resource
import com.frogobox.appkeyboard.data.remote.model.DataApiResponse
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import com.frogobox.appkeyboard.repository.data.DataApiRepository
import com.frogobox.appkeyboard.ui.productremote.ProductRemoteUiState
import com.frogobox.appkeyboard.ui.productremote.ProductRemoteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
 * Validates reactive UI states (Loading, Success, Empty, Error, Retry) and live search filtering.
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

        val viewModel = ProductRemoteViewModel(fakeRepo)
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
    }

    @Test
    fun testInitialFetch_empty_transitionsToEmpty() = runTest(testDispatcher) {
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Loading)
                emit(Resource.Success(DataApiResponse(success = true, items = emptyList())))
            }
        }

        val viewModel = ProductRemoteViewModel(fakeRepo)
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

        val viewModel = ProductRemoteViewModel(fakeRepo)
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

        val viewModel = ProductRemoteViewModel(fakeRepo)
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

        val viewModel = ProductRemoteViewModel(fakeRepo)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Flannel")
        advanceUntilIdle()

        assertEquals("Flannel", viewModel.searchQuery.value)
        assertEquals(1, viewModel.filteredProducts.value.size)
        assertEquals("Kemeja Flannel Pria", viewModel.filteredProducts.value.first().productName)
    }

    @Test
    fun testSearchFiltering_byCaption() = runTest(testDispatcher) {
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Success(DataApiResponse(success = true, items = dummyProducts)))
            }
        }

        val viewModel = ProductRemoteViewModel(fakeRepo)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("stretch")
        advanceUntilIdle()

        assertEquals(1, viewModel.filteredProducts.value.size)
        assertEquals("Celana Chino Slim Fit", viewModel.filteredProducts.value.first().productName)
    }

    @Test
    fun testSearchFiltering_noMatchYieldsEmptyFilteredList() = runTest(testDispatcher) {
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Success(DataApiResponse(success = true, items = dummyProducts)))
            }
        }

        val viewModel = ProductRemoteViewModel(fakeRepo)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Sepatu Kulit")
        advanceUntilIdle()

        assertEquals(3, viewModel.products.value.size)
        assertEquals(0, viewModel.filteredProducts.value.size)

        val uiState = viewModel.uiState.value as ProductRemoteUiState.Success
        assertEquals(0, uiState.filteredProducts.size)
    }

    @Test
    fun testSearchFiltering_blankQueryResetsToAllProducts() = runTest(testDispatcher) {
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Success(DataApiResponse(success = true, items = dummyProducts)))
            }
        }

        val viewModel = ProductRemoteViewModel(fakeRepo)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Chino")
        assertEquals(1, viewModel.filteredProducts.value.size)

        viewModel.onSearchQueryChanged("")
        assertEquals(3, viewModel.filteredProducts.value.size)
    }

    @Test
    fun testSearchFiltering_byProductionProductName() = runTest(testDispatcher) {
        val testProducts = listOf(
            DataItemResponse(id = 1, productName = "Produk Selesai", caption = "Sudah diunduh", statusDownload = "Sudah"),
            DataItemResponse(id = 2, productName = "Produk Baru", caption = "Review produk", statusDownload = "Belum"),
            DataItemResponse(id = 3, productName = "Celana Chino Slim", caption = "Celana chino stretch", statusDownload = "Belum")
        )
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Success(DataApiResponse(success = true, items = testProducts)))
            }
        }

        val viewModel = ProductRemoteViewModel(fakeRepo)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Chino")
        advanceUntilIdle()

        assertEquals(1, viewModel.filteredProducts.value.size)
        assertEquals("Celana Chino Slim", viewModel.filteredProducts.value.first().productName)
    }

    @Test
    fun testSearchFiltering_byOriginalFileName() = runTest(testDispatcher) {
        val testProducts = listOf(
            DataItemResponse(id = 1, productName = "Produk Selesai", originalFileName = "selesai.mp4"),
            DataItemResponse(id = 2, productName = "Produk Baru", originalFileName = "produk_baru.mp4")
        )
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Success(DataApiResponse(success = true, items = testProducts)))
            }
        }

        val viewModel = ProductRemoteViewModel(fakeRepo)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("selesai.mp4")
        advanceUntilIdle()

        assertEquals(1, viewModel.filteredProducts.value.size)
        assertEquals("selesai.mp4", viewModel.filteredProducts.value.first().originalFileName)
    }

    @Test
    fun testSearchFiltering_byStatusDownload() = runTest(testDispatcher) {
        val testProducts = listOf(
            DataItemResponse(id = 1, productName = "Item Selesai", statusDownload = "Sudah"),
            DataItemResponse(id = 2, productName = "Item Baru 1", statusDownload = "Belum"),
            DataItemResponse(id = 3, productName = "Item Baru 2", statusDownload = "Belum")
        )
        val fakeRepo = FakeDataApiRepository().apply {
            flowToReturn = flow {
                emit(Resource.Success(DataApiResponse(success = true, items = testProducts)))
            }
        }

        val viewModel = ProductRemoteViewModel(fakeRepo)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Sudah")
        advanceUntilIdle()

        assertEquals(1, viewModel.filteredProducts.value.size)
        assertEquals("Sudah", viewModel.filteredProducts.value.first().statusDownload)

        viewModel.onSearchQueryChanged("Belum")
        advanceUntilIdle()

        assertEquals(2, viewModel.filteredProducts.value.size)
    }
}
