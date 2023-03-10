package com.frogobox.keyboard.ui.keyboard.autotext

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import com.frogobox.keyboard.common.base.BaseKeyboard
import com.frogobox.keyboard.data.local.autotext.AutoTextEntity
import com.frogobox.keyboard.databinding.ItemKeyboardNewsBinding
import com.frogobox.keyboard.databinding.KeyboardAutotextBinding
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */

class AutoTextKeyboard(
    context: Context,
    attrs: AttributeSet?,
) : BaseKeyboard<KeyboardAutotextBinding>(context, attrs) {

    override fun setupViewBinding(): KeyboardAutotextBinding {
        return KeyboardAutotextBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun onCreate() {
        initView()
        setupData()
    }

    private fun initView() {
        binding?.apply {
            tvToolbarTitle.text = "Auto Text"
        }
    }

    private fun setupData() {
        setupRv(AutoTextKeyboardViewModel().getAutoText())
    }

    private fun setupRv(data: List<AutoTextEntity>) {
        binding?.apply {

            val adapterCallback = object : IFrogoBindingAdapter<AutoTextEntity, ItemKeyboardNewsBinding> {
                override fun onItemClicked(
                    binding: ItemKeyboardNewsBinding,
                    data: AutoTextEntity,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<AutoTextEntity>,
                ) {
                    // Your Clicked
                    val output = "${data.title} ${data.body}"
                    currentInputConnection?.commitText(output, 1)
                }

                override fun onItemLongClicked(
                    binding: ItemKeyboardNewsBinding,
                    data: AutoTextEntity,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<AutoTextEntity>,
                ) {
                }

                override fun setViewBinding(parent: ViewGroup): ItemKeyboardNewsBinding {
                    return ItemKeyboardNewsBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false)
                }

                override fun setupInitComponent(
                    binding: ItemKeyboardNewsBinding,
                    data: AutoTextEntity,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<AutoTextEntity>,
                ) {
                    binding.apply {
                        tvItemKeyboardMain.text = data.title
                    }
                }
            }

            rvKeyboardMain.injectorBinding<AutoTextEntity, ItemKeyboardNewsBinding>()
                .addData(data)
                .createLayoutLinearVertical(false)
                .addCallback(adapterCallback)
                .build()
        }
    }

}