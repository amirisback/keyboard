package com.frogobox.appkeyboard.util

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat

/**
 * Handles rich media (GIF, Sticker, Animated WebP) commit through Android InputConnectionCompat.
 * Ensures graceful fallback to clipboard copy if the target app does not accept rich content.
 */
object KeyboardRichMediaHelper {

    /**
     * Checks if the active target [EditorInfo] supports the given MIME type.
     */
    fun isMimeTypeSupported(editorInfo: EditorInfo?, mimeType: String): Boolean {
        if (editorInfo == null) return false
        val supportedMimes = try {
            editorInfo.contentMimeTypes ?: EditorInfoCompat.getContentMimeTypes(editorInfo)
        } catch (_: Exception) {
            editorInfo.contentMimeTypes ?: emptyArray()
        }

        return supportedMimes.any { supported ->
            try {
                ClipDescription.compareMimeTypes(supported, mimeType)
            } catch (_: Exception) {
                // Resilient fallback for JVM unit test environments where ClipDescription is unmocked
                if (supported == "*/*") true
                else if (supported.endsWith("/*")) {
                    val prefix = supported.substringBefore("/")
                    mimeType.startsWith("$prefix/")
                } else {
                    supported.equals(mimeType, ignoreCase = true)
                }
            }
        }
    }

    /**
     * Commits rich media content (e.g. GIF, WebP, PNG) into the target input connection.
     * Falls back to copying URI/content to clipboard if target app does not accept rich content.
     */
    fun commitRichContent(
        context: Context,
        inputConnection: InputConnection?,
        editorInfo: EditorInfo?,
        contentUri: Uri,
        mimeType: String,
        label: String = "Rich Content",
        linkUri: Uri? = null
    ): Boolean {
        if (inputConnection == null || editorInfo == null) {
            fallbackCopyToClipboard(context, contentUri, label)
            return false
        }

        if (!isMimeTypeSupported(editorInfo, mimeType)) {
            fallbackCopyToClipboard(context, contentUri, label)
            return false
        }

        val description = ClipDescription(label, arrayOf(mimeType))
        val inputContentInfo = InputContentInfoCompat(contentUri, description, linkUri)

        var flags = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            flags = flags or InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION
        }

        val opts = Bundle()
        val committed = InputConnectionCompat.commitContent(
            inputConnection,
            editorInfo,
            inputContentInfo,
            flags,
            opts
        )

        if (!committed) {
            fallbackCopyToClipboard(context, contentUri, label)
        }
        return committed
    }

    private fun fallbackCopyToClipboard(context: Context, contentUri: Uri, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newUri(context.contentResolver, label, contentUri)
        clipboard?.setPrimaryClip(clip)
    }
}
