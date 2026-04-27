package com.frogobox.libkeyboard.ui.emoji

import android.content.Context
import android.util.Log

private const val TAG = "EmojiHelper"

/**
 * Reads the emoji list at the given [path] and returns a parsed [MutableList]. If the
 * given file path does not exist, an empty [MutableList] is returned.
 *
 * @param context The initiating view's context.
 * @param path The path to the asset file.
 */
fun parseRawEmojiSpecsFile(context: Context, path: String): MutableList<String> {

    val emojis = mutableListOf<String>()
    var emojiEditorList: MutableList<String>? = null

    fun commitEmojiEditorList() {
        emojiEditorList?.let {
            // add only the base emoji for now, ignore the variations
            if (it.isNotEmpty()) {
                emojis.add(it.first())
            }
        }
        emojiEditorList = null
    }

    try {
        context.assets.open(path).bufferedReader().useLines { lines ->
            for (line in lines) {
                if (line.startsWith("#")) {
                    // Comment line
                } else if (line.startsWith("[")) {
                    commitEmojiEditorList()
                } else if (line.trim().isEmpty()) {
                    // Empty line
                    continue
                } else {
                    if (!line.startsWith("\t")) {
                        commitEmojiEditorList()
                    }

                    // Assume it is a data line
                    val data = line.split(";")
                    if (data.size == 3) {
                        val emoji = data[0].trim()
                        emojiEditorList?.add(emoji) ?: run {
                            emojiEditorList = mutableListOf(emoji)
                        }
                    }
                }
            }
            commitEmojiEditorList()
        }
    } catch (e: Exception) {
        Log.e(TAG, "Failed to parse emoji file: $path", e)
    }

    return emojis
}

/**
 * Returns all emoji categories auto-generated from [EmojiCategoryType] enum entries.
 */
fun getEmojiCategory(): List<EmojiCategory> {
    return EmojiCategoryType.entries.map { type ->
        EmojiCategory(name = type.name, icon = type.icon, path = type.path)
    }
}