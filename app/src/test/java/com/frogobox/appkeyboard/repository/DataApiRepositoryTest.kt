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
import org.junit.Assert.assertFalse
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
            productName = "Tongsis Bluetooth",
            caption = "Tongsis Bluetooth Wireless 3-in-1 Remote Shutter",
            originalFileName = "tongsis.mp4",
            fileSize = "3.26 MB",
            fileType = "video/mp4",
            isVideo = true,
            uploadTimestamp = "2026-09-18 04:58:28 WIB",
            driveLink = "https://drive.google.com/file/d/dummy123/view",
            statusDownload = "Sudah"
        ),
        DataItemResponse(
            id = 2,
            rowIndex = 2,
            productName = "Keyboard Mechanical RGB",
            caption = "Keyboard mechanical tactile switches RGB backlit",
            originalFileName = "keyboard.mp4",
            fileSize = "1.50 MB",
            fileType = "video/mp4",
            isVideo = false,
            statusDownload = "Belum"
        )
    )

    private val sampleApiResponse = DataApiResponse(
        success = true,
        total = 2,
        lastUpdated = "2026-09-19 08:00:00 WIB",
        lastUpdatedWib = "2026-09-19 15:00:00 WIB",
        sheetId = "1ox6JTF_IjN2sFOhpEsO3vM4T6Bd3yYfsnO09okOeb1k",
        source = "google-sheets",
        items = sampleItems
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
        assertEquals(true, successResult.data.success)
        assertEquals(2, successResult.data.total)
        assertEquals(2, successResult.data.items?.size)
        assertEquals("Tongsis Bluetooth Wireless 3-in-1 Remote Shutter", successResult.data.items?.get(0)?.caption)
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
                "success": true,
                "total": 1,
                "lastUpdated": "2026-09-19 12:00:00 WIB",
                "lastUpdatedWib": "2026-09-19 19:00:00 WIB",
                "sheetId": "1ox6JTF_IjN2sFOhpEsO3vM4T6Bd3yYfsnO09okOeb1k",
                "source": "google-sheets",
                "items": [
                    {
                        "id": "10",
                        "rowIndex": 10,
                        "productName": "Tripod Mini",
                        "caption": "Tripod Mini Flexible Stand",
                        "originalFileName": "tripod.mp4",
                        "fileType": "video/mp4",
                        "isVideo": true,
                        "fileSize": "2.40 MB",
                        "uploadTimestamp": "2026-09-18 10:00:00 WIB",
                        "driveLink": "https://drive.google.com/file/d/test",
                        "driveFileId": "test",
                        "thumbnailUrl": "https://example.com/thumb.jpg",
                        "previewUrl": "https://example.com/preview",
                        "statusDownload": "Sudah"
                    }
                ]
            }
        """.trimIndent()

        val parsed = gson.fromJson(json, DataApiResponse::class.java)

        assertNotNull(parsed)
        assertEquals(true, parsed.success)
        assertEquals(1, parsed.total)
        assertNotNull(parsed.items)
        assertEquals(1, parsed.items?.size)

        val item = parsed.items?.first()!!
        assertEquals(10, item.displayIndex)
        assertEquals("Tripod Mini", item.displayTitle)
        assertEquals("Tripod Mini Flexible Stand", item.displayBody)
        assertEquals("2026-09-18 10:00:00 WIB", item.displayTimestamp)
        assertEquals(true, item.isVideo)
        assertEquals("2.40 MB", item.fileSize)
        assertEquals("https://drive.google.com/file/d/test", item.driveLink)
        assertEquals("https://example.com/thumb.jpg", item.thumbnailUrl)
        assertTrue(item.isDownloaded)
    }

    @Test
    fun testDataItemResponse_fallbackDisplayProperties() {
        // Item with minimal fields
        val minimalItem = DataItemResponse(id = 5, productName = "Simple Title")
        assertEquals(5, minimalItem.displayIndex)
        assertEquals("Simple Title", minimalItem.displayTitle)
        assertNull(minimalItem.displayBody)
        assertNull(minimalItem.displayTimestamp)

        // Item with only caption
        val captionOnlyItem = DataItemResponse(rowIndex = 12, caption = "Caption Saja")
        assertEquals(12, captionOnlyItem.displayIndex)
        assertEquals("Caption Saja", captionOnlyItem.displayTitle)

        // Completely empty item
        val emptyItem = DataItemResponse()
        assertEquals(0, emptyItem.displayIndex)
        assertEquals("Item #0", emptyItem.displayTitle)
        assertNull(emptyItem.displayBody)
    }

    @Test
    fun testProductionJsonSerializationDeserialization_exactUserPayload() {
        val gson = Gson()
        val json = """
            {
              "success": true,
              "lastUpdated": "2026-09-20T15:36:51.788Z",
              "lastUpdatedWib": "2026-09-20 22:36:51 WIB",
              "total": 30,
              "sheetId": "1ox6JTF_IjN2sFOhpEsO3vM4T6Bd3yYfsnO09okOeb1k",
              "source": "google-sheets",
              "items": [
                {
                  "id": "gviz-32-67890",
                  "uploadTimestamp": "2026-09-20 22:21:57 WIB",
                  "productName": "Produk Selesai",
                  "caption": "Sudah diunduh",
                  "originalFileName": "selesai.mp4",
                  "fileSize": "0 B",
                  "fileType": "video/mp4",
                  "driveLink": "https://drive.google.com/file/d/67890/view",
                  "driveFileId": "67890",
                  "thumbnailUrl": "https://drive.google.com/thumbnail?id=67890&sz=w600",
                  "previewUrl": "https://drive.google.com/file/d/67890/preview",
                  "isVideo": true,
                  "statusDownload": "Sudah",
                  "rowIndex": 32
                },
                {
                  "id": "gviz-31-12345",
                  "uploadTimestamp": "2026-09-20 22:21:56 WIB",
                  "productName": "Produk Baru",
                  "caption": "Review produk #racunshopee",
                  "originalFileName": "produk_baru.mp4",
                  "fileSize": "0 B",
                  "fileType": "video/mp4",
                  "driveLink": "https://drive.google.com/file/d/12345/view",
                  "driveFileId": "12345",
                  "thumbnailUrl": "https://drive.google.com/thumbnail?id=12345&sz=w600",
                  "previewUrl": "https://drive.google.com/file/d/12345/preview",
                  "isVideo": true,
                  "statusDownload": "Belum",
                  "rowIndex": 31
                },
                {
                  "id": "gviz-28-1BrY05mLRCR92g5vo2TCb6MOB69_L8fmF",
                  "uploadTimestamp": "2026-09-20 22:12:57 WIB",
                  "productName": "Glad2Glow Blueberry 5% Ceramide Barrier Repair Moisturizer (30g)",
                  "caption": "Pelembab Wajah Gel Mencerahkan Menenangkan Kemerahan Kulit Kering Sensitif BPOM Original #glad2glow #glad2glowblueberry",
                  "originalFileName": "20260920_221257_glad2glow_blueberry_5_ceramide_barrier_repair_moisturizer_30g.mp4",
                  "fileSize": "3 MB",
                  "fileType": "video/mp4",
                  "driveLink": "https://drive.google.com/file/d/1BrY05mLRCR92g5vo2TCb6MOB69_L8fmF/view?usp=drivesdk",
                  "driveFileId": "1BrY05mLRCR92g5vo2TCb6MOB69_L8fmF",
                  "thumbnailUrl": "https://drive.google.com/thumbnail?id=1BrY05mLRCR92g5vo2TCb6MOB69_L8fmF&sz=w600",
                  "previewUrl": "https://drive.google.com/file/d/1BrY05mLRCR92g5vo2TCb6MOB69_L8fmF/preview",
                  "isVideo": true,
                  "statusDownload": "Belum",
                  "rowIndex": 28
                }
              ]
            }
        """.trimIndent()

        val parsed = gson.fromJson(json, DataApiResponse::class.java)

        assertNotNull(parsed)
        assertEquals(true, parsed.success)
        assertEquals("2026-09-20T15:36:51.788Z", parsed.lastUpdated)
        assertEquals("2026-09-20 22:36:51 WIB", parsed.lastUpdatedWib)
        assertEquals(30, parsed.total)
        assertEquals("1ox6JTF_IjN2sFOhpEsO3vM4T6Bd3yYfsnO09okOeb1k", parsed.sheetId)
        assertEquals("google-sheets", parsed.source)
        assertNotNull(parsed.items)
        val items = parsed.items!!
        assertEquals(3, items.size)

        // Validate Item 1 (Downloaded item)
        val item1 = items[0]
        assertEquals("gviz-32-67890", item1.id)
        assertEquals("2026-09-20 22:21:57 WIB", item1.uploadTimestamp)
        assertEquals("Produk Selesai", item1.productName)
        assertEquals("Produk Selesai", item1.displayTitle)
        assertEquals("Sudah diunduh", item1.caption)
        assertEquals("selesai.mp4", item1.originalFileName)
        assertEquals("0 B", item1.fileSize)
        assertEquals("video/mp4", item1.fileType)
        assertEquals("https://drive.google.com/file/d/67890/view", item1.driveLink)
        assertEquals("67890", item1.driveFileId)
        assertEquals("https://drive.google.com/thumbnail?id=67890&sz=w600", item1.thumbnailUrl)
        assertEquals("https://drive.google.com/file/d/67890/preview", item1.previewUrl)
        assertEquals(true, item1.isVideo)
        assertEquals("Sudah", item1.statusDownload)
        assertTrue(item1.isDownloaded)
        assertEquals(32, item1.rowIndex)
        assertEquals(32, item1.displayIndex)

        // Validate Item 2 (Pending item)
        val item2 = items[1]
        assertEquals("gviz-31-12345", item2.id)
        assertEquals("Produk Baru", item2.productName)
        assertEquals("Produk Baru", item2.displayTitle)
        assertEquals("Belum", item2.statusDownload)
        assertFalse(item2.isDownloaded)
        assertEquals(31, item2.rowIndex)

        // Validate Item 3 (Full product with long name & caption)
        val item3 = items[2]
        assertEquals("Glad2Glow Blueberry 5% Ceramide Barrier Repair Moisturizer (30g)", item3.productName)
        assertEquals("Glad2Glow Blueberry 5% Ceramide Barrier Repair Moisturizer (30g)", item3.displayTitle)
        assertEquals("20260920_221257_glad2glow_blueberry_5_ceramide_barrier_repair_moisturizer_30g.mp4", item3.originalFileName)
        assertEquals("1BrY05mLRCR92g5vo2TCb6MOB69_L8fmF", item3.driveFileId)
        assertEquals(28, item3.rowIndex)
    }
}
