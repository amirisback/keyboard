package com.frogobox.appkeyboard.ui.keyboard.movie

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.InputConnection
import android.widget.FrameLayout
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.frogobox.api.movie.ConsumeMovieApi
import com.frogobox.coresdk.response.FrogoDataResponse
import com.frogobox.coreutil.movie.MovieUrl
import com.frogobox.coreutil.movie.model.TrendingMovie
import com.frogobox.coreutil.movie.response.Trending
import com.frogobox.appkeyboard.ui.theme.compose.FrogoKeyboardTheme
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Modern Jetpack Compose-based Trending Movies Keyboard panel.
 */
class MovieKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val movieListState = MutableStateFlow<List<TrendingMovie>>(emptyList())
    private val isLoadingState = MutableStateFlow(false)

    var currentInputConnection: InputConnection? = null
    var onBackClick: (() -> Unit)? = null

    private val composeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FrogoKeyboardTheme {
                val movies by movieListState.collectAsState()
                val loading by isLoadingState.collectAsState()

                MovieKeyboardScreen(
                    movieList = movies,
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
        fetchMovies()
    }

    fun setInputConnection(inputConnection: InputConnection?) {
        currentInputConnection = inputConnection
    }

    fun setOnBackClickListener(listener: () -> Unit) {
        onBackClick = listener
    }

    fun fetchMovies() {
        val consumeMovieApi = ConsumeMovieApi(MovieUrl.API_KEY)
        consumeMovieApi.getTrendingMovieDay(object : FrogoDataResponse<Trending<TrendingMovie>> {
            override fun onFailed(statusCode: Int, errorMessage: String) {
                isLoadingState.value = false
            }

            override fun onFinish() {
                isLoadingState.value = false
            }

            override fun onHideProgress() {
                isLoadingState.value = false
            }

            override fun onShowProgress() {
                isLoadingState.value = true
            }

            override fun onSuccess(data: Trending<TrendingMovie>) {
                isLoadingState.value = false
                data.results?.let { movieListState.value = it }
            }
        })
    }

}