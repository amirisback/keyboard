package com.frogobox.appkeyboard.common.base

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.frogobox.sdk.view.FrogoBindFragment

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

abstract class BaseFragment<VB: ViewBinding> : FrogoBindFragment<VB>() {

    open fun initView() {}

    override fun onViewCreatedExt(view: View, savedInstanceState: Bundle?) {
        super.onViewCreatedExt(view, savedInstanceState)
        initView()
    }

}