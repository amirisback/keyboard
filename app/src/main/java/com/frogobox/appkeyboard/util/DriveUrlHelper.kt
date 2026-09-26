package com.frogobox.appkeyboard.util

/**
 * Utility for parsing and resolving Google Drive URLs and direct video download links.
 */
object DriveUrlHelper {

    private val DRIVE_FILE_ID_REGEX = Regex("""(?:/file/d/|/d/|[?&]id=)([a-zA-Z0-9_-]+)""")

    /**
     * Extracts the Google Drive file ID from a URL or raw ID string.
     */
    fun extractDriveFileId(input: String?): String? {
        if (input.isNullOrBlank()) return null
        val trimmed = input.trim()

        // Check if trimmed input is already an ID (alphanumeric, underscores, hyphens, typically >= 20 chars)
        if (trimmed.matches(Regex("""^[a-zA-Z0-9_-]{20,}$""")) && !trimmed.startsWith("http", ignoreCase = true)) {
            return trimmed
        }

        val match = DRIVE_FILE_ID_REGEX.find(trimmed)
        return match?.groupValues?.getOrNull(1)
    }

    /**
     * Constructs a direct download URL from either a Google Drive file ID or a drive link.
     * Falls back to the original link if it's already a direct HTTP/HTTPS URL.
     */
    fun getDirectDownloadUrl(driveFileId: String?, driveLink: String?): String? {
        val fileId = driveFileId?.takeIf { it.isNotBlank() }
            ?: extractDriveFileId(driveLink)

        return if (!fileId.isNullOrBlank()) {
            "https://drive.usercontent.google.com/download?id=$fileId&export=download"
        } else if (!driveLink.isNullOrBlank() && (driveLink.startsWith("http://") || driveLink.startsWith("https://"))) {
            driveLink
        } else {
            null
        }
    }

    /**
     * Secondary fallback download URL using standard Google Drive uc endpoint.
     */
    fun getFallbackDownloadUrl(driveFileId: String?, driveLink: String?): String? {
        val fileId = driveFileId?.takeIf { it.isNotBlank() }
            ?: extractDriveFileId(driveLink)

        return if (!fileId.isNullOrBlank()) {
            "https://drive.google.com/uc?export=download&id=$fileId"
        } else {
            getDirectDownloadUrl(driveFileId, driveLink)
        }
    }

    /**
     * Sanitizes and produces a valid video file name.
     */
    fun sanitizeFileName(originalFileName: String?, productName: String?, fileId: String? = null): String {
        val candidate = when {
            !originalFileName.isNullOrBlank() -> originalFileName.trim()
            !productName.isNullOrBlank() -> "${productName.trim()}.mp4"
            !fileId.isNullOrBlank() -> "video_$fileId.mp4"
            else -> "video_${System.currentTimeMillis()}.mp4"
        }

        // Replace illegal filesystem characters
        val cleanName = candidate.replace(Regex("""[\\/:*?"<>|]"""), "_")

        return if (cleanName.endsWith(".mp4", ignoreCase = true) ||
            cleanName.endsWith(".mov", ignoreCase = true) ||
            cleanName.endsWith(".mkv", ignoreCase = true) ||
            cleanName.endsWith(".webm", ignoreCase = true)
        ) {
            cleanName
        } else {
            "$cleanName.mp4"
        }
    }
}
