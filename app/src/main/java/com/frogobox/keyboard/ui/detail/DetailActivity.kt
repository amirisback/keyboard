package com.frogobox.keyboard.ui.detail

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.frogobox.keyboard.R
import com.frogobox.keyboard.core.BaseBindActivity
import com.frogobox.keyboard.databinding.ActivityDetailBinding

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
        setupDetailActivity("Test Area")
        if (savedInstanceState == null) {
            // Call View Model Here
            Log.d(TAG, "View Model : ${viewModel::class.java.simpleName}")
        }
        // TODO : Add your code here

    }

    override fun initView() {
        super.initView()
        binding.apply {
            val dummy = listOf("Kuningan",
                "Menteng",
                "Menten213g",
                "Men123teng",
                "Mw23423",
                "Me123nteng",
                "Mente234234234ng",
                "Pegangsaan"
            )
            val adapterS = ArrayAdapter(this@DetailActivity, R.layout.item_spinner, R.id.tv_text, dummy)
            etTextAuto.setAdapter(adapterS)
        }
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.apply {

        }
    }

}