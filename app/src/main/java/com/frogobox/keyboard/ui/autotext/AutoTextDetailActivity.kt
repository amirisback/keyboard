package com.frogobox.keyboard.ui.autotext

import android.os.Bundle
import androidx.activity.viewModels
import com.frogobox.keyboard.common.base.BaseActivity
import com.frogobox.keyboard.data.local.autotext.AutoTextEntity
import com.frogobox.keyboard.databinding.ActivityAutotextDetailBinding
import com.frogobox.sdk.ext.getExtraDataExt
import com.frogobox.sdk.ext.startActivityExt
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */


@AndroidEntryPoint
class AutoTextDetailActivity : BaseActivity<ActivityAutotextDetailBinding>() {

    companion object {
        const val EXTRA_AUTO_TEXT = "extra_auto_text"
    }

    private val viewModel: AutoTextViewModel by viewModels()

    private val autoText: AutoTextEntity by lazy {
        Gson().fromJson(
            intent.extras?.getString(EXTRA_AUTO_TEXT),
            AutoTextEntity::class.java
        )
    }

    override fun setupViewModel() {
        super.setupViewModel()
        viewModel.eventSuccessState.observe(this) {
            setResult(AutoTextEditorActivity.RESULT_CODE_EDIT)
            finish()
        }
    }

    override fun setupViewBinding(): ActivityAutotextDetailBinding {
        return ActivityAutotextDetailBinding.inflate(layoutInflater)
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        setupDetailActivity("Detail Auto Text")
        setupUI()
    }

    private fun setupUI() {
        binding.apply {

            tvTitle.text = autoText.title
            tvContent.text = autoText.body

            btnDelete.setOnClickListener {
                viewModel.deleteAutoText(autoText)
            }

            btnEdit.setOnClickListener {
                startActivityExt<AutoTextEditorActivity, AutoTextEntity>(
                    AutoTextEditorActivity.EXTRA_AUTO_TEXT_EDIT,
                    autoText
                )
            }
        }
    }

}