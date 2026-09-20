package com.frogobox.appkeyboard.repository.data

import com.frogobox.appkeyboard.common.core.Resource
import com.frogobox.appkeyboard.data.remote.model.DataApiResponse
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface defining operations for fetching remote API data.
 */
interface DataApiRepository {

    fun fetchDataStream(): Flow<Resource<DataApiResponse>>

    suspend fun fetchData(): Resource<DataApiResponse>

}
