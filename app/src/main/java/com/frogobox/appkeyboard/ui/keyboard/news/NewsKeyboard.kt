package com.frogobox.appkeyboard.ui.keyboard.news

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.InputConnection
import android.widget.FrameLayout
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.frogobox.api.news.ConsumeNewsApi
import com.frogobox.coreapi.ConsumeApiResponse
import com.frogobox.coreutil.news.NewsConstant.CATEGORY_HEALTH
import com.frogobox.coreutil.news.NewsConstant.COUNTRY_ID
import com.frogobox.coreutil.news.NewsUrl
import com.frogobox.coreutil.news.model.Article
import com.frogobox.coreutil.news.response.ArticleResponse
import com.frogobox.appkeyboard.ui.theme.compose.FrogoKeyboardTheme
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Modern Jetpack Compose-based News Headlines Keyboard panel.
 */
class NewsKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val articleListState = MutableStateFlow<List<Article>>(emptyList())
    private val isLoadingState = MutableStateFlow(false)

    var currentInputConnection: InputConnection? = null
    var onBackClick: (() -> Unit)? = null

    private val composeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FrogoKeyboardTheme {
                val articles by articleListState.collectAsState()
                val loading by isLoadingState.collectAsState()

                NewsKeyboardScreen(
                    articles = articles,
                    isLoading = loading,
                    onCommitText = { text ->
                        currentInputConnection?.commitText(text, 1)
                    },
                    onBackClick = {
                        onBackClick?.invoke()
                    }
                )
            }
        }
    }

    init {
        addView(composeView)
        fetchNews()
    }

    fun setInputConnection(inputConnection: InputConnection?) {
        currentInputConnection = inputConnection
    }

    fun setOnBackClickListener(listener: () -> Unit) {
        onBackClick = listener
    }

    fun fetchNews() {
        val consumeNewsApi = ConsumeNewsApi(NewsUrl.API_KEY)
        consumeNewsApi.getTopHeadline(
            null,
            null,
            CATEGORY_HEALTH,
            COUNTRY_ID,
            null,
            null,
            object : ConsumeApiResponse<ArticleResponse> {
                override fun onSuccess(data: ArticleResponse) {
                    isLoadingState.value = false
                    data.articles?.let { articleListState.value = it }
                }

                override fun onFailed(statusCode: Int, errorMessage: String) {
                    isLoadingState.value = false
                }

                override fun onFinish() {
                    isLoadingState.value = false
                }

                override fun onShowProgress() {
                    isLoadingState.value = true
                }

                override fun onHideProgress() {
                    isLoadingState.value = false
                }
            }
        )
    }

}