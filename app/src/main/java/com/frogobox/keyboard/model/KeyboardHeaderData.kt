package com.frogobox.keyboard.model

import androidx.annotation.Keep

/**
 * Created by Faisal Amir on 09/03/23
 * https://github.com/amirisback
 */

@Keep
data class KeyboardHeaderData(
    val type: KeyboardHeaderType,
    val icon: Int
)