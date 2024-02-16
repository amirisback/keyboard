package com.frogobox.appkeyboard.ui.test

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.frogobox.appkeyboard.R
import com.frogobox.appkeyboard.common.base.BaseActivity
import com.frogobox.appkeyboard.databinding.ActivityTestBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestActivity : BaseActivity<ActivityTestBinding>() {

    companion object {
        private val TAG: String = TestActivity::class.java.simpleName
    }

    private val viewModel: TestViewModel by viewModels()

    override fun setupViewBinding(): ActivityTestBinding {
        return ActivityTestBinding.inflate(layoutInflater)
    }

    override fun setupViewModel() {
        super.setupViewModel()
        viewModel.apply {

        }
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
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
            val adapterS = ArrayAdapter(this@TestActivity, R.layout.item_spinner, R.id.tv_text, dummy)
            etTextAuto.setAdapter(adapterS)
        }
    }

}