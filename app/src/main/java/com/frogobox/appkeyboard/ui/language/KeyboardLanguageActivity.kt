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
import com.frogobox.sdk.ext.showToast
import com.frogobox.sdk.ext.visible
import com.frogobox.sdk.util.FrogoFunc
import com.frogobox.sdk.util.SimpleDialogUtil
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

    override fun areContentsTheSame(oldItem: KeyboardLanguage, newItem: KeyboardLanguage): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: KeyboardLanguage, newItem: KeyboardLanguage): Boolean {
        return oldItem.xml == newItem.xml
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
        applyLanguage(data)
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

    private fun applyLanguage(data: KeyboardLanguage) {
        SimpleDialogUtil.create(
            this@KeyboardLanguageActivity,
            "Change Theme",
            "Are you sure you want to change keyboard theme?",
            object  : SimpleDialogUtil.OnDialogClickListener {

                override fun negativeButton() {}
                override fun positiveButton() {
                    viewModel.setKeyboard(data.xml) {
                        showToast("${data.name} Language Applied")
                        viewModel.getKeyboardLanguage(this@KeyboardLanguageActivity)
                    }
                }
            }
        )
    }

}