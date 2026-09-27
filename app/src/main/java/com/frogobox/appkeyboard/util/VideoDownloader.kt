package com.frogobox.appkeyboard.util

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.frogobox.appkeyboard.data.remote.VideoDownloadService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit

/**
 * Modern Android & Retrofit video downloader supporting:
 * - Scoped Storage via MediaStore on Android 10+ (API 29+) without requiring WRITE_EXTERNAL_STORAGE.
 * - Legacy storage fallback with MediaScanner registration.
 * - Retrofit @Streaming byte-by-byte piping to avoid OutOfMemoryError.
 * - Automatic redirect following for Google Drive direct links.
 * - Real-time progress callback (0% - 100%).
 */
object VideoDownloader {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val downloadService: VideoDownloadService by lazy {
        Retrofit.Builder()
            .baseUrl("https://drive.google.com/")
            .client(okHttpClient)
            .build()
            .create(VideoDownloadService::class.java)
    }

    /**
     * Downloads a video from [downloadUrl] and saves it directly to Android public Movies/SellerKeyboard
     * or app media storage, emitting progress through [onProgress].
     *
     * @return Result containing the saved URI/Path as String on success, or exception on failure.
     */
    suspend fun downloadVideo(
        context: Context,
        downloadUrl: String,
        fileName: String,
        onProgress: (Int) -> Unit = {}
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = downloadService.downloadFile(downloadUrl)
            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    IllegalStateException("HTTP ${response.code()}: ${response.message()}")
                )
            }

            val body = response.body()
                ?: return@withContext Result.failure(IllegalStateException("Response body is empty"))

            val totalBytes = body.contentLength()
            val inputStream = body.byteStream()

            val saveResult = saveStreamToStorage(context, inputStream, fileName, totalBytes, onProgress)
            saveResult
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveStreamToStorage(
        context: Context,
        inputStream: InputStream,
        fileName: String,
        totalBytes: Long,
        onProgress: (Int) -> Unit
    ): Result<String> {
        val cleanFileName = DriveUrlHelper.sanitizeFileName(fileName, null)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveToMediaStoreQ(context, inputStream, cleanFileName, totalBytes, onProgress)
        } else {
            saveToLegacyFiles(context, inputStream, cleanFileName, totalBytes, onProgress)
        }
    }

    private fun saveToMediaStoreQ(
        context: Context,
        inputStream: InputStream,
        fileName: String,
        totalBytes: Long,
        onProgress: (Int) -> Unit
    ): Result<String> {
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.RELATIVE_PATH, "${Environment.DIRECTORY_MOVIES}/SellerKeyboard")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val uri: Uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: return saveToLegacyFiles(context, inputStream, fileName, totalBytes, onProgress)

        return try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                copyStreamWithProgress(inputStream, outputStream, totalBytes, onProgress)
            } ?: throw IllegalStateException("Cannot open output stream for MediaStore URI")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }

            Result.success(uri.toString())
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            Result.failure(e)
        }
    }

    private fun saveToLegacyFiles(
        context: Context,
        inputStream: InputStream,
        fileName: String,
        totalBytes: Long,
        onProgress: (Int) -> Unit
    ): Result<String> {
        val targetDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
            "SellerKeyboard"
        ).apply { if (!exists()) mkdirs() }

        val targetFile = File(targetDir, fileName)

        return try {
            FileOutputStream(targetFile).use { outputStream ->
                copyStreamWithProgress(inputStream, outputStream, totalBytes, onProgress)
            }

            MediaScannerConnection.scanFile(
                context,
                arrayOf(targetFile.absolutePath),
                arrayOf("video/mp4"),
                null
            )

            Result.success(targetFile.absolutePath)
        } catch (e: Exception) {
            if (targetFile.exists()) targetFile.delete()
            Result.failure(e)
        }
    }

    private fun copyStreamWithProgress(
        input: InputStream,
        output: OutputStream,
        totalBytes: Long,
        onProgress: (Int) -> Unit
    ) {
        val buffer = ByteArray(8 * 1024)
        var bytesCopied = 0L
        var lastReportedPercent = -1

        input.use { inStream ->
            output.use { outStream ->
                var bytes = inStream.read(buffer)
                while (bytes >= 0) {
                    outStream.write(buffer, 0, bytes)
                    bytesCopied += bytes

                    if (totalBytes > 0) {
                        val percent = ((bytesCopied * 100) / totalBytes).toInt().coerceIn(0, 100)
                        if (percent != lastReportedPercent) {
                            lastReportedPercent = percent
                            onProgress(percent)
                        }
                    } else {
                        onProgress(-1) // indeterminate
                    }
                    bytes = inStream.read(buffer)
                }
                outStream.flush()
            }
        }
        onProgress(100)
    }
}
