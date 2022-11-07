package com.frogobox.research.ui.keyboard

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import com.frogobox.api.news.ConsumeNewsApi
import com.frogobox.coreapi.ConsumeApiResponse
import com.frogobox.coreapi.news.NewsConstant.CATEGORY_HEALTH
import com.frogobox.coreapi.news.NewsConstant.COUNTRY_ID
import com.frogobox.coreapi.news.NewsUrl
import com.frogobox.coreapi.news.model.Article
import com.frogobox.coreapi.news.response.ArticleResponse
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding
import com.frogobox.research.core.BaseKeyboard
import com.frogobox.research.databinding.ItemKeyboardMainBinding
import com.frogobox.research.databinding.KeyboardMainBinding

/**
 * Created by Faisal Amir on 07/11/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

class MainKeyboard(
    context: Context,
    attrs: AttributeSet?,
) : BaseKeyboard<KeyboardMainBinding>(context, attrs) {

    override fun setupViewBinding(): KeyboardMainBinding {
        return KeyboardMainBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun onCreate() {
        setupData()
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
                }

                override fun onHideProgress() {
                    // Your Progress Hide
                }

            })

    }

    private fun setupRv(data: List<Article>) {
        binding?.apply {

            val adapterCallback = object : IFrogoBindingAdapter<Article, ItemKeyboardMainBinding>{
                override fun onItemClicked(
                    binding: ItemKeyboardMainBinding,
                    data: Article,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<Article>,
                ) {
                    // Your Clicked
                    currentInputConnection.commitText(data.title, 1)
                }

                override fun onItemLongClicked(
                    binding: ItemKeyboardMainBinding,
                    data: Article,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<Article>,
                ) {}

                override fun setViewBinding(parent: ViewGroup): ItemKeyboardMainBinding {
                    return ItemKeyboardMainBinding.inflate(LayoutInflater.from(context), parent, false)
                }

                override fun setupInitComponent(
                    binding: ItemKeyboardMainBinding,
                    data: Article,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<Article>,
                ) {
                    binding.apply {
                        tvItemKeyboardMain.text = data.title
                    }
                }
            }

            rvKeyboardMain.injectorBinding<Article, ItemKeyboardMainBinding>()
                .addData(data)
                .createLayoutLinearVertical(false)
                .addCallback(adapterCallback)
                .build()
        }
    }

}