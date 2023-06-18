package com.frogobox.appkeyboard.ui.keyboard.movie

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.frogobox.api.movie.ConsumeMovieApi
import com.frogobox.appkeyboard.databinding.ItemKeyboardMovieBinding
import com.frogobox.appkeyboard.databinding.KeyboardGridBinding
import com.frogobox.coresdk.response.FrogoDataResponse
import com.frogobox.coreutil.movie.MovieUrl
import com.frogobox.coreutil.movie.model.TrendingMovie
import com.frogobox.coreutil.movie.response.Trending
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

class MovieKeyboard(
    context: Context,
    attrs: AttributeSet?,
) : BaseKeyboard<KeyboardGridBinding>(context, attrs) {

    override fun setupViewBinding(): KeyboardGridBinding {
        return KeyboardGridBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun onCreate() {
        setupData()
        initView()
    }

    private fun initView() {
        binding?.apply {
            tvToolbarTitle.text = "Movie Api"
        }
    }

    private fun setupData() {
        val consumeMovieApi = ConsumeMovieApi(MovieUrl.API_KEY) // Your API_KEY
        consumeMovieApi.getTrendingMovieDay(object : FrogoDataResponse<Trending<TrendingMovie>> {
            override fun onFailed(statusCode: Int, errorMessage: String) {

            }

            override fun onFinish() {

            }

            override fun onHideProgress() {
                binding?.progressBar?.gone()
            }

            override fun onShowProgress() {
                binding?.progressBar?.visible()
            }

            override fun onSuccess(data: Trending<TrendingMovie>) {
                data.results?.let { setupRv(it) }
            }

        })
    }

    private fun setupRv(data: List<TrendingMovie>) {
        binding?.apply {

            val adapterCallback =
                object : IFrogoBindingAdapter<TrendingMovie, ItemKeyboardMovieBinding> {
                    override fun onItemClicked(
                        binding: ItemKeyboardMovieBinding,
                        data: TrendingMovie,
                        position: Int,
                        notifyListener: FrogoRecyclerNotifyListener<TrendingMovie>,
                    ) {
                        // Your Clicked
                        Log.d("FrogoKeyboard", "onItemClicked: ${data.title}")
                        Log.d("FrogoKeyboard", "currentInputConnection: $currentInputConnection")
                        val output = "Movie: ${data.title} - ${data.release_date} \n " +
                                "Overview: ${data.overview} \n " +
                                "Vote: ${data.vote_average} \n " +
                                "${MovieUrl.BASE_URL_IMAGE_ORIGNAL}${data.poster_path}" +
                                "\n" +
                                "Terima Kasih"
                        currentInputConnection?.commitText(output, 1)
                    }

                    override fun onItemLongClicked(
                        binding: ItemKeyboardMovieBinding,
                        data: TrendingMovie,
                        position: Int,
                        notifyListener: FrogoRecyclerNotifyListener<TrendingMovie>,
                    ) {
                    }

                    override fun setViewBinding(parent: ViewGroup): ItemKeyboardMovieBinding {
                        return ItemKeyboardMovieBinding.inflate(LayoutInflater.from(context),
                            parent,
                            false)
                    }

                    override fun setupInitComponent(
                        binding: ItemKeyboardMovieBinding,
                        data: TrendingMovie,
                        position: Int,
                        notifyListener: FrogoRecyclerNotifyListener<TrendingMovie>,
                    ) {
                        binding.apply {
                            frogoRvGridType6TvTitle.text = data.title
                            frogoRvGridType6TvSubtitle.text = data.release_date
                            frogoRvGridType6TvDesc.text = data.overview
                            Glide.with(context).load("${MovieUrl.BASE_URL_IMAGE_ORIGNAL}${data.poster_path}").into(frogoRvGridType6CivPoster)
                        }
                    }
                }

            rvKeyboardMain.injectorBinding<TrendingMovie, ItemKeyboardMovieBinding>()
                .addData(data)
                .createLayoutStaggeredGrid(2)
                .addCallback(adapterCallback)
                .build()
        }
    }

}