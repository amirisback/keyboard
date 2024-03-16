package com.frogobox.appkeyboard.ui.toggle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import com.frogobox.appkeyboard.databinding.ActivityToggleBinding
import com.frogobox.appkeyboard.databinding.ItemToggleBinding
import com.frogobox.appkeyboard.model.KeyboardFeature
import com.frogobox.appkeyboard.ui.main.BaseMainActivity
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding
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
class ToggleActivity : BaseMainActivity<ActivityToggleBinding>() {

    private val viewModel: ToggleViewModel by viewModels()

    override fun setupViewBinding(): ActivityToggleBinding {
        return ActivityToggleBinding.inflate(layoutInflater)
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.getKeyboardFeatureData()
        }

        setupDetailActivity("Toggle Feature")
    }

    override fun setupViewModel() {
        super.setupViewModel()
        viewModel.keyboardFeatureState.observe(this) {
            binding.root.injectorBinding<KeyboardFeature, ItemToggleBinding>()
                .addData(it)
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
                            viewModel.switchToggle(data.id, isChecked)
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

}