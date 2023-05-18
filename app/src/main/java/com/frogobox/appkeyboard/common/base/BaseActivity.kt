package com.frogobox.appkeyboard.common.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.frogobox.appkeyboard.BuildConfig
import com.frogobox.sdk.view.FrogoBindActivity

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

abstract class BaseActivity<VB: ViewBinding> : FrogoBindActivity<VB>() {

    open fun initView() {}

    override fun setupDebugMode(): Boolean {
        return BuildConfig.DEBUG
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        initView()
    }

}