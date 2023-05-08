package com.frogobox.keyboard.ui.language

import android.os.Bundle
import androidx.activity.viewModels
import com.frogobox.keyboard.common.base.BaseActivity
import com.frogobox.keyboard.databinding.ActivityKeyboardLanguageBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

@AndroidEntryPoint
class KeyboardLanguageActivity : BaseActivity<ActivityKeyboardLanguageBinding>() {

    private val viewModel: KeyboardLanguageViewModel by viewModels()

    override fun setupViewBinding(): ActivityKeyboardLanguageBinding {
        return ActivityKeyboardLanguageBinding.inflate(layoutInflater)
    }

    override fun setupViewModel() {
        super.setupViewModel()
        viewModel.apply {

        }
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.apply {

        }
    }

}