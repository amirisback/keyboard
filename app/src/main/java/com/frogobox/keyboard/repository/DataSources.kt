package com.frogobox.research.repository

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


interface DataSources {

    fun onClearDisposables()

    fun addSubscribe(disposable: Disposable)

}