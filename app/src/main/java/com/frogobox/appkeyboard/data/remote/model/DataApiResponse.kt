package com.frogobox.appkeyboard.data.remote.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * Root data model for remote API response from data.json.
 * Strictly adheres to Google Sheets feed schema.
 * Annotated with @Keep and @SerializedName for safe serialization/deserialization.
 */
@Keep
data class DataApiResponse(
    @SerializedName("success")
    val success: Boolean? = null,

    @SerializedName("lastUpdated")
    val lastUpdated: String? = null,

    @SerializedName("lastUpdatedWib")
    val lastUpdatedWib: String? = null,

    @SerializedName("total")
    val total: Int? = null,

    @SerializedName("sheetId")
    val sheetId: String? = null,

    @SerializedName("source")
    val source: String? = null,

    @SerializedName("items")
    val items: List<DataItemResponse>? = null
) {
    val allItems: List<DataItemResponse>
        get() = items ?: emptyList()
}
