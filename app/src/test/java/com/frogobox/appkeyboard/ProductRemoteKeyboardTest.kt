package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.common.core.Resource
import com.frogobox.appkeyboard.data.remote.DataApiService
import com.frogobox.appkeyboard.data.remote.model.DataApiResponse
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.repository.data.DataApiRepositoryImpl
import com.frogobox.appkeyboard.ui.keyboard.productremote.ProductRemoteOutputMode
import com.frogobox.appkeyboard.ui.keyboard.productremote.toCommitTextByMode
import com.frogobox.appkeyboard.ui.keyboard.productremote.toFormattedCommitText
import com.frogobox.appkeyboard.ui.keyboard.productremote.toProductCaptionCommitText
import com.frogobox.appkeyboard.ui.keyboard.productremote.toProductTitleCommitText
import com.frogobox.appkeyboard.ui.keyboard.root.KeyboardPanelState
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

/**
 * Unit test suite for Product Remote Keyboard:
 * - DataItemResponse.toFormattedCommitText() text generation
 * - Remote API state machine transitions (Loading -> Success, Loading -> Empty, Loading -> Error -> Retry)
 * - KeyboardFeatureType and KeyboardPanelState integration
 */
class ProductRemoteKeyboardTest {

    private class FakeDataApiService : DataApiService {
        var responseToReturn: Response<DataApiResponse>? = null
        var exceptionToThrow: Throwable? = null

        override suspend fun getData(): Response<DataApiResponse> {
            exceptionToThrow?.let { throw it }
            return responseToReturn
                ?: Response.error(500, "Server Error".toResponseBody("text/plain".toMediaTypeOrNull()))
        }
    }

    @Test
    fun testToFormattedCommitText_allFieldsPresent() {
        val item = DataItemResponse(
            id = 1,
            productName = "Promo Spesial",
            caption = "Diskon hingga 70% berlaku untuk semua produk ready stock.",
            driveLink = "https://drive.google.com/file/d/promo123/view",
            originalFileName = "promo.mp4"
        )

        val expected = buildString {
            append("Promo Spesial\n")
            append("Diskon hingga 70% berlaku untuk semua produk ready stock.\n\n")
            append("Link: https://drive.google.com/file/d/promo123/view\n")
            append("File: promo.mp4")
        }

        assertEquals(expected, item.toFormattedCommitText())
    }

    @Test
    fun testToFormattedCommitText_withoutLink() {
        val item = DataItemResponse(
            id = 2,
            productName = "Pemberitahuan Toko",
            caption = "Pesanan di atas jam 15:00 akan dikirim keesokan harinya.",
            driveLink = null,
            originalFileName = "notice.png"
        )

        val expected = buildString {
            append("Pemberitahuan Toko\n")
            append("Pesanan di atas jam 15:00 akan dikirim keesokan harinya.\n")
            append("File: notice.png")
        }

        assertEquals(expected, item.toFormattedCommitText())
    }

    @Test
    fun testToFormattedCommitText_withoutFile() {
        val item = DataItemResponse(
            id = 3,
            productName = "Format Pemesanan",
            caption = "Nama:\nAlamat:\nNo HP:\nPesanan:",
            driveLink = "https://drive.google.com/file/d/format_order",
            originalFileName = null
        )

        val expected = buildString {
            append("Format Pemesanan\n")
            append("Nama:\nAlamat:\nNo HP:\nPesanan:\n\n")
            append("Link: https://drive.google.com/file/d/format_order")
        }

        assertEquals(expected, item.toFormattedCommitText())
    }

    @Test
    fun testToFormattedCommitText_onlyTitleAndBody() {
        val item = DataItemResponse(
            id = 4,
            productName = "Rekening Resmi",
            caption = "BCA 1234567890 a/n Toko Resmi",
            driveLink = null,
            originalFileName = null
        )

        val expected = "Rekening Resmi\nBCA 1234567890 a/n Toko Resmi"
        assertEquals(expected, item.toFormattedCommitText())
    }

    @Test
    fun testToFormattedCommitText_contentEqualsHeadline() {
        val item = DataItemResponse(
            id = 5,
            productName = "Terima Kasih",
            caption = "Terima Kasih",
            driveLink = null,
            originalFileName = null
        )

        // When content is identical to headline, duplicate line is skipped
        val expected = "Terima Kasih"
        assertEquals(expected, item.toFormattedCommitText())
    }

    @Test
    fun testToFormattedCommitText_blankOrNullBody() {
        val item = DataItemResponse(
            id = 6,
            productName = "Template Singkat",
            caption = "   ",
            driveLink = "https://drive.google.com/file/d/brief",
            originalFileName = "brief.pdf"
        )

        val expected = "Template Singkat\n\nLink: https://drive.google.com/file/d/brief\nFile: brief.pdf"
        assertEquals(expected, item.toFormattedCommitText())
    }

    @Test
    fun testKeyboardFeatureTypeAndPanelMapping() {
        val feature = KeyboardFeatureType.PRODUCT_REMOTE
        assertEquals("menu_product_remote", feature.id)
        assertEquals("Product Remote", feature.text)
        assertEquals(R.drawable.ic_menu_ps_sale, feature.icon)

        val mappedPanel = KeyboardPanelState.fromFeature(feature)
        assertEquals(KeyboardPanelState.PRODUCT_REMOTE, mappedPanel)
        assertNotNull(mappedPanel)
        assertFalse(mappedPanel!!.isTemplate)
        assertNull(mappedPanel.templateFeatureType)
    }

    @Test
    fun testStateTransitions_loadingToSuccess() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val fakeApiService = FakeDataApiService().apply {
            responseToReturn = Response.success(
                DataApiResponse(
                    success = true,
                    total = 1,
                    items = listOf(
                        DataItemResponse(
                            id = "gviz-10",
                            productName = "Promo Live",
                            caption = "Voucher diskon 20%",
                            rowIndex = 10
                        )
                    )
                )
            )
        }

        val repository = DataApiRepositoryImpl(fakeApiService, testDispatcher)
        val emissions = repository.fetchDataStream().toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)

        val success = emissions[1] as Resource.Success<DataApiResponse>
        assertEquals(1, success.data.items?.size)
        assertEquals("Promo Live", success.data.items?.first()?.productName)
    }

    @Test
    fun testStateTransitions_loadingToEmpty() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val fakeApiService = FakeDataApiService().apply {
            responseToReturn = Response.success(
                DataApiResponse(
                    success = true,
                    total = 0,
                    items = emptyList()
                )
            )
        }

        val repository = DataApiRepositoryImpl(fakeApiService, testDispatcher)
        val emissions = repository.fetchDataStream().toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)

        val success = emissions[1] as Resource.Success<DataApiResponse>
        assertTrue(success.data.items.orEmpty().isEmpty())
    }

    @Test
    fun testStateTransitions_loadingToErrorThenRetrySuccess() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val fakeApiService = FakeDataApiService().apply {
            exceptionToThrow = ConnectException("Failed to connect to 192.168.100.6:3000")
        }

        val repository = DataApiRepositoryImpl(fakeApiService, testDispatcher)

        // Attempt 1: Error
        val attempt1 = repository.fetchDataStream().toList()
        assertEquals(2, attempt1.size)
        assertTrue(attempt1[0] is Resource.Loading)
        assertTrue(attempt1[1] is Resource.Error)
        val errorResult = attempt1[1] as Resource.Error
        assertTrue(errorResult.message.contains("Connection refused", ignoreCase = true))

        // Attempt 2: Retry succeeds
        fakeApiService.exceptionToThrow = null
        fakeApiService.responseToReturn = Response.success(
            DataApiResponse(
                success = true,
                total = 1,
                items = listOf(
                    DataItemResponse(id = "gviz-20", productName = "Recovered Template", rowIndex = 20)
                )
            )
        )

        val attempt2 = repository.fetchDataStream().toList()
        assertEquals(2, attempt2.size)
        assertTrue(attempt2[0] is Resource.Loading)
        assertTrue(attempt2[1] is Resource.Success)
        val successResult = attempt2[1] as Resource.Success<DataApiResponse>
        assertEquals("Recovered Template", successResult.data.items?.first()?.productName)
    }

    @Test
    fun testToProductCaptionCommitText_whenCaptionPresent_returnsCaption() {
        val item = DataItemResponse(
            id = 1,
            productName = "Kemeja Flannel Pria",
            caption = "Kemeja Flannel Pria Casual Lengan Panjang Promo Gajian"
        )
        assertEquals("Kemeja Flannel Pria Casual Lengan Panjang Promo Gajian", item.toProductCaptionCommitText())
    }

    @Test
    fun testToProductCaptionCommitText_whenCaptionBlank_fallsBackToProductName() {
        val item = DataItemResponse(
            id = 2,
            productName = "Celana Chino Slim Fit Stretch Nyaman",
            caption = "   "
        )
        assertEquals("Celana Chino Slim Fit Stretch Nyaman", item.toProductCaptionCommitText())
    }

    @Test
    fun testToProductCaptionCommitText_whenCaptionAndProductNameBlank_fallsBackToDisplayIndex() {
        val item = DataItemResponse(
            id = 3,
            productName = "",
            caption = null,
            rowIndex = 3
        )
        assertEquals("Item #3", item.toProductCaptionCommitText())
    }

    @Test
    fun testToProductCaptionCommitText_whenAllBlank_fallsBackToDisplayIndex() {
        val item = DataItemResponse(
            id = 4,
            productName = null,
            caption = null,
            rowIndex = 4
        )
        assertEquals("Item #4", item.toProductCaptionCommitText())
    }

    @Test
    fun testToProductTitleCommitText_whenProductNamePresent_returnsProductName() {
        val item = DataItemResponse(
            id = 50,
            productName = "Glad2Glow Blueberry 5% Ceramide Barrier Repair Moisturizer (30g)",
            caption = "Pelembab Wajah Gel Mencerahkan Menenangkan Kemerahan Kulit Kering #glad2glow"
        )
        assertEquals("Glad2Glow Blueberry 5% Ceramide Barrier Repair Moisturizer (30g)", item.toProductTitleCommitText())
        assertEquals("Pelembab Wajah Gel Mencerahkan Menenangkan Kemerahan Kulit Kering #glad2glow", item.toProductCaptionCommitText())
    }

    @Test
    fun testToProductTitleCommitText_whenProductNameBlank_fallsBackToCaption() {
        val item = DataItemResponse(
            id = 6,
            productName = "   ",
            caption = "Tas Ransel Laptop Waterproof"
        )
        assertEquals("Tas Ransel Laptop Waterproof", item.toProductTitleCommitText())
    }

    @Test
    fun testToProductTitleCommitText_whenAllBlank_fallsBackToProdukIndex() {
        val item = DataItemResponse(
            id = 7,
            productName = null,
            caption = null,
            rowIndex = 7
        )
        assertEquals("Produk #7", item.toProductTitleCommitText())
    }

    @Test
    fun testToCommitTextByMode_captionAndTitleModes() {
        val item = DataItemResponse(
            id = 8,
            productName = "Jam Tangan Chrono",
            caption = "Jam Tangan Pria Mewah Tahan Air Original"
        )

        val captionOutput = item.toCommitTextByMode(ProductRemoteOutputMode.CAPTION)
        assertEquals("Jam Tangan Pria Mewah Tahan Air Original", captionOutput)

        val titleOutput = item.toCommitTextByMode(ProductRemoteOutputMode.TITLE)
        assertEquals("Jam Tangan Chrono", titleOutput)
    }

    @Test
    fun testProductRemoteOutputMode_labelsAndEntries() {
        assertEquals(2, ProductRemoteOutputMode.entries.size)
        assertEquals("Caption", ProductRemoteOutputMode.CAPTION.shortLabel)
        assertEquals("Judul", ProductRemoteOutputMode.TITLE.shortLabel)
        assertTrue(ProductRemoteOutputMode.CAPTION.displayName.contains("Caption"))
        assertTrue(ProductRemoteOutputMode.TITLE.displayName.contains("Judul"))
    }

    @Test
    fun testDataItemResponse_fileNameAndTimestampSafety() {
        val item = DataItemResponse(
            id = "gviz-32-67890",
            productName = "Produk Selesai",
            caption = "Sudah diunduh",
            originalFileName = "selesai.mp4",
            uploadTimestamp = "2026-09-20 22:21:57 WIB",
            statusDownload = "Sudah",
            rowIndex = 32
        )

        // Verifies originalFileName is preserved accurately
        assertEquals("selesai.mp4", item.originalFileName)
        assertEquals("2026-09-20 22:21:57 WIB", item.displayTimestamp)
        assertEquals("Produk Selesai", item.displayTitle)
        assertEquals("Sudah diunduh", item.displayBody)
        assertTrue(item.isDownloaded)
    }

    @Test
    fun testDataItemResponse_nullFieldsHandling() {
        val minimalItem = DataItemResponse(
            id = "gviz-10",
            productName = "Produk Sederhana",
            rowIndex = 10
        )

        assertNull(minimalItem.originalFileName)
        assertNull(minimalItem.displayTimestamp)
        assertNull(minimalItem.displayBody)
        assertFalse(minimalItem.isDownloaded)
        assertEquals("Produk Sederhana", minimalItem.displayTitle)
    }
}
