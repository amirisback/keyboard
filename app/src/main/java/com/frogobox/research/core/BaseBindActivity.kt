package com.frogobox.research.core

import android.os.Bundle
import android.util.Log
import androidx.viewbinding.ViewBinding

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

abstract class BaseBindActivity<VB : ViewBinding> : BaseActivity() {

    companion object {
        private val TAG: String = BaseBindActivity::class.java.simpleName
    }

    protected val binding: VB by lazy { initBinding() }

    abstract fun initBinding(): VB

    open fun initView() {
        Log.d(TAG, "initView")
    }

    open fun initObserver() {
        Log.d(TAG, "initObserver")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initObserver()
        initView()
    }

}