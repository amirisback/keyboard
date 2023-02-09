package com.frogobox.keyboard.data.remote.sample

import android.content.Context
import com.frogobox.keyboard.common.base.BaseResponseModel
import com.frogobox.keyboard.data.remote.ApiService
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

interface SampleApi {

    // TODO : Add your code here

    @GET("api/v1/sample/data.json")
    fun getSample() : Observable<BaseResponseModel<List<SampleResponse>>>

    class Creator {
        fun newInstance(context: Context, baseUrl: String): SampleApi {
            return ApiService.create(context, baseUrl)
        }
    }

}