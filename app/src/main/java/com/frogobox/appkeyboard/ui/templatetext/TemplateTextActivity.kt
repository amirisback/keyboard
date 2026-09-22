package com.frogobox.appkeyboard.ui.templatetext

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.frogobox.appkeyboard.common.base.BaseComposeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TemplateTextActivity : BaseComposeActivity() {

    companion object {
        const val EXTRA_INITIAL_CATEGORY = "EXTRA_INITIAL_CATEGORY"
    }

    private val viewModel: TemplateTextViewModel by viewModels()

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        val initialCategory = intent.getStringExtra(EXTRA_INITIAL_CATEGORY)
        if (!initialCategory.isNullOrBlank()) {
            viewModel.setCategory(initialCategory)
        }
    }

    @Composable
    override fun Content() {
        val templateList by viewModel.templateList.collectAsState()
        val selectedCategory by viewModel.selectedCategory.collectAsState()
        val searchQuery by viewModel.searchQuery.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val eventMessage by viewModel.eventMessage.collectAsState()

        LaunchedEffect(eventMessage) {
            eventMessage?.let { msg ->
                Toast.makeText(this@TemplateTextActivity, msg, Toast.LENGTH_SHORT).show()
                viewModel.clearEventMessage()
            }
        }

        TemplateTextScreen(
            templateList = templateList,
            selectedCategory = selectedCategory,
            searchQuery = searchQuery,
            isLoading = isLoading,
            onCategorySelected = { category ->
                viewModel.setCategory(category)
            },
            onSearchQueryChange = { query ->
                viewModel.setSearchQuery(query)
            },
            onAddTemplate = { category, text ->
                viewModel.insertTemplate(category, text)
            },
            onUpdateTemplate = { id, category, text ->
                viewModel.updateTemplate(id, category, text)
            },
            onDeleteTemplate = { id ->
                viewModel.deleteTemplate(id)
            },
            onResetAllToDefaults = {
                viewModel.resetAllToDefaults()
            },
            onBackClick = { finish() }
        )
    }
}
