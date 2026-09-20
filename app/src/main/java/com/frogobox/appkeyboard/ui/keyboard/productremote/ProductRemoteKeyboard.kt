package com.frogobox.appkeyboard.ui.keyboard.productremote

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.InputConnection
import android.widget.FrameLayout
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.frogobox.appkeyboard.common.core.Resource
import com.frogobox.appkeyboard.data.remote.ApiService
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import com.frogobox.appkeyboard.di.NetworkModule
import com.frogobox.appkeyboard.repository.data.DataApiRepository
import com.frogobox.appkeyboard.repository.data.DataApiRepositoryImpl
import com.frogobox.appkeyboard.ui.theme.compose.FrogoKeyboardTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Standalone FrameLayout custom view for Product Remote Keyboard panel.
 * Fetches from DataApiRepository / DataApiService (http://192.168.100.6:3000/api/data.json),
 * embeds ProductRemoteKeyboardScreen via ComposeView with FrogoKeyboardTheme.
 */
class ProductRemoteKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val repository: DataApiRepository = DataApiRepositoryImpl(
        ApiService.create(context, NetworkModule.BASE_URL)
    )
) : FrameLayout(context, attrs, defStyleAttr) {

    private val itemsState = MutableStateFlow<List<DataItemResponse>>(emptyList())
    private val isLoadingState = MutableStateFlow(false)
    private val errorMessageState = MutableStateFlow<String?>(null)

    private val viewJob = SupervisorJob()
    private val viewScope = CoroutineScope(Dispatchers.Main + viewJob)

    var currentInputConnection: InputConnection? = null
    var onBackClick: (() -> Unit)? = null

    private val composeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FrogoKeyboardTheme {
                val items by itemsState.collectAsState()
                val isLoading by isLoadingState.collectAsState()
                val errorMessage by errorMessageState.collectAsState()

                ProductRemoteKeyboardScreen(
                    items = items,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    onCommitText = { text ->
                        currentInputConnection?.commitText(text, 1)
                    },
                    onBackClick = {
                        onBackClick?.invoke()
                    },
                    onRefresh = {
                        fetchData()
                    }
                )
            }
        }
    }

    init {
        addView(composeView)
        fetchData()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewJob.cancelChildren()
    }

    fun setInputConnection(inputConnection: InputConnection?) {
        currentInputConnection = inputConnection
    }

    fun setOnBackClickListener(listener: () -> Unit) {
        onBackClick = listener
    }

    fun fetchData() {
        viewScope.launch {
            repository.fetchDataStream().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        isLoadingState.value = true
                    }
                    is Resource.Success -> {
                        isLoadingState.value = false
                        errorMessageState.value = null
                        val activeItems = resource.data.items.orEmpty()
                        itemsState.value = activeItems
                    }
                    is Resource.Error -> {
                        isLoadingState.value = false
                        errorMessageState.value = resource.message
                    }
                }
            }
        }
    }

}
