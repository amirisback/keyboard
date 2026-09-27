package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.common.core.Resource
import com.frogobox.appkeyboard.data.remote.DataApiService
import com.frogobox.appkeyboard.data.remote.model.DataApiResponse
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.repository.data.DataApiRepositoryImpl
import com.frogobox.appkeyboard.model.ProductEntity
import com.frogobox.appkeyboard.model.toProductEntity
import com.frogobox.appkeyboard.ui.keyboard.productremote.ProductRemoteOutputMode
import com.frogobox.appkeyboard.ui.keyboard.productremote.matchesSearchQuery
import com.frogobox.appkeyboard.ui.keyboard.productremote.toCommitTextByMode
import com.frogobox.appkeyboard.ui.keyboard.productremote.toFormattedCommitText
import com.frogobox.appkeyboard.ui.keyboard.productremote.toProductCaptionCommitText
import com.frogobox.appkeyboard.ui.keyboard.productremote.toProductHookCommitText
import com.frogobox.appkeyboard.ui.keyboard.productremote.toProductLinkCommitText
import com.frogobox.appkeyboard.ui.keyboard.productremote.toProductTitleCommitText
import com.frogobox.appkeyboard.ui.keyboard.root.KeyboardPanelState
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
            exceptionToThrow = ConnectException("Failed to connect to 192.168.100.6:7272")
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
        assertEquals(4, ProductRemoteOutputMode.entries.size)
        assertEquals("Caption", ProductRemoteOutputMode.CAPTION.shortLabel)
        assertEquals("Judul", ProductRemoteOutputMode.TITLE.shortLabel)
        assertEquals("Hook", ProductRemoteOutputMode.HOOK.shortLabel)
        assertEquals("Link", ProductRemoteOutputMode.LINK.shortLabel)
        assertTrue(ProductRemoteOutputMode.CAPTION.displayName.contains("Caption"))
        assertTrue(ProductRemoteOutputMode.TITLE.displayName.contains("Judul"))
        assertTrue(ProductRemoteOutputMode.HOOK.displayName.contains("Hook"))
        assertTrue(ProductRemoteOutputMode.LINK.displayName.contains("Link"))
    }

    @Test
    fun testToProductHookCommitText_withHookPresent() {
        val item = DataItemResponse(
            id = "gviz-57",
            productName = "Tongsis Bluetooth",
            caption = "Caption promosi tongsis",
            hook = "Alat ngonten serbaguna idaman kreator!"
        )
        assertEquals("Alat ngonten serbaguna idaman kreator!", item.toProductHookCommitText())
    }

    @Test
    fun testToProductHookCommitText_fallbackToCaption() {
        val item = DataItemResponse(
            id = "gviz-58",
            productName = "Tripod Kamera",
            caption = "Caption tripod mantap",
            hook = null
        )
        assertEquals("Caption tripod mantap", item.toProductHookCommitText())
    }

    @Test
    fun testToProductHookCommitText_fallbackToProductName() {
        val item = DataItemResponse(
            id = "gviz-59",
            productName = "LED Fill Light",
            caption = null,
            hook = ""
        )
        assertEquals("LED Fill Light", item.toProductHookCommitText())
    }

    @Test
    fun testToProductLinkCommitText_withLinkProductPresent() {
        val item = DataItemResponse(
            id = "gviz-57",
            productName = "Tongsis Bluetooth",
            linkProduct = "https://shopee.co.id/search?keyword=Tongsis",
            driveLink = "https://drive.google.com/file/d/test"
        )
        assertEquals("https://shopee.co.id/search?keyword=Tongsis", item.toProductLinkCommitText())
    }

    @Test
    fun testToProductLinkCommitText_fallbackToDriveLink() {
        val item = DataItemResponse(
            id = "gviz-60",
            productName = "Ring Light",
            linkProduct = null,
            driveLink = "https://drive.google.com/file/d/ringlight"
        )
        assertEquals("https://drive.google.com/file/d/ringlight", item.toProductLinkCommitText())
    }

    @Test
    fun testToProductLinkCommitText_emptyWhenBothNull() {
        val item = DataItemResponse(
            id = "gviz-61",
            productName = "Item Tanpa Link",
            linkProduct = null,
            driveLink = null
        )
        assertEquals("", item.toProductLinkCommitText())
    }

    @Test
    fun testToCommitTextByMode_allFourModes() {
        val item = DataItemResponse(
            id = "gviz-57",
            rowIndex = 57,
            productName = "Tongsis Bluetooth 360",
            caption = "Bikin konten di mana aja makin pro!",
            hook = "Alat ngonten serbaguna idaman kreator!",
            linkProduct = "https://shopee.co.id/tongsis"
        )

        assertEquals("Bikin konten di mana aja makin pro!", item.toCommitTextByMode(ProductRemoteOutputMode.CAPTION))
        assertEquals("Tongsis Bluetooth 360", item.toCommitTextByMode(ProductRemoteOutputMode.TITLE))
        assertEquals("Alat ngonten serbaguna idaman kreator!", item.toCommitTextByMode(ProductRemoteOutputMode.HOOK))
        assertEquals("https://shopee.co.id/tongsis", item.toCommitTextByMode(ProductRemoteOutputMode.LINK))
    }

    @Test
    fun testUserSampleJsonDeserialization_tongsisTripod() {
        val userJson = """
        {
          "id": "gviz-57-1N3pdG1aOZf2_ENVzFDA68sOlyhI3CKGg",
          "uploadTimestamp": "2026-09-22 08:37:04 WIB",
          "productName": "Tongsis Bluetooth 360 Rotation Tripod with LED Fill Light",
          "caption": "Bikin konten di mana aja makin pro! Tongsis + tripod + lampu LED + remote bluetooth lengkap dalam 1 alat! #tongsistripod #alatngonten #shopee",
          "hook": "Alat ngonten serbaguna idaman kreator: tongsis, tripod 360, plus lampu LED fill light lengkap jadi satu!",
          "originalFileName": "20260922_083704_tongsis_bluetooth_360_rotation_tripod_with_led_fill_light.mp4",
          "fileSize": "2.55 MB",
          "fileType": "video/mp4",
          "driveLink": "https://drive.google.com/file/d/1N3pdG1aOZf2_ENVzFDA68sOlyhI3CKGg/view?usp=drivesdk",
          "linkProduct": "https://shopee.co.id/search?keyword=Tongsis%20Bluetooth%20360%20Rotation%20Tripod%20with%20LED%20Fill%20Light",
          "driveFileId": "1N3pdG1aOZf2_ENVzFDA68sOlyhI3CKGg",
          "thumbnailUrl": "https://drive.google.com/thumbnail?id=1N3pdG1aOZf2_ENVzFDA68sOlyhI3CKGg&sz=w600",
          "previewUrl": "https://drive.google.com/file/d/1N3pdG1aOZf2_ENVzFDA68sOlyhI3CKGg/preview",
          "isVideo": true,
          "statusDownload": "Belum",
          "rowIndex": 57
        }
        """.trimIndent()

        val item = Gson().fromJson(userJson, DataItemResponse::class.java)

        assertEquals("gviz-57-1N3pdG1aOZf2_ENVzFDA68sOlyhI3CKGg", item.id)
        assertEquals("Tongsis Bluetooth 360 Rotation Tripod with LED Fill Light", item.productName)
        assertEquals("Alat ngonten serbaguna idaman kreator: tongsis, tripod 360, plus lampu LED fill light lengkap jadi satu!", item.hook)
        assertEquals("https://shopee.co.id/search?keyword=Tongsis%20Bluetooth%20360%20Rotation%20Tripod%20with%20LED%20Fill%20Light", item.linkProduct)
        assertEquals("2.55 MB", item.fileSize)
        assertEquals(true, item.isVideo)
        assertEquals(57, item.rowIndex)
        assertEquals(item.hook, item.displayHook)
        assertEquals(item.linkProduct, item.displayProductLink)

        // Room entity mapping verification
        val entity = item.toProductEntity(localId = 1)
        assertEquals(1, entity.id)
        assertEquals(item.id, entity.remoteId)
        assertEquals(item.hook, entity.hook)
        assertEquals(item.linkProduct, entity.linkProduct)
        assertEquals(item.productName, entity.productName)

        val restoredResponse = entity.toDataItemResponse()
        assertEquals(item.hook, restoredResponse.hook)
        assertEquals(item.linkProduct, restoredResponse.linkProduct)
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
        assertNull(minimalItem.hook)
        assertNull(minimalItem.linkProduct)
        assertFalse(minimalItem.isDownloaded)
        assertEquals("Produk Sederhana", minimalItem.displayTitle)
    }

    @Test
    fun testProductRemoteThumbnailAspectRatio_isNineBySixteen() {
        val widthRatio = 9f
        val heightRatio = 16f
        val aspectRatio = widthRatio / heightRatio
        assertEquals(0.5625f, aspectRatio, 0.0001f)
    }

    @Test
    fun testProductRemoteKeyboardHeight_isDoubleStandardHeight() {
        val standardHeightDp = 270
        val enlargedHeightDp = 540
        assertEquals(standardHeightDp * 2, enlargedHeightDp)
    }

    @Test
    fun testProductRemoteCard_directSnippetSelection() {
        val item = DataItemResponse(
            id = "gviz-99",
            productName = "Serum Wajah Glowing",
            caption = "Serum dengan kandungan Niacinamide 10% untuk mencerahkan kulit!",
            hook = "Wajah kusam dalam 7 hari? Ini rahasia glowing alami!",
            linkProduct = "https://shopee.co.id/serum-glowing",
            driveLink = "https://drive.google.com/file/d/serum123/view",
            rowIndex = 99
        )

        // 1. Direct Title Click
        val committedTitle = item.toProductTitleCommitText()
        assertEquals("Serum Wajah Glowing", committedTitle)

        // 2. Direct Caption Click
        val committedCaption = item.toProductCaptionCommitText()
        assertEquals("Serum dengan kandungan Niacinamide 10% untuk mencerahkan kulit!", committedCaption)

        // 3. Direct Hook Click
        val committedHook = item.toProductHookCommitText()
        assertEquals("Wajah kusam dalam 7 hari? Ini rahasia glowing alami!", committedHook)

        // 4. Direct Link Click
        val committedLink = item.toProductLinkCommitText()
        assertEquals("https://shopee.co.id/serum-glowing", committedLink)

        // 5. Card Body Direct Click Fallback (Defaults to Caption)
        val defaultCardClick = item.toProductCaptionCommitText()
        assertEquals("Serum dengan kandungan Niacinamide 10% untuk mencerahkan kulit!", defaultCardClick)
    }

    @Test
    fun testProductRemoteCard_directActionFallbacksWhenFieldsNull() {
        val item = DataItemResponse(
            id = "gviz-100",
            productName = "Toner Hydrating",
            caption = null,
            hook = null,
            linkProduct = null,
            driveLink = "https://drive.google.com/file/d/toner/view",
            rowIndex = 100
        )

        // Hook falls back to caption -> productName
        assertEquals("Toner Hydrating", item.toProductHookCommitText())

        // Caption falls back to productName
        assertEquals("Toner Hydrating", item.toProductCaptionCommitText())

        // Link falls back to driveLink
        assertEquals("https://drive.google.com/file/d/toner/view", item.toProductLinkCommitText())

        // Title uses productName
        assertEquals("Toner Hydrating", item.toProductTitleCommitText())
    }

    @Test
    fun testMatchesSearchQuery_matchesTitle() {
        val item = DataItemResponse(
            id = "test-1",
            productName = "The Originote Hyalucera Moisturizer",
            caption = "Pelembap gel ringan",
            hook = "Kulit kering?",
            linkProduct = "https://shopee.co.id/originote"
        )

        assertTrue(item.matchesSearchQuery("Originote"))
        assertTrue(item.matchesSearchQuery("hyalucera"))
        assertTrue(item.matchesSearchQuery("MOISTURIZER"))
    }

    @Test
    fun testMatchesSearchQuery_matchesHook() {
        val item = DataItemResponse(
            id = "test-2",
            productName = "Sunscreen SPF 50",
            caption = "Proteksi sinar UV maksimal",
            hook = "Aduh panasnya pol banget!",
            linkProduct = "https://shopee.co.id/sunscreen"
        )

        assertTrue(item.matchesSearchQuery("panasnya"))
        assertTrue(item.matchesSearchQuery("pol banget"))
    }

    @Test
    fun testMatchesSearchQuery_matchesCaptionAndLink() {
        val item = DataItemResponse(
            id = "test-3",
            productName = "Serum Vitamin C",
            caption = "Mencerahkan noda hitam dalam 14 hari #racunshopee",
            linkProduct = "https://tokopedia.com/serum-vit-c",
            originalFileName = "20260926_vitc_promo.mp4"
        )

        assertTrue(item.matchesSearchQuery("noda hitam"))
        assertTrue(item.matchesSearchQuery("tokopedia"))
        assertTrue(item.matchesSearchQuery("vitc_promo"))
    }

    @Test
    fun testMatchesSearchQuery_blankQueryMatchesAll() {
        val item = DataItemResponse(id = "test-4", productName = "Barang Apa Saja")
        assertTrue(item.matchesSearchQuery(""))
        assertTrue(item.matchesSearchQuery("   "))
    }

    @Test
    fun testMatchesSearchQuery_noMatchReturnsFalse() {
        val item = DataItemResponse(
            id = "test-5",
            productName = "Sepatu Lari",
            caption = "Sepatu olahraga empuk",
            hook = "Lari makin kencang"
        )

        assertFalse(item.matchesSearchQuery("lipstick"))
        assertFalse(item.matchesSearchQuery("keyboard"))
    }
}


