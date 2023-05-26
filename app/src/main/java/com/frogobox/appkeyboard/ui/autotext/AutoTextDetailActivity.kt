package com.frogobox.appkeyboard.ui.autotext

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.viewModels
import com.frogobox.appkeyboard.common.base.BaseActivity
import com.frogobox.appkeyboard.databinding.ActivityAutotextDetailBinding
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */


@AndroidEntryPoint
class AutoTextDetailActivity : BaseActivity<ActivityAutotextDetailBinding>() {

    companion object {
        const val EXTRA_AUTO_TEXT = "EXTRA_AUTO_TEXT"
        const val RESULT_CODE_DELETE = 109
    }

    private val viewModel: AutoTextViewModel by viewModels()

    override fun setupViewModel() {
        super.setupViewModel()
        viewModel.eventSuccessState.observe(this) {
            setResult(RESULT_CODE_DELETE)
            finish()
        }
    }

    override fun setupViewBinding(): ActivityAutotextDetailBinding {
        return ActivityAutotextDetailBinding.inflate(layoutInflater)
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        setupDetailActivity("Detail Auto Text")
        setupUI(autoText())
    }

    override fun setupActivityResultExt(result: ActivityResult) {
        super.setupActivityResultExt(result)
        if (result.resultCode == AutoTextEditorActivity.RESULT_CODE_UPDATE) {
            result.data?.getStringExtra(AutoTextEditorActivity.EXTRA_AUTO_TEXT_EDIT_RESULT)
            val data = Gson().fromJson(
                result.data?.getStringExtra(AutoTextEditorActivity.EXTRA_AUTO_TEXT_EDIT_RESULT),
                AutoTextEntity::class.java
            )
            setupUI(data)
        }
    }

    override fun doOnBackPressedExt() {
        setResult(AutoTextEditorActivity.RESULT_CODE_ADD)
        super.doOnBackPressedExt()
    }


    private fun autoText(): AutoTextEntity {
        return if (intent.hasExtra(EXTRA_AUTO_TEXT)) {
            Gson().fromJson(
                intent.extras?.getString(EXTRA_AUTO_TEXT),
                AutoTextEntity::class.java
            )
        } else {
            AutoTextEntity()
        }
    }

    private fun setupUI(data: AutoTextEntity) {
        binding.apply {

            tvTitle.text = data.title
            tvContent.text = data.body

            btnDelete.setOnClickListener {
                viewModel.deleteAutoText(data)
            }

            btnEdit.setOnClickListener {
                startActivityResultExt(
                    Intent(
                        this@AutoTextDetailActivity,
                        AutoTextEditorActivity::class.java
                    ).apply {
                        putExtra(
                            AutoTextEditorActivity.EXTRA_AUTO_TEXT_EDIT,
                            Gson().toJson(data)
                        )
                    }
                )
            }
        }
    }

}