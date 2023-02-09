package com.frogobox.keyboard.common.base

/**
 * Created by Faisal Amir on 06/01/23
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */


interface BaseResponseCallback {
    fun onFailed(statusCode: Int, errorMessage: String = "")
    fun onFinish()
    fun onHideProgress()
    fun onShowProgress()
}