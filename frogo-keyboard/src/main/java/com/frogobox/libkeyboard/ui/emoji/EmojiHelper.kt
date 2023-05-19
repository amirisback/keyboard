package com.frogobox.libkeyboard.ui.emoji

import android.content.Context

/**
 * Reads the emoji list at the given [path] and returns an parsed [MutableList]. If the
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
            emojis.add(it.first())
        }
        emojiEditorList = null
    }

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
                    if (emojiEditorList != null) {
                        emojiEditorList!!.add(emoji)
                    } else {
                        emojiEditorList = mutableListOf(emoji)
                    }
                }
            }
        }
        commitEmojiEditorList()
    }
    
    return emojis
}

fun getEmojiCategory() : List<EmojiCategory>  {
    return listOf(
        EmojiCategory(EmojiCategoryType.GENERAL.name, EmojiCategoryType.GENERAL.icon, EmojiCategoryType.GENERAL.path),
        EmojiCategory(EmojiCategoryType.SMILEYS_EMOTION.name, EmojiCategoryType.SMILEYS_EMOTION.icon, EmojiCategoryType.SMILEYS_EMOTION.path),
        EmojiCategory(EmojiCategoryType.PEOPLE_BODY.name, EmojiCategoryType.PEOPLE_BODY.icon, EmojiCategoryType.PEOPLE_BODY.path),
        EmojiCategory(EmojiCategoryType.ACTIVITIES.name, EmojiCategoryType.ACTIVITIES.icon, EmojiCategoryType.ACTIVITIES.path),
        EmojiCategory(EmojiCategoryType.ANIMAL_NATURE.name, EmojiCategoryType.ANIMAL_NATURE.icon, EmojiCategoryType.ANIMAL_NATURE.path),
        EmojiCategory(EmojiCategoryType.FOOD_DRINK.name, EmojiCategoryType.FOOD_DRINK.icon, EmojiCategoryType.FOOD_DRINK.path),
        EmojiCategory(EmojiCategoryType.FLAG.name, EmojiCategoryType.FLAG.icon, EmojiCategoryType.FLAG.path),
        EmojiCategory(EmojiCategoryType.OBJECTS.name, EmojiCategoryType.OBJECTS.icon, EmojiCategoryType.OBJECTS.path),
        EmojiCategory(EmojiCategoryType.TRAVEL_PLACES.name, EmojiCategoryType.TRAVEL_PLACES.icon, EmojiCategoryType.TRAVEL_PLACES.path),
        EmojiCategory(EmojiCategoryType.SYMBOLS.name, EmojiCategoryType.SYMBOLS.icon, EmojiCategoryType.SYMBOLS.path)
    )
}