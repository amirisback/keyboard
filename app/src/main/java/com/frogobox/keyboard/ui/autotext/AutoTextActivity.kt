package com.frogobox.keyboard.ui.autotext

import android.os.Bundle
import androidx.activity.viewModels
import com.frogobox.keyboard.common.base.BaseActivity
import com.frogobox.keyboard.databinding.ActivityAutotextBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */


@AndroidEntryPoint
class AutoTextActivity : BaseActivity<ActivityAutotextBinding>() {

    private val viewModel: AutoTextViewModel by viewModels()

    override fun setupViewBinding(): ActivityAutotextBinding {
        return ActivityAutotextBinding.inflate(layoutInflater)
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        setupDetailActivity("Auto Text")
    }

}