package com.frogobox.appkeyboard.model

import androidx.annotation.Keep

/**
 * Created by Faisal Amir on 09/03/23
 * https://github.com/amirisback
 */

@Keep
data class KeyboardFeature(
    var id: String,
    var type: KeyboardFeatureType,
    var icon: Int,
    var state: Boolean = false
)