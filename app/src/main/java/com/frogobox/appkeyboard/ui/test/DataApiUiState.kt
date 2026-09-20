package com.frogobox.appkeyboard.ui.test

import androidx.annotation.Keep
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse

/**
 * UI State machine representing Remote API fetch lifecycle in Test Playground.
 */
@Keep
sealed interface DataApiUiState {
    data object Idle : DataApiUiState
    data object Loading : DataApiUiState
    data class Success(
        val total: Int,
        val lastUpdatedWib: String,
        val items: List<DataItemResponse>
    ) : DataApiUiState
    data class Error(val message: String) : DataApiUiState
}
