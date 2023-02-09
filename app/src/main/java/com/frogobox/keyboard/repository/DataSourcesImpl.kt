package com.frogobox.research.repository

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * Created by Faisal Amir on 06/01/23
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */


abstract class DataSourcesImpl : DataSources {

    protected val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    override fun onClearDisposables() {
        compositeDisposable.clear()
    }

    override fun addSubscribe(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

}