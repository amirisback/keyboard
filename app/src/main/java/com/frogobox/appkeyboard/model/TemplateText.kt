package com.frogobox.appkeyboard.model

import androidx.annotation.Keep

@Keep
data class TemplateText(
    val id: Int,
    val text: String,
    val type: KeyboardFeatureType
)
