package com.frogobox.appkeyboard.util

import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.ClipboardItem
import org.json.JSONArray
import org.json.JSONObject

/**
 * Backup and restore helper for user AutoText, pinned clipboard items, and keyboard settings.
 * Ensures zero data corruption and strictly sanitizes exported data (excludes unpinned or sensitive clipboard items).
 */
object KeyboardBackupRestoreHelper {

    const val CURRENT_BACKUP_VERSION = 1

    data class AutoTextBackupItem(
        val title: String,
        val text: String,
        val category: String = "Umum"
    )

    data class KeyboardBackupData(
        val version: Int = CURRENT_BACKUP_VERSION,
        val exportedAt: Long = System.currentTimeMillis(),
        val autoTexts: List<AutoTextBackupItem>,
        val pinnedClips: List<String>,
        val settings: Map<String, String> = emptyMap()
    )

    /**
     * Serializes user data into sanitized JSON.
     * Only exports pinned, non-sensitive clipboard items.
     */
    fun exportToJson(
        autoTexts: List<AutoTextEntity>,
        clipboardItems: List<ClipboardItem>,
        settings: Map<String, String> = emptyMap()
    ): String {
        val root = JSONObject()
        root.put("version", CURRENT_BACKUP_VERSION)
        root.put("exportedAt", System.currentTimeMillis())

        // 1. AutoText items
        val autoTextArray = JSONArray()
        for (item in autoTexts) {
            val obj = JSONObject()
            obj.put("title", item.title)
            obj.put("text", item.body)
            obj.put("category", "Custom")
            autoTextArray.put(obj)
        }
        root.put("autoTexts", autoTextArray)

        // 2. Pinned Clipboard items (sanitize sensitive data)
        val clipsArray = JSONArray()
        val safeClips = clipboardItems.filter { it.isPinned && !ClipboardSecurityGuard.isSensitiveContent(it.text) }
        for (clip in safeClips) {
            clipsArray.put(clip.text)
        }
        root.put("pinnedClips", clipsArray)

        // 3. Settings key-values
        val settingsObj = JSONObject()
        for ((key, value) in settings) {
            settingsObj.put(key, value)
        }
        root.put("settings", settingsObj)

        return root.toString(2)
    }

    /**
     * Parses and validates incoming JSON string into typed [KeyboardBackupData].
     */
    fun importFromJson(jsonString: String): Result<KeyboardBackupData> {
        return runCatching {
            val root = JSONObject(jsonString)
            val version = root.optInt("version", CURRENT_BACKUP_VERSION)
            val exportedAt = root.optLong("exportedAt", System.currentTimeMillis())

            val autoTextList = mutableListOf<AutoTextBackupItem>()
            val autoTextArray = root.optJSONArray("autoTexts")
            if (autoTextArray != null) {
                for (i in 0 until autoTextArray.length()) {
                    val obj = autoTextArray.getJSONObject(i)
                    val title = obj.optString("title", "")
                    val text = obj.optString("text", "")
                    val category = obj.optString("category", "Custom")
                    if (title.isNotBlank() && text.isNotBlank()) {
                        autoTextList.add(AutoTextBackupItem(title, text, category))
                    }
                }
            }

            val pinnedClips = mutableListOf<String>()
            val clipsArray = root.optJSONArray("pinnedClips")
            if (clipsArray != null) {
                for (i in 0 until clipsArray.length()) {
                    val clip = clipsArray.getString(i)
                    if (clip.isNotBlank()) {
                        pinnedClips.add(clip)
                    }
                }
            }

            val settingsMap = mutableMapOf<String, String>()
            val settingsObj = root.optJSONObject("settings")
            if (settingsObj != null) {
                val keys = settingsObj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    settingsMap[key] = settingsObj.optString(key, "")
                }
            }

            KeyboardBackupData(
                version = version,
                exportedAt = exportedAt,
                autoTexts = autoTextList,
                pinnedClips = pinnedClips,
                settings = settingsMap
            )
        }
    }
}
