package com.frogobox.appkeyboard.repository.data

import com.frogobox.appkeyboard.common.core.Resource
import com.frogobox.appkeyboard.data.remote.DataApiService
import com.frogobox.appkeyboard.data.remote.model.DataApiResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of DataApiRepository executing asynchronous network calls on Dispatchers.IO.
 */
@Singleton
class DataApiRepositoryImpl(
    private val apiService: DataApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DataApiRepository {

    @Inject
    constructor(apiService: DataApiService) : this(apiService, Dispatchers.IO)

    override fun fetchDataStream(): Flow<Resource<DataApiResponse>> = flow {
        emit(Resource.Loading)
        val result = executeFetch()
        emit(result)
    }.catch { e ->
        emit(Resource.Error(message = formatErrorMessage(e), cause = e))
    }.flowOn(ioDispatcher)

    override suspend fun fetchData(): Resource<DataApiResponse> = withContext(ioDispatcher) {
        executeFetch()
    }

    private suspend fun executeFetch(): Resource<DataApiResponse> {
        return try {
            val response = apiService.getData()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.Error(message = "Empty response body from server", code = response.code())
                }
            } else {
                val errorBody = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                val errorMsg = errorBody ?: response.message().takeIf { it.isNotBlank() } ?: "HTTP error"
                Resource.Error(message = "HTTP ${response.code()}: $errorMsg", code = response.code())
            }
        } catch (e: Exception) {
            Resource.Error(message = formatErrorMessage(e), cause = e)
        }
    }

    private fun formatErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is ConnectException -> "Connection refused. Please ensure the local server is running at http://192.168.100.6:3000."
            is SocketTimeoutException -> "Connection timed out while communicating with the server."
            is UnknownHostException -> "Unable to resolve host 192.168.100.6. Check your Wi-Fi network."
            is IOException -> throwable.localizedMessage ?: "Network I/O error occurred."
            else -> throwable.localizedMessage ?: "Unexpected error occurred."
        }
    }
}
