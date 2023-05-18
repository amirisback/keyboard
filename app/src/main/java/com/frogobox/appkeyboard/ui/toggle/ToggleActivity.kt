package com.frogobox.appkeyboard.ui.toggle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.frogobox.appkeyboard.databinding.ActivityToggleBinding
import com.frogobox.appkeyboard.databinding.ItemToggleBinding
import com.frogobox.appkeyboard.model.KeyboardFeature
import com.frogobox.appkeyboard.services.KeyboardUtil
import com.frogobox.appkeyboard.ui.main.BaseMainActivity
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding
import com.frogobox.sdk.delegate.preference.PreferenceDelegates
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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
class ToggleActivity : BaseMainActivity<ActivityToggleBinding>() {

    @Inject
    lateinit var pref : PreferenceDelegates

    override fun setupViewBinding(): ActivityToggleBinding {
        return ActivityToggleBinding.inflate(layoutInflater)
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        setupDetailActivity("Toggle Feature")
        setupRV()
    }

    private fun setupRV() {
        binding.root.injectorBinding<KeyboardFeature, ItemToggleBinding>()
            .addData(KeyboardUtil().menuToggle())
            .addCallback(object :
                IFrogoBindingAdapter<KeyboardFeature, ItemToggleBinding> {

                override fun setViewBinding(parent: ViewGroup): ItemToggleBinding {
                    return ItemToggleBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                }

                override fun setupInitComponent(
                    binding: ItemToggleBinding,
                    data: KeyboardFeature,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<KeyboardFeature>,
                ) {
                    binding.ivIcon.setImageResource(data.icon)
                    binding.tvTitle.text = data.type.title
                    binding.swToggle.isChecked = data.state
                    binding.swToggle.setOnCheckedChangeListener { _, isChecked ->
                        pref.savePrefBoolean(data.id, isChecked)
                    }
                }

                override fun onItemClicked(
                    binding: ItemToggleBinding,
                    data: KeyboardFeature,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<KeyboardFeature>,
                ) {}

                override fun onItemLongClicked(
                    binding: ItemToggleBinding,
                    data: KeyboardFeature,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<KeyboardFeature>,
                ) {}

            })
            .createLayoutGrid(2)
            .build()
    }


}