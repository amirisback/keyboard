package com.frogobox.appkeyboard.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import kotlinx.parcelize.Parcelize

/**
 * Room Database entity representing Product Remote items cached locally for offline keyboard use.
 * Complies with Room schema export, Parcelize, and ProGuard obfuscation safety.
 */
@Keep
@Entity(
    tableName = "product_remote",
    indices = [
        Index(value = ["remoteId"])
    ]
)
@Parcelize
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "remoteId")
    var remoteId: String? = null,

    @ColumnInfo(name = "uploadTimestamp")
    var uploadTimestamp: String? = null,

    @ColumnInfo(name = "productName")
    var productName: String = "",

    @ColumnInfo(name = "caption")
    var caption: String = "",

    @ColumnInfo(name = "originalFileName")
    var originalFileName: String? = null,

    @ColumnInfo(name = "fileSize")
    var fileSize: String? = null,

    @ColumnInfo(name = "fileType")
    var fileType: String? = null,

    @ColumnInfo(name = "driveLink")
    var driveLink: String? = null,

    @ColumnInfo(name = "driveFileId")
    var driveFileId: String? = null,

    @ColumnInfo(name = "thumbnailUrl")
    var thumbnailUrl: String? = null,

    @ColumnInfo(name = "previewUrl")
    var previewUrl: String? = null,

    @ColumnInfo(name = "isVideo")
    var isVideo: Boolean = false,

    @ColumnInfo(name = "statusDownload")
    var statusDownload: String = "Belum",

    @ColumnInfo(name = "rowIndex")
    var rowIndex: Int? = null,

    @ColumnInfo(name = "createdAt")
    var createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updatedAt")
    var updatedAt: Long = System.currentTimeMillis()
) : Parcelable {

    val isDownloaded: Boolean
        get() = statusDownload.equals("Sudah", ignoreCase = true)

    fun toDataItemResponse(): DataItemResponse {
        return DataItemResponse(
            id = remoteId ?: id.toString(),
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
    }

    companion object {
        fun fromDataItemResponse(item: DataItemResponse, localId: Int = 0): ProductEntity {
            return ProductEntity(
                id = localId,
                remoteId = item.id,
                uploadTimestamp = item.uploadTimestamp,
                productName = item.productName ?: item.displayTitle,
                caption = item.caption ?: "",
                originalFileName = item.originalFileName,
                fileSize = item.fileSize,
                fileType = item.fileType,
                driveLink = item.driveLink,
                driveFileId = item.driveFileId,
                thumbnailUrl = item.thumbnailUrl,
                previewUrl = item.previewUrl,
                isVideo = item.isVideo ?: false,
                statusDownload = item.statusDownload ?: "Belum",
                rowIndex = item.rowIndex
            )
        }
    }
}

fun DataItemResponse.toProductEntity(localId: Int = 0): ProductEntity =
    ProductEntity.fromDataItemResponse(this, localId)
