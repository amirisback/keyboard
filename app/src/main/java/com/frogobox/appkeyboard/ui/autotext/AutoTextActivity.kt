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
import com.frogobox.coresdk.source.Resource
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AutoTextActivity : BaseComposeActivity() {

    private val viewModel: AutoTextViewModel by viewModels()

    private var autoTextList by mutableStateOf<List<AutoTextEntity>>(emptyList())
    private var isLoading by mutableStateOf(false)

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            AutoTextDetailActivity.RESULT_CODE_DELETE,
            AutoTextEditorActivity.RESULT_CODE_UPDATE,
            AutoTextEditorActivity.RESULT_CODE_ADD -> {
                viewModel.getAutoText()
            }
        }
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        viewModel.autoText.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    isLoading = true
                }
                is Resource.Success -> {
                    isLoading = false
                    autoTextList = resource.result
                }
                is Resource.Error -> {
                    isLoading = false
                }
            }
        }
        viewModel.getAutoText()
    }

    @Composable
    override fun Content() {
        AutoTextScreen(
            autoTextList = autoTextList,
            isLoading = isLoading,
            onItemClick = { data ->
                val extra = Gson().toJson(data)
                activityResultLauncher.launch(
                    Intent(this@AutoTextActivity, AutoTextDetailActivity::class.java).apply {
                        putExtra(AutoTextDetailActivity.EXTRA_AUTO_TEXT, extra)
                    }
                )
            },
            onAddClick = {
                activityResultLauncher.launch(
                    Intent(this@AutoTextActivity, AutoTextEditorActivity::class.java)
                )
            },
            onBackClick = { finish() }
        )
    }
}