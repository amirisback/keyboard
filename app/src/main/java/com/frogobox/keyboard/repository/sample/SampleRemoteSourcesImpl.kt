package com.frogobox.keyboard.repository.sample

import com.frogobox.keyboard.common.callback.DataResponseCallback
import com.frogobox.keyboard.common.ext.disposedBy
import com.frogobox.keyboard.data.remote.sample.SampleApi
import com.frogobox.keyboard.data.remote.sample.SampleResponse
import com.frogobox.research.repository.DataSourcesImpl
import com.frogobox.research.repository.sample.SampleRemoteSources
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
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


class SampleRemoteSourcesImpl @Inject constructor(
    private val api: SampleApi,
) : DataSourcesImpl(), SampleRemoteSources {

    override fun getSample(callback: DataResponseCallback<List<SampleResponse>>) {
        api.getSample().subscribeOn(Schedulers.io())
            .doOnSubscribe {
                callback.onShowProgress()
            }
            .doAfterTerminate {
                callback.onHideProgress()
                callback.onFinish()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.data?.let { it1 -> callback.onSuccess(it1) }
            }, {
                callback.onFailed(500, it.message.toString())
            }).disposedBy(compositeDisposable)

    }


}