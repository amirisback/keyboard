package com.frogobox.appkeyboard.common.core

import androidx.annotation.Keep

/**
 * Standard sealed Resource wrapper for asynchronous operations and network requests.
 */
@Keep
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val cause: Throwable? = null, val code: Int? = null) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}
