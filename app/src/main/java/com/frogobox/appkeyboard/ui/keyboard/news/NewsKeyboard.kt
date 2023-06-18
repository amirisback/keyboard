package com.frogobox.appkeyboard.ui.keyboard.news

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.frogobox.api.news.ConsumeNewsApi
import com.frogobox.appkeyboard.databinding.ItemKeyboardNewsBinding
import com.frogobox.appkeyboard.databinding.KeyboardListBinding
import com.frogobox.coreapi.ConsumeApiResponse
import com.frogobox.coreutil.news.NewsConstant.CATEGORY_HEALTH
import com.frogobox.coreutil.news.NewsConstant.COUNTRY_ID
import com.frogobox.coreutil.news.NewsUrl
import com.frogobox.coreutil.news.model.Article
import com.frogobox.coreutil.news.response.ArticleResponse

import com.frogobox.libkeyboard.common.core.BaseKeyboard
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding
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

class NewsKeyboard(
    context: Context,
    attrs: AttributeSet?,
) : BaseKeyboard<KeyboardListBinding>(context, attrs) {

    override fun setupViewBinding(): KeyboardListBinding {
        return KeyboardListBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun onCreate() {
        setupData()
        initView()
    }

    private fun initView() {
        binding?.apply {
            tvToolbarTitle.text = "News Api"
        }
    }

    private fun setupData() {
        val consumeNewsApi = ConsumeNewsApi(NewsUrl.API_KEY) // Your API_KEY
        consumeNewsApi.getTopHeadline( // Adding Base Parameter on main function
            null,
            null,
            CATEGORY_HEALTH,
            COUNTRY_ID,
            null,
            null,
            object : ConsumeApiResponse<ArticleResponse> {
                override fun onSuccess(data: ArticleResponse) {
                    // Your Ui or data
                    data.articles?.let { setupRv(it) }
                }

                override fun onFailed(statusCode: Int, errorMessage: String) {
                    // Your failed to do
                }

                override fun onFinish() {
                    // Your finish to do
                }

                override fun onShowProgress() {
                    // Your Progress Show
                    binding?.progressBar?.visible()
                }

                override fun onHideProgress() {
                    // Your Progress Hide
                    binding?.progressBar?.gone()
                }

            })

    }

    private fun setupRv(data: List<Article>) {
        binding?.apply {

            val adapterCallback = object : IFrogoBindingAdapter<Article, ItemKeyboardNewsBinding> {
                override fun onItemClicked(
                    binding: ItemKeyboardNewsBinding,
                    data: Article,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<Article>,
                ) {
                    // Your Clicked
                    Log.d("NewsKeyboard", "onItemClicked: ${data.title}")
                    Log.d("FrogoKeyboard", "currentInputConnection: $currentInputConnection")
                    val output = "${data.title}\n" +
                            "${data.author}\n" +
                            "\n" +
                            "${data.description}\n" +
                            "\n" +
                            "Sumber : ${data.url}" +
                            "\n" +
                            "Terima Kasih"
                    currentInputConnection?.commitText(output, 1)
                }

                override fun onItemLongClicked(
                    binding: ItemKeyboardNewsBinding,
                    data: Article,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<Article>,
                ) {
                }

                override fun setViewBinding(parent: ViewGroup): ItemKeyboardNewsBinding {
                    return ItemKeyboardNewsBinding.inflate(LayoutInflater.from(context),
                        parent,
                        false)
                }

                override fun setupInitComponent(
                    binding: ItemKeyboardNewsBinding,
                    data: Article,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<Article>,
                ) {
                    binding.apply {
                        tvItemKeyboardMain.text = data.title
                    }
                }
            }

            rvKeyboardMain.injectorBinding<Article, ItemKeyboardNewsBinding>()
                .addData(data)
                .createLayoutLinearVertical(false)
                .addCallback(adapterCallback)
                .build()
        }
    }

}