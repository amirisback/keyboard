package com.frogobox.appkeyboard.repository

import com.frogobox.appkeyboard.common.core.Resource
import com.frogobox.appkeyboard.data.remote.DataApiService
import com.frogobox.appkeyboard.data.remote.model.DataApiResponse
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import com.frogobox.appkeyboard.repository.data.DataApiRepositoryImpl
import com.google.gson.Gson
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Modern Coroutine unit tests for DataApiRepository and remote data models.
 * Strictly adheres to NO SUPPRESSION, ALWAYS MIGRATE policy using runTest and StandardTestDispatcher.
 */
class DataApiRepositoryTest {

    private class FakeDataApiService : DataApiService {
        var responseToReturn: Response<DataApiResponse>? = null
        var exceptionToThrow: Throwable? = null

        override suspend fun getData(): Response<DataApiResponse> {
            exceptionToThrow?.let { throw it }
            return responseToReturn
                ?: Response.error(500, "Server Error".toResponseBody("text/plain".toMediaTypeOrNull()))
        }
    }

    private val sampleItems = listOf(
        DataItemResponse(
            id = 1,
            rowIndex = 1,
            title = "Tongsis Bluetooth",
            caption = "Tongsis Bluetooth Wireless 3-in-1 Remote Shutter",
            body = "Deskripsi produk tongsis tripod wireless bluetooth",
            category = "Accessories",
            fileSize = "3.26 MB",
            isVideo = true,
            uploaderName = "Celana Kulot Official",
            uploadTimestamp = "2026-09-18 04:58:28 WIB",
            driveLink = "https://drive.google.com/file/d/dummy123/view",
            isActive = true
        ),
        DataItemResponse(
            id = 2,
            rowIndex = 2,
            title = "Keyboard Mechanical RGB",
            body = "Keyboard mechanical tactile switches RGB backlit",
            category = "Hardware",
            fileSize = "1.50 MB",
            isVideo = false,
            uploaderName = "Tech Store",
            isActive = true
        )
    )

    private val sampleApiResponse = DataApiResponse(
        code = 200,
        status = "success",
        message = "Data fetched successfully",
        total = 2,
        lastUpdated = "2026-09-19 08:00:00 WIB",
        data = sampleItems
    )

    @Test
    fun testFetchDataStream_success_emitsLoadingThenSuccess() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val fakeApiService = FakeDataApiService().apply {
            responseToReturn = Response.success(sampleApiResponse)
        }
        val repository = DataApiRepositoryImpl(fakeApiService, testDispatcher)

        val emissions = repository.fetchDataStream().toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)

        val successResult = emissions[1] as Resource.Success<DataApiResponse>
        assertEquals(200, successResult.data.code)
        assertEquals("success", successResult.data.status)
        assertEquals(2, successResult.data.total)
        assertEquals(2, successResult.data.data?.size)
        assertEquals("Tongsis Bluetooth Wireless 3-in-1 Remote Shutter", successResult.data.data?.get(0)?.caption)
    }

    @Test
    fun testFetchDataStream_connectException_emitsLoadingThenError() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val fakeApiService = FakeDataApiService().apply {
            exceptionToThrow = ConnectException("Connection refused")
        }
        val repository = DataApiRepositoryImpl(fakeApiService, testDispatcher)

        val emissions = repository.fetchDataStream().toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)

        val errorResult = emissions[1] as Resource.Error
        assertTrue(errorResult.message.contains("Connection refused", ignoreCase = true))
        assertTrue(errorResult.cause is ConnectException)
    }

    @Test
    fun testFetchDataStream_socketTimeoutException_emitsLoadingThenError() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val fakeApiService = FakeDataApiService().apply {
            exceptionToThrow = SocketTimeoutException("Read timed out")
        }
        val repository = DataApiRepositoryImpl(fakeApiService, testDispatcher)

        val emissions = repository.fetchDataStream().toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)

        val errorResult = emissions[1] as Resource.Error
        assertTrue(errorResult.message.contains("timed out", ignoreCase = true))
        assertTrue(errorResult.cause is SocketTimeoutException)
    }

    @Test
    fun testFetchDataStream_unknownHostException_emitsLoadingThenError() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val fakeApiService = FakeDataApiService().apply {
            exceptionToThrow = UnknownHostException("192.168.100.6")
        }
        val repository = DataApiRepositoryImpl(fakeApiService, testDispatcher)

        val emissions = repository.fetchDataStream().toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)

        val errorResult = emissions[1] as Resource.Error
        assertTrue(errorResult.message.contains("Unable to resolve host", ignoreCase = true))
    }

    @Test
    fun testFetchDataStream_http404_emitsLoadingThenError() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val fakeApiService = FakeDataApiService().apply {
            responseToReturn = Response.error(
                404,
                "Endpoint not found".toResponseBody("text/plain".toMediaTypeOrNull())
            )
        }
        val repository = DataApiRepositoryImpl(fakeApiService, testDispatcher)

        val emissions = repository.fetchDataStream().toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)

        val errorResult = emissions[1] as Resource.Error
        assertEquals(404, errorResult.code)
        assertTrue(errorResult.message.contains("404"))
    }

    @Test
    fun testFetchData_directSuspend_success() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val fakeApiService = FakeDataApiService().apply {
            responseToReturn = Response.success(sampleApiResponse)
        }
        val repository = DataApiRepositoryImpl(fakeApiService, testDispatcher)

        val result = repository.fetchData()

        assertTrue(result is Resource.Success)
        val successResult = result as Resource.Success<DataApiResponse>
        assertEquals(2, successResult.data.total)
    }

    @Test
    fun testGsonSerializationDeserialization_matchesContract() {
        val gson = Gson()
        val json = """
            {
                "code": 200,
                "status": "success",
                "message": "OK",
                "total": 1,
                "lastUpdated": "2026-09-19 12:00:00 WIB",
                "data": [
                    {
                        "id": 10,
                        "rowIndex": 10,
                        "title": "Tripod Mini",
                        "caption": "Tripod Mini Flexible Stand",
                        "body": "Flexible spider tripod for mobile photography",
                        "category": "Camera",
                        "isVideo": true,
                        "fileSize": "2.40 MB",
                        "uploaderName": "PhotoStudio",
                        "uploadTimestamp": "2026-09-18 10:00:00 WIB",
                        "driveLink": "https://drive.google.com/file/d/test",
                        "thumbnailUrl": "https://example.com/thumb.jpg",
                        "isActive": true
                    }
                ]
            }
        """.trimIndent()

        val parsed = gson.fromJson(json, DataApiResponse::class.java)

        assertNotNull(parsed)
        assertEquals(200, parsed.code)
        assertEquals("success", parsed.status)
        assertEquals(1, parsed.total)
        assertNotNull(parsed.data)
        assertEquals(1, parsed.data?.size)

        val item = parsed.data?.first()!!
        assertEquals(10, item.displayIndex)
        assertEquals("Tripod Mini Flexible Stand", item.displayTitle)
        assertEquals("PhotoStudio", item.displaySubtitle)
        assertEquals("Flexible spider tripod for mobile photography", item.displayBody)
        assertEquals("2026-09-18 10:00:00 WIB", item.displayTimestamp)
        assertEquals(true, item.isVideo)
        assertEquals("2.40 MB", item.fileSize)
        assertEquals("https://drive.google.com/file/d/test", item.driveLink)
        assertEquals("https://example.com/thumb.jpg", item.thumbnailUrl)
    }

    @Test
    fun testDataItemResponse_fallbackDisplayProperties() {
        // Item with minimal fields
        val minimalItem = DataItemResponse(id = 5, title = "Simple Title")
        assertEquals(5, minimalItem.displayIndex)
        assertEquals("Simple Title", minimalItem.displayTitle)
        assertNull(minimalItem.displaySubtitle)
        assertNull(minimalItem.displayBody)
        assertNull(minimalItem.displayTimestamp)

        // Item with only rowIndex and uploaderName
        val uploaderOnlyItem = DataItemResponse(rowIndex = 12, uploaderName = "Admin")
        assertEquals(12, uploaderOnlyItem.displayIndex)
        assertEquals("Admin", uploaderOnlyItem.displayTitle)
        assertEquals("Admin", uploaderOnlyItem.displaySubtitle)

        // Completely empty item
        val emptyItem = DataItemResponse()
        assertEquals(0, emptyItem.displayIndex)
        assertEquals("Item #0", emptyItem.displayTitle)
        assertNull(emptyItem.displaySubtitle)
        assertNull(emptyItem.displayBody)
    }
}
