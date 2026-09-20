package com.frogobox.appkeyboard.data.remote.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * Data model for individual items returned from remote API matching data.json.
 * Strictly adheres to Google Sheets feed schema.
 * Annotated with @Keep and @SerializedName to ensure ProGuard obfuscation safety.
 */
@Keep
data class DataItemResponse(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("uploadTimestamp")
    val uploadTimestamp: String? = null,

    @SerializedName("productName")
    val productName: String? = null,

    @SerializedName("caption")
    val caption: String? = null,

    @SerializedName("originalFileName")
    val originalFileName: String? = null,

    @SerializedName("fileSize")
    val fileSize: String? = null,

    @SerializedName("fileType")
    val fileType: String? = null,

    @SerializedName("driveLink")
    val driveLink: String? = null,

    @SerializedName("driveFileId")
    val driveFileId: String? = null,

    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String? = null,

    @SerializedName("previewUrl")
    val previewUrl: String? = null,

    @SerializedName("isVideo")
    val isVideo: Boolean? = null,

    @SerializedName("statusDownload")
    val statusDownload: String? = null,

    @SerializedName("rowIndex")
    val rowIndex: Int? = null
) {
    constructor(
        id: Int,
        uploadTimestamp: String? = null,
        productName: String? = null,
        caption: String? = null,
        originalFileName: String? = null,
        fileSize: String? = null,
        fileType: String? = null,
        driveLink: String? = null,
        driveFileId: String? = null,
        thumbnailUrl: String? = null,
        previewUrl: String? = null,
        isVideo: Boolean? = null,
        statusDownload: String? = null,
        rowIndex: Int? = null
    ) : this(
        id = id.toString(),
        uploadTimestamp = uploadTimestamp,
        productName = productName,
        caption = caption,
        originalFileName = originalFileName,
        fileSize = fileSize,
        fileType = fileType,
        driveLink = driveLink,
        driveFileId = driveFileId,
        thumbnailUrl = thumbnailUrl,
        previewUrl = previewUrl,
        isVideo = isVideo,
        statusDownload = statusDownload,
        rowIndex = rowIndex ?: id
    )

    val displayIndex: Int
        get() = rowIndex ?: id?.filter { it.isDigit() }?.toIntOrNull() ?: (id?.hashCode() ?: 0).let { if (it < 0) -it else it }

    val displayTitle: String
        get() = productName?.takeIf { it.isNotBlank() }
            ?: caption?.takeIf { it.isNotBlank() }
            ?: "Item #$displayIndex"

    val displayBody: String?
        get() = caption?.takeIf { it.isNotBlank() && it != displayTitle }

    val displayTimestamp: String?
        get() = uploadTimestamp?.takeIf { it.isNotBlank() }

    val isDownloaded: Boolean
        get() = statusDownload.equals("Sudah", ignoreCase = true)
}
