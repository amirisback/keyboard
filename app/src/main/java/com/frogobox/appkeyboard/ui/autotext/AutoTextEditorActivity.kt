package com.frogobox.appkeyboard.ui.autotext

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.frogobox.appkeyboard.common.base.BaseActivity
import com.frogobox.appkeyboard.databinding.ActivityAutotextEditorBinding
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.sdk.ext.toText
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */


@AndroidEntryPoint
class AutoTextEditorActivity : BaseActivity<ActivityAutotextEditorBinding>() {

    companion object {
        const val EXTRA_AUTO_TEXT_EDIT = "EXTRA_AUTO_TEXT_EDIT"
        const val EXTRA_AUTO_TEXT_EDIT_RESULT = "EXTRA_AUTO_TEXT_EDIT_RESULT"
        const val RESULT_CODE_ADD = 100
        const val RESULT_CODE_UPDATE = 101
    }

    private val viewModel: AutoTextViewModel by viewModels()

    override fun setupViewBinding(): ActivityAutotextEditorBinding {
        return ActivityAutotextEditorBinding.inflate(layoutInflater)
    }

    override fun setupViewModel() {
        super.setupViewModel()
        viewModel.apply {
            eventSuccessState.observe(this@AutoTextEditorActivity) {
                if (hasExtraDetail()) {
                    setResult(RESULT_CODE_UPDATE, Intent().apply {
                        val extra = AutoTextEntity(
                            id = autoText().id,
                            title = binding.etTitle.toText(),
                            body = binding.etContent.toText()
                        )
                        putExtra(EXTRA_AUTO_TEXT_EDIT_RESULT, Gson().toJson(extra))
                    })
                } else {
                    setResult(RESULT_CODE_ADD)
                }
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

            if (hasExtraDetail()) {
                btnSave.text = "Update"
                etTitle.setText(autoText().title)
                etContent.setText(autoText().body)
            }

            btnSave.setOnClickListener {
                if (hasExtraDetail()) {
                    setupUpdate()
                } else {
                    setupInsert()
                }
            }
        }
    }

    private fun autoText() : AutoTextEntity {
        return Gson().fromJson(
            intent.extras?.getString(EXTRA_AUTO_TEXT_EDIT),
            AutoTextEntity::class.java
        )
    }

    private fun hasExtraDetail() : Boolean {
        return intent.hasExtra(EXTRA_AUTO_TEXT_EDIT)
    }

    private fun setupInsert() {
        val title = binding.etTitle.text.toString()
        val body = binding.etContent.text.toString()
        viewModel.insertAutoText(title, body)
    }

    private fun setupUpdate() {
        val title = binding.etTitle.toText()
        val body = binding.etContent.toText()
        viewModel.updateAutoText(autoText().id, title, body)
    }

}