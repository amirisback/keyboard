package com.frogobox.appkeyboard.model

import androidx.annotation.Keep

@Keep
data class ClipboardItem(
    val id: String,
    val text: String,
    val timestamp: Long,
    val isPinned: Boolean = false
)
