package com.frogobox.appkeyboard.model

import androidx.annotation.Keep

/**
 * Created by Faisal Amir on 09/03/23
 * https://github.com/amirisback
 */

@Keep
data class KeyboardFeatureModel(
    var id: String,
    var text: String,
    var icon: Int,
)