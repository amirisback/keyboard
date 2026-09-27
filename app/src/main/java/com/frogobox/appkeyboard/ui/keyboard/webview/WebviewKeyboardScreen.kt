package com.frogobox.appkeyboard.ui.keyboard.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard
import java.net.URLEncoder

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebviewKeyboardScreen(
    onCommitText: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRegisterKeyHandler: (((Int, Boolean) -> Boolean)?) -> Unit = {}
) {
    var urlInput by remember { mutableStateOf("https://www.google.com") }
    var currentWebUrl by remember { mutableStateOf("https://www.google.com") }
    var webProgress by remember { mutableIntStateOf(0) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    val focusManager = LocalFocusManager.current

    val loadUrlOrSearch: () -> Unit = {
        val query = urlInput.trim()
        val targetUrl = when {
            query.startsWith("http://") || query.startsWith("https://") -> query
            query.contains(".") && !query.contains(" ") -> "https://$query"
            query.isNotEmpty() -> "https://www.google.com/search?q=" + URLEncoder.encode(query, "UTF-8")
            else -> "https://www.google.com"
        }
        urlInput = targetUrl
        webViewInstance?.loadUrl(targetUrl)
        focusManager.clearFocus()
    }

    val handleKeyPress: (Int, Boolean) -> Boolean = { code, isShifted ->
        when (code) {
            ItemMainKeyboard.KEYCODE_DELETE -> {
                if (urlInput.isNotEmpty()) {
                    urlInput = urlInput.dropLast(1)
                }
                true
            }
            ItemMainKeyboard.KEYCODE_ENTER -> {
                loadUrlOrSearch()
                true
            }
            ItemMainKeyboard.KEYCODE_SPACE -> {
                urlInput += " "
                true
            }
            ItemMainKeyboard.KEYCODE_SHIFT,
            ItemMainKeyboard.KEYCODE_MODE_CHANGE,
            ItemMainKeyboard.KEYCODE_EMOJI -> {
                false
            }
            else -> {
                if (code > 0) {
                    var ch = code.toChar()
                    if (ch.isLetter() && isShifted) {
                        ch = ch.uppercaseChar()
                    }
                    urlInput += ch
                    true
                } else {
                    false
                }
            }
        }
    }

    DisposableEffect(handleKeyPress) {
        onRegisterKeyHandler(handleKeyPress)
        onDispose {
            onRegisterKeyHandler(null)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Web Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .clickable { onBackClick() }
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Search / URL input bar
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                BasicTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { loadUrlOrSearch() }),
                    modifier = Modifier.weight(1f)
                )

                if (urlInput.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { urlInput = "" }
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Reload Button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .clickable { webViewInstance?.reload() }
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reload",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Paste URL Pill
            Row(
                modifier = Modifier
                    .height(28.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        val activeUrl = webViewInstance?.url ?: currentWebUrl
                        if (activeUrl.isNotBlank()) {
                            onCommitText(activeUrl)
                            onBackClick()
                        }
                    }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = "Paste",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "Paste",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // Web Loading Progress Bar
        if (webProgress in 1..99) {
            LinearProgressIndicator(
                progress = { webProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            HorizontalDivider(
                thickness = 0.8.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            )
        }

        // Native WebView embedded in Compose
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            url?.let {
                                urlInput = it
                                currentWebUrl = it
                            }
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            webProgress = 100
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            webProgress = newProgress
                        }
                    }

                    loadUrl("https://www.google.com")
                    webViewInstance = this
                }
            },
            update = { webViewInstance = it },
            onRelease = { view ->
                try {
                    view.stopLoading()
                    view.destroy()
                } catch (_: Exception) {}
            }
        )
    }
}
