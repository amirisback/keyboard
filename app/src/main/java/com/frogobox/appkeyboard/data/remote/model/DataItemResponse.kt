package com.frogobox.appkeyboard.data.remote.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * Data model for individual items returned from remote API.
 * Annotated with @Keep and @SerializedName to ensure ProGuard obfuscation safety.
 */
@Keep
data class DataItemResponse(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("body")
    val body: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("isActive")
    val isActive: Boolean? = null,

    @SerializedName("rowIndex")
    val rowIndex: Int? = null,

    @SerializedName("isVideo")
    val isVideo: Boolean? = null,

    @SerializedName("fileSize")
    val fileSize: String? = null,

    @SerializedName("caption")
    val caption: String? = null,

    @SerializedName("uploaderName")
    val uploaderName: String? = null,

    @SerializedName("uploadTimestamp")
    val uploadTimestamp: String? = null,

    @SerializedName("driveLink")
    val driveLink: String? = null,

    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String? = null
) {
    val displayIndex: Int
        get() = rowIndex ?: id ?: 0

    val displayTitle: String
        get() = caption?.takeIf { it.isNotBlank() }
            ?: title?.takeIf { it.isNotBlank() }
            ?: uploaderName?.takeIf { it.isNotBlank() }
            ?: "Item #$displayIndex"

    val displaySubtitle: String?
        get() = uploaderName?.takeIf { it.isNotBlank() }
            ?: category?.takeIf { it.isNotBlank() }

    val displayBody: String?
        get() = body?.takeIf { it.isNotBlank() }
            ?: description?.takeIf { it.isNotBlank() }
            ?: caption?.takeIf { it.isNotBlank() }

    val displayTimestamp: String?
        get() = uploadTimestamp?.takeIf { it.isNotBlank() }
            ?: createdAt?.takeIf { it.isNotBlank() }
}
