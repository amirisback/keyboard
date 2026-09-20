package com.frogobox.appkeyboard.ui.test

import com.frogobox.appkeyboard.common.callback.DataResponseCallback
import com.frogobox.appkeyboard.common.callback.StateResponseCallback
import com.frogobox.appkeyboard.common.core.Resource
import com.frogobox.appkeyboard.data.remote.model.DataApiResponse
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.AutoTextLabelType
import com.frogobox.appkeyboard.repository.autotext.AutoTextRepository
import com.frogobox.appkeyboard.repository.data.DataApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Modern Coroutine unit tests for TestViewModel UI state transitions regarding remote data API.
 * Adheres strictly to NO SUPPRESSION, ALWAYS MIGRATE policy using runTest and TestDispatchers.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DataApiViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeAutoTextRepository : AutoTextRepository {
        override fun getAutoText(): Flow<List<AutoTextEntity>> = flowOf(emptyList())
        override fun getAutoTextByLabel(label: AutoTextLabelType): Flow<List<AutoTextEntity>> = flowOf(emptyList())
        override fun getAutoTextByTitle(title: String): Flow<List<AutoTextEntity>> = flowOf(emptyList())
        override fun getAutoTextByBody(body: String): Flow<List<AutoTextEntity>> = flowOf(emptyList())
        override fun getAutoTextByTitleOrBody(keyword: String): Flow<List<AutoTextEntity>> = flowOf(emptyList())
        override suspend fun insertAutoText(autoText: AutoTextEntity) {}
        override suspend fun insertAutoText(autoTexts: List<AutoTextEntity>) {}
        override suspend fun updateAutoText(autoText: AutoTextEntity) {}
        override suspend fun deleteAutoText(autoText: AutoTextEntity) {}
        override suspend fun deleteAutoText(idList: List<Int>) {}
        override suspend fun nukeAutoText() {}

        override fun getAutoText(callback: DataResponseCallback<List<AutoTextEntity>>) {}
        override fun getAutoTextByLabel(label: AutoTextLabelType, callback: DataResponseCallback<List<AutoTextEntity>>) {}
        override fun getAutoTextByTitle(title: String, callback: DataResponseCallback<List<AutoTextEntity>>) {}
        override fun getAutoTextByBody(body: String, callback: DataResponseCallback<List<AutoTextEntity>>) {}
        override fun getAutoTextByTitleOrBody(keyword: String, callback: DataResponseCallback<List<AutoTextEntity>>) {}
        override fun insertAutoText(autoText: AutoTextEntity, callback: StateResponseCallback) {}
        override fun insertAutoText(autoTexts: List<AutoTextEntity>, callback: StateResponseCallback) {}
        override fun updateAutoText(autoText: AutoTextEntity, callback: StateResponseCallback) {}
        override fun deleteAutoText(autoText: AutoTextEntity, callback: StateResponseCallback) {}
        override fun deleteAutoText(idList: List<Int>, callback: StateResponseCallback) {}
        override fun nukeAutoText(callback: StateResponseCallback) {}
    }

    private class FakeDataApiRepository : DataApiRepository {
        var flowToReturn: Flow<Resource<DataApiResponse>> = emptyFlow()
        var directResultToReturn: Resource<DataApiResponse> = Resource.Success(DataApiResponse(code = 200))

        override fun fetchDataStream(): Flow<Resource<DataApiResponse>> = flowToReturn
        override suspend fun fetchData(): Resource<DataApiResponse> = directResultToReturn
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState_isIdle() = runTest(testDispatcher) {
        val fakeAutoTextRepo = FakeAutoTextRepository()
        val fakeDataApiRepo = FakeDataApiRepository()
        val viewModel = TestViewModel(fakeAutoTextRepo, fakeDataApiRepo)

        advanceUntilIdle()

        assertEquals(DataApiUiState.Idle, viewModel.remoteApiUiState.value)
    }

    @Test
    fun testFetchRemoteData_success_transitionsThroughLoadingToSuccess() = runTest(testDispatcher) {
        val fakeAutoTextRepo = FakeAutoTextRepository()
        val fakeDataApiRepo = FakeDataApiRepository()

        val sampleItems = listOf(
            DataItemResponse(id = 1, title = "Wireless Mouse", body = "2.4GHz wireless mouse")
        )
        val successResponse = DataApiResponse(
            code = 200,
            status = "success",
            total = 1,
            lastUpdated = "2026-09-19 15:00:00 WIB",
            data = sampleItems
        )

        fakeDataApiRepo.flowToReturn = flow {
            emit(Resource.Loading)
            emit(Resource.Success(successResponse))
        }

        val viewModel = TestViewModel(fakeAutoTextRepo, fakeDataApiRepo)
        val states = mutableListOf<DataApiUiState>()
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.remoteApiUiState.toList(states)
        }

        viewModel.fetchRemoteData()
        advanceUntilIdle()

        assertEquals(3, states.size)
        assertTrue(states[0] is DataApiUiState.Idle)
        assertTrue(states[1] is DataApiUiState.Loading)
        assertTrue(states[2] is DataApiUiState.Success)

        val successState = states[2] as DataApiUiState.Success
        assertEquals(1, successState.total)
        assertEquals("2026-09-19 15:00:00 WIB", successState.lastUpdatedWib)
        assertEquals(1, successState.items.size)
        assertEquals("Wireless Mouse", successState.items[0].title)

        collectJob.cancel()
    }

    @Test
    fun testFetchRemoteData_error_transitionsThroughLoadingToError() = runTest(testDispatcher) {
        val fakeAutoTextRepo = FakeAutoTextRepository()
        val fakeDataApiRepo = FakeDataApiRepository()
        val errorMsg = "Connection refused. Please ensure the local server is running at http://192.168.100.6:3000."

        fakeDataApiRepo.flowToReturn = flow {
            emit(Resource.Loading)
            emit(Resource.Error(message = errorMsg))
        }

        val viewModel = TestViewModel(fakeAutoTextRepo, fakeDataApiRepo)
        val states = mutableListOf<DataApiUiState>()
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.remoteApiUiState.toList(states)
        }

        viewModel.fetchRemoteData()
        advanceUntilIdle()

        assertEquals(3, states.size)
        assertTrue(states[0] is DataApiUiState.Idle)
        assertTrue(states[1] is DataApiUiState.Loading)
        assertTrue(states[2] is DataApiUiState.Error)

        val errorState = states[2] as DataApiUiState.Error
        assertEquals(errorMsg, errorState.message)

        collectJob.cancel()
    }

    @Test
    fun testResetRemoteData_revertsStateToIdle() = runTest(testDispatcher) {
        val fakeAutoTextRepo = FakeAutoTextRepository()
        val fakeDataApiRepo = FakeDataApiRepository()

        fakeDataApiRepo.flowToReturn = flow {
            emit(Resource.Loading)
        }

        val viewModel = TestViewModel(fakeAutoTextRepo, fakeDataApiRepo)
        advanceUntilIdle()

        viewModel.fetchRemoteData()
        advanceUntilIdle()
        assertTrue(viewModel.remoteApiUiState.value is DataApiUiState.Loading)

        viewModel.resetRemoteData()
        assertEquals(DataApiUiState.Idle, viewModel.remoteApiUiState.value)
    }

    @Test
    fun testInsertText_handlesEmptyAndExistingContent() = runTest(testDispatcher) {
        val fakeAutoTextRepo = FakeAutoTextRepository()
        val fakeDataApiRepo = FakeDataApiRepository()
        val viewModel = TestViewModel(fakeAutoTextRepo, fakeDataApiRepo)
        advanceUntilIdle()

        assertEquals("", viewModel.sandboxText.value)

        viewModel.onInsertText("First line from API")
        assertEquals("First line from API", viewModel.sandboxText.value)

        viewModel.onInsertText("Second line from API")
        assertEquals("First line from API\nSecond line from API", viewModel.sandboxText.value)

        viewModel.onClearText()
        assertEquals("", viewModel.sandboxText.value)
    }
}
