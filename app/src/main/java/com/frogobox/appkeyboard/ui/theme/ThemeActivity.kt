package com.frogobox.appkeyboard.ui.theme

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.frogobox.appkeyboard.common.base.BaseActivity
import com.frogobox.appkeyboard.databinding.ActivityThemeBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by faisalamircs on 16/02/2024
 * -----------------------------------------
 * Name     : Muhammad Faisal Amir
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 */


@AndroidEntryPoint
class ThemeActivity : BaseActivity<ActivityThemeBinding>() {

    companion object {
        private val TAG: String = ThemeActivity::class.java.simpleName
    }

    private val viewModel: ThemeViewModel by viewModels()

    override fun setupViewBinding(): ActivityThemeBinding {
        return ActivityThemeBinding.inflate(layoutInflater)
    }

    override fun setupViewModel() {
        super.setupViewModel()
        viewModel.apply {

        }
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        setupDetailActivity("Setup Theme")
        if (savedInstanceState == null) {
            // Call View Model Here
            Log.d(TAG, "View Model : ${viewModel::class.java.simpleName}")
        }
        // TODO : Add your code here

    }

    override fun initView() {
        super.initView()
        binding.apply {

        }
    }

}