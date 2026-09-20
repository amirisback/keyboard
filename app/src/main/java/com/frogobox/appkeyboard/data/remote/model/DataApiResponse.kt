package com.frogobox.appkeyboard.data.remote.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * Root data model for remote API response from /api/data.json.
 * Annotated with @Keep and @SerializedName for safe serialization/deserialization.
 */
@Keep
data class DataApiResponse(
    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("total")
    val total: Int? = null,

    @SerializedName("lastUpdated")
    val lastUpdated: String? = null,

    @SerializedName("data")
    val data: List<DataItemResponse>? = null
)
