package com.frogobox.research.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.frogobox.research.core.BaseBindActivity
import com.frogobox.research.databinding.ActivityDetailBinding

class DetailActivity : BaseBindActivity<ActivityDetailBinding>() {

    companion object {
        private val TAG: String = DetailActivity::class.java.simpleName
    }

    private val viewModel: DetailViewModel by viewModels()

    override fun initBinding(): ActivityDetailBinding {
        return ActivityDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun initObserver() {
        super.initObserver()
        viewModel.apply {

        }
    }

}