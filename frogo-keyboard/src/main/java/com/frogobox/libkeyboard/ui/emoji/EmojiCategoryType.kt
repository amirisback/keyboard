package com.frogobox.libkeyboard.ui.emoji

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */


enum class EmojiCategoryType(val icon: String, val path: String) {
    GENERAL("🙂", "media/_emoji_general.txt"),
    ACTIVITIES("\uD83C\uDF83", "media/emoji_activities.txt"),
    ANIMAL_NATURE("\uD83D\uDC35", "media/emoji_animal_nature.txt"),
    FLAG("\uD83C\uDFC1", "media/emoji_flag.txt"),
    FOOD_DRINK("\uD83C\uDF47", "media/emoji_food_drink.txt"),
    OBJECTS("\uD83D\uDC53", "media/emoji_objects.txt"),
    PEOPLE_BODY("\uD83D\uDC4B", "media/emoji_people_body.txt"),
    SMILEYS_EMOTION("\uD83D\uDE00", "media/emoji_smileys_emotion.txt"),
    SYMBOLS("✅", "media/emoji_symbols.txt"),
    TRAVEL_PLACES("\uD83C\uDF0D", "media/emoji_travel_places.txt")
}