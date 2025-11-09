package com.frogobox.appkeyboard.ui.main

import androidx.viewbinding.ViewBinding
import com.frogobox.appkeyboard.common.base.BaseActivity
import com.frogobox.sdk.delegate.piracy.PiracyCallback
import com.frogobox.sdk.delegate.piracy.PiracyDelegates
import com.frogobox.sdk.delegate.piracy.PiracyDelegatesImpl
import com.frogobox.sdk.delegate.piracy.PiracyMessage
import com.frogobox.sdk.ext.openPlayStore

abstract class BaseMainActivity<VB : ViewBinding> : BaseActivity<VB>(),
    PiracyDelegates by PiracyDelegatesImpl()
{

    override fun setupDelegates() {
        super.setupDelegates()
        setupPiracyDelegate(this, this)
    }

    override fun setupPiracyMode() {
        connectPiracyChecker(object : PiracyCallback {
            override fun doOnPirated(message: PiracyMessage) {

                showPiracedDialog(message, object : PiracyCallback {
                    override fun doOnPirated(message: PiracyMessage) {
                        openPlayStore(packageName)
                    }

                })
            }
        })
    }

}