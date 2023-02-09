package com.frogobox.research.repository.sample

import com.frogobox.keyboard.repository.sample.SampleRepository
import com.frogobox.keyboard.common.callback.DataResponseCallback
import com.frogobox.keyboard.data.remote.sample.SampleResponse
import com.frogobox.keyboard.repository.sample.SampleRemoteSourcesImpl
import javax.inject.Inject

/**
 * Created by Faisal Amir on 06/01/23
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */


class SampleRepositoryImpl @Inject constructor(
    private val remoteDataSources: SampleRemoteSourcesImpl,
    private val localDataSources: SampleLocalSourcesImpl
) : SampleRepository {

    override fun getSampleDataFromServer(callback: DataResponseCallback<List<SampleResponse>>) {
        remoteDataSources.getSample(callback)
    }

}