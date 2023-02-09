package com.frogobox.keyboard.common.delegate

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */


class SampleDelegateImpl : SampleDelegate {

    override fun getTagMainDelegate(): String {
        return SampleDelegateImpl::class.java.simpleName
    }

}