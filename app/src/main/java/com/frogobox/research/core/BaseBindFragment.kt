package com.frogobox.research.core

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

abstract class BaseBindFragment<VB : ViewBinding> : BaseFragment() {

    companion object {
        private val TAG: String = BaseBindFragment::class.java.simpleName
    }

    private var _binding: VB? = null

    protected val binding: VB get() = _binding!!

    abstract fun setupViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    open fun initView() {}

    open fun initObserver() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = setupViewBinding(inflater, container)
        if (savedInstanceState == null) {
            Log.d(TAG,"View Binding : ${binding::class.java.simpleName}")
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        Log.d(TAG,"Destroying View Binding")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        initView()
    }

}