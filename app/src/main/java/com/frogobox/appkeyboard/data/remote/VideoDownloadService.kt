package com.frogobox.appkeyboard.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * Retrofit service for streaming file downloads (videos, media) without buffering into memory.
 */
interface VideoDownloadService {

    @Streaming
    @GET
    suspend fun downloadFile(@Url fileUrl: String): Response<ResponseBody>

}
