package com.frogobox.appkeyboard.suggestion

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.frogobox.appkeyboard.databinding.LayoutKeyboardSuggestionBinding
import com.frogobox.sdk.ext.gone
import com.frogobox.sdk.ext.visible

/**
 * Custom Suggestion Strip View displaying the 3 candidate sections:
 * Section 1: User's typed word
 * Section 2: Predicted word (central hero)
 * Section 3: Auto-correct word
 */
class KeyboardSuggestionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val binding: LayoutKeyboardSuggestionBinding =
        LayoutKeyboardSuggestionBinding.inflate(LayoutInflater.from(context), this, true)

    private var currentResult: SuggestionResult = SuggestionResult.EMPTY

    var onCandidateSelected: ((selectedWord: String, candidateType: CandidateType) -> Unit)? = null
    var onSwitchMenuClicked: (() -> Unit)? = null
    var onCloseClicked: (() -> Unit)? = null

    enum class CandidateType {
        USER_WORD,
        PREDICTED_WORD,
        AUTOCORRECT_WORD
    }

    init {
        setupListeners()
    }

    private fun setupListeners() {
        binding.sectionUserWord.setOnClickListener {
            val word = currentResult.userWord
            if (word.isNotEmpty()) {
                onCandidateSelected?.invoke(word, CandidateType.USER_WORD)
            }
        }

        binding.sectionPredictedWord.setOnClickListener {
            val word = currentResult.predictedWord
            if (word.isNotEmpty()) {
                onCandidateSelected?.invoke(word, CandidateType.PREDICTED_WORD)
            }
        }

        binding.sectionAutocorrectWord.setOnClickListener {
            val word = currentResult.autoCorrectWord
            if (word.isNotEmpty()) {
                onCandidateSelected?.invoke(word, CandidateType.AUTOCORRECT_WORD)
            }
        }

        binding.btnSwitchMenu.setOnClickListener {
            onSwitchMenuClicked?.invoke()
        }

        binding.btnClearSuggestion.setOnClickListener {
            onCloseClicked?.invoke()
        }
    }

    /**
     * Updates the 3 suggestion candidates in real-time
     */
    fun setSuggestions(result: SuggestionResult) {
        currentResult = result

        // Section 1: User's literal word
        if (result.userWord.isNotEmpty()) {
            binding.tvUserWord.text = "“${result.userWord}”"
            binding.sectionUserWord.visible()
            binding.divider1.visible()
        } else {
            binding.tvUserWord.text = ""
            binding.sectionUserWord.gone()
            binding.divider1.gone()
        }

        // Section 2: Predicted word
        if (result.predictedWord.isNotEmpty()) {
            binding.tvPredictedWord.text = result.predictedWord
            binding.sectionPredictedWord.visible()
        } else {
            binding.tvPredictedWord.text = ""
            binding.sectionPredictedWord.gone()
        }

        // Section 3: Auto-correct word
        if (result.autoCorrectWord.isNotEmpty() && result.autoCorrectWord != result.predictedWord) {
            binding.tvAutocorrectWord.text = result.autoCorrectWord
            binding.sectionAutocorrectWord.visible()
            binding.divider2.visible()
        } else {
            binding.tvAutocorrectWord.text = ""
            binding.sectionAutocorrectWord.gone()
            binding.divider2.gone()
        }
    }

    fun clearSuggestions() {
        setSuggestions(SuggestionResult.EMPTY)
    }

}
