package com.frogobox.appkeyboard.ui.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import com.frogobox.appkeyboard.common.base.BaseActivity
import com.frogobox.appkeyboard.databinding.ActivityKeyboardLanguageBinding
import com.frogobox.appkeyboard.databinding.ItemLanguageKeyboardBinding
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding
import com.frogobox.sdk.ext.gone
import com.frogobox.sdk.ext.visible
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

@AndroidEntryPoint
class KeyboardLanguageActivity : BaseActivity<ActivityKeyboardLanguageBinding>(),
    IFrogoBindingAdapter<KeyboardLanguage, ItemLanguageKeyboardBinding> {

    private val viewModel: KeyboardLanguageViewModel by viewModels()

    override fun setupViewBinding(): ActivityKeyboardLanguageBinding {
        return ActivityKeyboardLanguageBinding.inflate(layoutInflater)
    }

    override fun setupViewModel() {
        super.setupViewModel()
        viewModel.apply {
            keyboardLanguage.observe(this@KeyboardLanguageActivity) {
                setupRV(it)
            }
        }
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.getKeyboardLanguage(this)
        }
    }

    override fun initView() {
        super.initView()
        setupDetailActivity("Keyboard Language")
        binding.apply {

        }
    }

    override fun setViewBinding(parent: ViewGroup): ItemLanguageKeyboardBinding {
        return ItemLanguageKeyboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onItemClicked(
        binding: ItemLanguageKeyboardBinding,
        data: KeyboardLanguage,
        position: Int,
        notifyListener: FrogoRecyclerNotifyListener<KeyboardLanguage>
    ) {
        viewModel.setKeyboard(data.xml) {
            viewModel.getKeyboardLanguage(this@KeyboardLanguageActivity)
        }
    }

    override fun onItemLongClicked(
        binding: ItemLanguageKeyboardBinding,
        data: KeyboardLanguage,
        position: Int,
        notifyListener: FrogoRecyclerNotifyListener<KeyboardLanguage>
    ) {}

    override fun setupInitComponent(
        binding: ItemLanguageKeyboardBinding,
        data: KeyboardLanguage,
        position: Int,
        notifyListener: FrogoRecyclerNotifyListener<KeyboardLanguage>
    ) {
        binding.apply {
            tvItemKeyboardMain.text = data.name
            if (viewModel.checkKeyboardType(data.xml)) {
                this.ivIcon.visible()
            } else {
                this.ivIcon.gone()
            }
        }
    }

    private fun setupRV(data: List<KeyboardLanguage>) {
        binding.rvKeyboardLanguage
            .injectorBinding<KeyboardLanguage, ItemLanguageKeyboardBinding>()
            .addData(data)
            .addCallback(this)
            .createLayoutLinearVertical(false)
            .build()
    }

}