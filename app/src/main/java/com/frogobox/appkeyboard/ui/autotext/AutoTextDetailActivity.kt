package com.frogobox.appkeyboard.ui.autotext

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.frogobox.appkeyboard.common.base.BaseComposeActivity
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AutoTextDetailActivity : BaseComposeActivity() {

    companion object {
        const val EXTRA_AUTO_TEXT = "EXTRA_AUTO_TEXT"
        const val RESULT_CODE_DELETE = 109
    }

    private val viewModel: AutoTextViewModel by viewModels()

    private var currentData by mutableStateOf(AutoTextEntity())

    private val editLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AutoTextEditorActivity.RESULT_CODE_UPDATE) {
            val json = result.data?.getStringExtra(AutoTextEditorActivity.EXTRA_AUTO_TEXT_EDIT_RESULT)
            if (!json.isNullOrBlank()) {
                val updatedData = Gson().fromJson(json, AutoTextEntity::class.java)
                if (updatedData != null) {
                    currentData = updatedData
                }
            }
        }
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        currentData = extractAutoText()

        viewModel.eventSuccessState.observe(this) {
            setResult(RESULT_CODE_DELETE)
            finish()
        }
    }

    override fun doOnBackPressedExt() {
        setResult(AutoTextEditorActivity.RESULT_CODE_ADD)
        super.doOnBackPressedExt()
    }

    private fun extractAutoText(): AutoTextEntity {
        return if (intent.hasExtra(EXTRA_AUTO_TEXT)) {
            Gson().fromJson(
                intent.extras?.getString(EXTRA_AUTO_TEXT),
                AutoTextEntity::class.java
            ) ?: AutoTextEntity()
        } else {
            AutoTextEntity()
        }
    }

    @Composable
    override fun Content() {
        AutoTextDetailScreen(
            data = currentData,
            onEditClick = {
                editLauncher.launch(
                    Intent(this@AutoTextDetailActivity, AutoTextEditorActivity::class.java).apply {
                        putExtra(
                            AutoTextEditorActivity.EXTRA_AUTO_TEXT_EDIT,
                            Gson().toJson(currentData)
                        )
                    }
                )
            },
            onDeleteClick = {
                viewModel.deleteAutoText(currentData)
            },
            onBackClick = {
                doOnBackPressedExt()
            }
        )
    }
}