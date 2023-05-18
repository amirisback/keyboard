package com.frogobox.appkeyboard.common.ext

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


fun Disposable.disposedBy(compositeDisposable: CompositeDisposable?) {
    compositeDisposable?.add(this)
}