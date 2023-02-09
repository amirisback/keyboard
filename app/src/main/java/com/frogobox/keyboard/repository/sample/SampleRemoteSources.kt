package com.frogobox.research.repository.sample

import com.frogobox.keyboard.common.callback.DataResponseCallback
import com.frogobox.keyboard.data.remote.sample.SampleResponse

/**
 * Created by Faisal Amir on 06/01/23
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */


interface SampleRemoteSources {

    fun getSample(callback: DataResponseCallback<List<SampleResponse>>)

}