package com.frogobox.appkeyboard.ui.autotext

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.frogobox.appkeyboard.common.base.BaseComposeActivity
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AutoTextEditorActivity : BaseComposeActivity() {

    companion object {
        const val EXTRA_AUTO_TEXT_EDIT = "EXTRA_AUTO_TEXT_EDIT"
        const val EXTRA_AUTO_TEXT_EDIT_RESULT = "EXTRA_AUTO_TEXT_EDIT_RESULT"
        const val RESULT_CODE_ADD = 100
        const val RESULT_CODE_UPDATE = 101
    }

    private val viewModel: AutoTextViewModel by viewModels()

    private var lastSavedTitle: String = ""
    private var lastSavedBody: String = ""

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)

        viewModel.eventSuccessState.observe(this) {
            if (hasExtraDetail()) {
                setResult(RESULT_CODE_UPDATE, Intent().apply {
                    val extra = AutoTextEntity(
                        id = autoText().id,
                        title = lastSavedTitle,
                        body = lastSavedBody
                    )
                    putExtra(EXTRA_AUTO_TEXT_EDIT_RESULT, Gson().toJson(extra))
                })
            } else {
                setResult(RESULT_CODE_ADD)
            }
            finish()
        }
    }

    private fun autoText(): AutoTextEntity {
        return Gson().fromJson(
            intent.extras?.getString(EXTRA_AUTO_TEXT_EDIT),
            AutoTextEntity::class.java
        ) ?: AutoTextEntity()
    }

    private fun hasExtraDetail(): Boolean {
        return intent.hasExtra(EXTRA_AUTO_TEXT_EDIT)
    }

    @Composable
    override fun Content() {
        val detail = autoText()
        val isEdit = hasExtraDetail()

        AutoTextEditorScreen(
            initialTitle = if (isEdit) detail.title else "",
            initialBody = if (isEdit) detail.body else "",
            isEditMode = isEdit,
            onSave = { title, body ->
                lastSavedTitle = title
                lastSavedBody = body
                if (isEdit) {
                    viewModel.updateAutoText(detail.id, title, body)
                } else {
                    viewModel.insertAutoText(title, body)
                }
            },
            onBackClick = { finish() }
        )
    }
}