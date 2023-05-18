package com.frogobox.appkeyboard.common.base

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * Created by Faisal Amir on 06/02/23
 * https://github.com/amirisback
 */

@Keep
data class BaseResponseModel<T>(

    @SerializedName("code")
    var code: Int? = 0,

    @SerializedName("message")
    var message: String? = "",

    @SerializedName("data")
    var data: T? = null,

)