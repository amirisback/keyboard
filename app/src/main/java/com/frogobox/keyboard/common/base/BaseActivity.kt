package com.frogobox.keyboard.common.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.frogobox.keyboard.BuildConfig
import com.frogobox.sdk.delegate.piracy.FrogoPiracyCallback
import com.frogobox.sdk.delegate.piracy.FrogoPiracyDialogCallback
import com.frogobox.sdk.delegate.piracy.util.PiracyMessage
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

    override fun setupPiracyMode() {
        connectPiracyChecker(object : FrogoPiracyCallback {
            override fun doOnPirated(message: PiracyMessage) {

                showPiracedDialog(message, object : FrogoPiracyDialogCallback {
                    override fun doOnPirated(message: PiracyMessage) {
                        openPlaystore(packageName)
                    }

                })
            }
        })
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        initView()
    }

}