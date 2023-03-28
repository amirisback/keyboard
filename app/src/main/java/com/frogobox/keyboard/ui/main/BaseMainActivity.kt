package com.frogobox.keyboard.ui.main

import androidx.viewbinding.ViewBinding
import com.frogobox.keyboard.common.base.BaseActivity
import com.frogobox.sdk.delegate.piracy.FrogoPiracyCallback
import com.frogobox.sdk.delegate.piracy.FrogoPiracyDialogCallback
import com.frogobox.sdk.delegate.piracy.PiracyDelegates
import com.frogobox.sdk.delegate.piracy.PiracyDelegatesImpl
import com.frogobox.sdk.delegate.piracy.util.PiracyMessage
import com.frogobox.sdk.delegate.util.UtilDelegates
import com.frogobox.sdk.delegate.util.UtilDelegatesImpl

abstract class BaseMainActivity<VB : ViewBinding> : BaseActivity<VB>(),
    PiracyDelegates by PiracyDelegatesImpl(),
    UtilDelegates by UtilDelegatesImpl()
{

    override fun setupDelegates() {
        super.setupDelegates()
        setupPiracyDelegate(this, this)
        setupUtilDelegates(this)
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

}