package com.frogobox.appkeyboard.ui.test

import androidx.lifecycle.viewModelScope
import com.frogobox.appkeyboard.common.base.BaseViewModel
import com.frogobox.appkeyboard.common.core.Resource
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.AutoTextLabelType
import com.frogobox.appkeyboard.repository.autotext.AutoTextRepository
import com.frogobox.appkeyboard.repository.data.DataApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

/**
 * Created by Faisal Amir on 24/10/22
 * Redesigned for TASK-010 and TASK-015 by Tim Mobile
 */
@HiltViewModel
class TestViewModel @Inject constructor(
    private val repository: AutoTextRepository,
    private val dataApiRepository: DataApiRepository
) : BaseViewModel() {

    private val _sandboxText = MutableStateFlow("")
    val sandboxText: StateFlow<String> = _sandboxText.asStateFlow()

    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _metrics = MutableStateFlow(TypingMetrics())
    val metrics: StateFlow<TypingMetrics> = _metrics.asStateFlow()

    private val _autoTextList = MutableStateFlow<List<AutoTextEntity>>(emptyList())
    val autoTextList: StateFlow<List<AutoTextEntity>> = _autoTextList.asStateFlow()

    private val _remoteApiUiState = MutableStateFlow<DataApiUiState>(DataApiUiState.Idle)
    val remoteApiUiState: StateFlow<DataApiUiState> = _remoteApiUiState.asStateFlow()

    private var typingStartTimestamp: Long = 0L

    init {
        loadAutoTextSnippets()
    }

    private fun loadAutoTextSnippets() {
        viewModelScope.launch {
            repository.getAutoText()
                .catch { emit(getDefaultTestSnippets()) }
                .collect { list ->
                    if (list.isEmpty()) {
                        _autoTextList.value = getDefaultTestSnippets()
                    } else {
                        _autoTextList.value = list
                    }
                }
        }
    }

    private fun getDefaultTestSnippets(): List<AutoTextEntity> {
        return listOf(
            AutoTextEntity(
                id = -1,
                title = "Greeting",
                body = "Hello! Hope you are having a wonderful and productive day.",
                label = AutoTextLabelType.DEFAULT,
                isActive = true
            ),
            AutoTextEntity(
                id = -2,
                title = "Meeting Confirmation",
                body = "Looking forward to our sync meeting tomorrow at 10:00 AM.",
                label = AutoTextLabelType.DEFAULT,
                isActive = true
            ),
            AutoTextEntity(
                id = -3,
                title = "Quick Sign-off",
                body = "Kind regards,\nFrogo Keyboard Development Team",
                label = AutoTextLabelType.DEFAULT,
                isActive = true
            ),
            AutoTextEntity(
                id = -4,
                title = "Fast Response",
                body = "Thanks for reaching out! I will review and reply as soon as possible.",
                label = AutoTextLabelType.DEFAULT,
                isActive = true
            )
        )
    }

    fun onTextChanged(newText: String) {
        _sandboxText.value = newText
        val now = System.currentTimeMillis()
        if (typingStartTimestamp == 0L && newText.isNotEmpty()) {
            typingStartTimestamp = now
        }
        val elapsedMillis = if (typingStartTimestamp > 0L) now - typingStartTimestamp else 0L
        _metrics.value = TypingMetricsCalculator.calculate(newText, elapsedMillis)
    }

    fun onInsertText(textToInsert: String) {
        val current = _sandboxText.value
        val updated = if (current.isEmpty()) {
            textToInsert
        } else {
            "$current\n$textToInsert"
        }
        onTextChanged(updated)
    }

    fun onClearText() {
        _sandboxText.value = ""
        typingStartTimestamp = 0L
        _metrics.value = TypingMetrics()
    }

    fun onResetTimer() {
        typingStartTimestamp = System.currentTimeMillis()
        _metrics.value = TypingMetricsCalculator.calculate(_sandboxText.value, 0L)
    }

    fun onTabSelected(tabIndex: Int) {
        _activeTab.value = tabIndex
    }

    fun fetchRemoteData() {
        viewModelScope.launch {
            dataApiRepository.fetchDataStream().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _remoteApiUiState.value = DataApiUiState.Loading
                    }
                    is Resource.Success -> {
                        val items = resource.data.data ?: emptyList()
                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                        }
                        val nowWib = "${sdf.format(Date())} WIB"
                        _remoteApiUiState.value = DataApiUiState.Success(
                            total = resource.data.total ?: items.size,
                            lastUpdatedWib = resource.data.lastUpdated ?: nowWib,
                            items = items
                        )
                    }
                    is Resource.Error -> {
                        _remoteApiUiState.value = DataApiUiState.Error(
                            message = resource.message
                        )
                    }
                }
            }
        }
    }

    fun resetRemoteData() {
        _remoteApiUiState.value = DataApiUiState.Idle
    }
}