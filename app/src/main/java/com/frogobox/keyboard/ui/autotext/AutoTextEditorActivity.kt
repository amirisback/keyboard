package com.frogobox.keyboard.ui.autotext

import android.os.Bundle
import androidx.activity.viewModels
import com.frogobox.keyboard.common.base.BaseActivity
import com.frogobox.keyboard.databinding.ActivityAutotextEditorBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */


@AndroidEntryPoint
class AutoTextEditorActivity : BaseActivity<ActivityAutotextEditorBinding>() {

    companion object {
        const val RESULT_CODE_EDIT = 100
    }

    private val viewModel: AutoTextViewModel by viewModels()

    override fun setupViewBinding(): ActivityAutotextEditorBinding {
        return ActivityAutotextEditorBinding.inflate(layoutInflater)
    }

    override fun setupViewModel() {
        super.setupViewModel()
        viewModel.apply {
            eventSuccessState.observe(this@AutoTextEditorActivity) {
                setResult(RESULT_CODE_EDIT)
                finish()
            }
        }
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        setupDetailActivity("Editor Auto Text")
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            btnSave.setOnClickListener {
                setupInsert()
            }
        }
    }

    private fun setupInsert() {
        val title = binding.etTitle.text.toString()
        val body = binding.etContent.text.toString()
        viewModel.insertAutoText(title, body)
    }

}