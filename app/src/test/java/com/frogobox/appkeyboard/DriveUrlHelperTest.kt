package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.util.DriveUrlHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DriveUrlHelperTest {

    @Test
    fun testExtractDriveFileId_fromViewUrl() {
        val url = "https://drive.google.com/file/d/1N3pdG1aOZf2_ENVzFDA68sOlyhI3CKGg/view?usp=drivesdk"
        val id = DriveUrlHelper.extractDriveFileId(url)
        assertEquals("1N3pdG1aOZf2_ENVzFDA68sOlyhI3CKGg", id)
    }

    @Test
    fun testExtractDriveFileId_fromOpenIdUrl() {
        val url = "https://drive.google.com/open?id=1BrY05mLRCR92g5vo2TCb6MOB69_L8fmF"
        val id = DriveUrlHelper.extractDriveFileId(url)
        assertEquals("1BrY05mLRCR92g5vo2TCb6MOB69_L8fmF", id)
    }

    @Test
    fun testExtractDriveFileId_fromRawId() {
        val rawId = "1N3pdG1aOZf2_ENVzFDA68sOlyhI3CKGg"
        val id = DriveUrlHelper.extractDriveFileId(rawId)
        assertEquals("1N3pdG1aOZf2_ENVzFDA68sOlyhI3CKGg", id)
    }

    @Test
    fun testExtractDriveFileId_emptyOrInvalid() {
        assertNull(DriveUrlHelper.extractDriveFileId(null))
        assertNull(DriveUrlHelper.extractDriveFileId(""))
        assertNull(DriveUrlHelper.extractDriveFileId("   "))
    }

    @Test
    fun testGetDirectDownloadUrl_withFileId() {
        val fileId = "1N3pdG1aOZf2_ENVzFDA68sOlyhI3CKGg"
        val downloadUrl = DriveUrlHelper.getDirectDownloadUrl(fileId, null)
        assertEquals(
            "https://drive.usercontent.google.com/download?id=1N3pdG1aOZf2_ENVzFDA68sOlyhI3CKGg&export=download",
            downloadUrl
        )
    }

    @Test
    fun testGetDirectDownloadUrl_fromDriveLinkFallback() {
        val driveLink = "https://drive.google.com/file/d/67890/view"
        val downloadUrl = DriveUrlHelper.getDirectDownloadUrl(null, driveLink)
        assertEquals(
            "https://drive.usercontent.google.com/download?id=67890&export=download",
            downloadUrl
        )
    }

    @Test
    fun testGetDirectDownloadUrl_directHttpUrl() {
        val directUrl = "https://example.com/videos/product123.mp4"
        val downloadUrl = DriveUrlHelper.getDirectDownloadUrl(null, directUrl)
        assertEquals(directUrl, downloadUrl)
    }

    @Test
    fun testSanitizeFileName() {
        assertEquals(
            "test_video.mp4",
            DriveUrlHelper.sanitizeFileName("test_video.mp4", "Product Name")
        )
        assertEquals(
            "Speaker Bluetooth.mp4",
            DriveUrlHelper.sanitizeFileName(null, "Speaker Bluetooth")
        )
        assertEquals(
            "video_12345.mp4",
            DriveUrlHelper.sanitizeFileName(null, null, "12345")
        )
    }
}
