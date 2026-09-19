package com.frogobox.appkeyboard.ui.keyboard.webview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import com.frogobox.appkeyboard.databinding.KeyboardWebviewBinding
import com.frogobox.libkeyboard.common.core.BaseKeyboard
import com.frogobox.sdk.ext.gone
import com.frogobox.sdk.ext.visible

/**
 * Created by Faisal Amir on 07/11/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

class WebiewKeyboard(
    context: Context,
    attrs: AttributeSet?,
) : BaseKeyboard<KeyboardWebviewBinding>(context, attrs) {

    override fun setupViewBinding(inflater: LayoutInflater, parent: LinearLayout): KeyboardWebviewBinding {
        return KeyboardWebviewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initUI() {
        super.initUI()
        binding.apply {
            webview.settings.javaScriptEnabled = true
            webview.settings.domStorageEnabled = true

            webview.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    pbWebLoading.visible()
                    url?.let { etSearchUrl.setText(it) }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    pbWebLoading.gone()
                }
            }

            webview.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    pbWebLoading.progress = newProgress
                    if (newProgress >= 100) {
                        pbWebLoading.gone()
                    } else {
                        pbWebLoading.visible()
                    }
                }
            }

            val loadUrlOrSearch = {
                val query = etSearchUrl.text?.toString()?.trim() ?: ""
                val targetUrl = when {
                    query.startsWith("http://") || query.startsWith("https://") -> query
                    query.contains(".") && !query.contains(" ") -> "https://$query"
                    query.isNotEmpty() -> "https://www.google.com/search?q=" + java.net.URLEncoder.encode(query, "UTF-8")
                    else -> "https://www.google.com"
                }
                webview.loadUrl(targetUrl)
            }

            etSearchUrl.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_GO) {
                    loadUrlOrSearch()
                    true
                } else false
            }

            btnReload.setOnClickListener {
                webview.reload()
            }

            btnInsertLink.setOnClickListener {
                val currentUrl = webview.url
                if (!currentUrl.isNullOrBlank()) {
                    currentInputConnection?.commitText(currentUrl, 1)
                }
            }

            webview.loadUrl("https://www.google.com")
        }
    }

}