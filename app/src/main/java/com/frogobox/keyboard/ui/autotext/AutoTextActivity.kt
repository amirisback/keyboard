package com.frogobox.keyboard.ui.autotext

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import com.frogobox.keyboard.common.base.BaseActivity
import com.frogobox.keyboard.data.local.autotext.AutoTextEntity
import com.frogobox.keyboard.databinding.ActivityAutotextBinding
import com.frogobox.keyboard.databinding.ItemAutotextBinding
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */


@AndroidEntryPoint
class AutoTextActivity : BaseActivity<ActivityAutotextBinding>() {

    private val viewModel: AutoTextViewModel by viewModels()

    override fun setupViewBinding(): ActivityAutotextBinding {
        return ActivityAutotextBinding.inflate(layoutInflater)
    }

    override fun setupViewModel() {
        viewModel.apply {
            autoText.observe(this@AutoTextActivity) {
                setupRvAutoText(it)
            }
        }
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        setupDetailActivity("Auto Text")
        viewModel.getAutoText()
    }

    private fun setupRvAutoText(data: List<AutoTextEntity>) {
        binding.rvAutotext.injectorBinding<AutoTextEntity, ItemAutotextBinding>()
            .addData(data)
            .addCallback(object : IFrogoBindingAdapter<AutoTextEntity, ItemAutotextBinding> {

                override fun setViewBinding(parent: ViewGroup): ItemAutotextBinding {
                    return ItemAutotextBinding.inflate(layoutInflater, parent, false)
                }

                override fun setupInitComponent(
                    binding: ItemAutotextBinding,
                    data: AutoTextEntity,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<AutoTextEntity>,
                ) {
                    binding.tvAutotextTitle.text = data.title
                    binding.tvAutotextContent.text = data.body
                }

                override fun onItemClicked(
                    binding: ItemAutotextBinding,
                    data: AutoTextEntity,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<AutoTextEntity>,
                ) {}

                override fun onItemLongClicked(
                    binding: ItemAutotextBinding,
                    data: AutoTextEntity,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<AutoTextEntity>,
                ) {}

            })
            .createLayoutLinearVertical(false)
            .build()
    }
    
}