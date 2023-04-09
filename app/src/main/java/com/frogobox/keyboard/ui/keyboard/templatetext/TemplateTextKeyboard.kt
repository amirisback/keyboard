package com.frogobox.keyboard.ui.keyboard.templatetext

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import com.frogobox.keyboard.common.base.BaseKeyboard
import com.frogobox.keyboard.databinding.ItemKeyboardNewsBinding
import com.frogobox.keyboard.databinding.KeyboardAutotextBinding
import com.frogobox.keyboard.ui.keyboard.templatetext.TemplateTextType.*
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding

class TemplateTextKeyboard(
    context: Context,
    attrs: AttributeSet?,
) : BaseKeyboard<KeyboardAutotextBinding>(context, attrs) {

    var typePlayStore : TemplateTextType? = null

    override fun setupViewBinding(): KeyboardAutotextBinding {
        return KeyboardAutotextBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun onCreate() {
        initView()
        setupData()
    }

    private fun initView() {
        binding?.apply {
            tvToolbarTitle.text = when (typePlayStore) {
                GAME -> GAME.name
                APP -> APP.name
                SALE -> SALE.name
                else -> {
                    GAME.name
                }
            }
        }
    }

    fun setupTemplateTextType(templateTextType: TemplateTextType) {
        this.typePlayStore = templateTextType
        binding?.apply {
            tvToolbarTitle.text = when (templateTextType) {
                GAME -> GAME.name
                APP -> APP.name
                SALE -> SALE.name
            }
        }
        setupRv(
            when (templateTextType) {
                GAME -> TemplateTextUtils.getTextGame()
                APP -> TemplateTextUtils.getTextApp()
                SALE -> TemplateTextUtils.getTextSales()
            }
        )
    }

    private fun setupData() {
        setupRv(
            when (typePlayStore) {
                GAME -> TemplateTextUtils.getTextGame()
                APP -> TemplateTextUtils.getTextApp()
                else -> {
                    TemplateTextUtils.getTextGame()}
            }
        )
    }

    private fun setupRv(data: List<TemplateTextModel>) {
        binding?.apply {

            val adapterCallback = object :
                IFrogoBindingAdapter<TemplateTextModel, ItemKeyboardNewsBinding> {
                override fun onItemClicked(
                    binding: ItemKeyboardNewsBinding,
                    data: TemplateTextModel,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<TemplateTextModel>,
                ) {
                    // Your Clicked
                    val output = data.text
                    currentInputConnection?.commitText(output, 1)
                }

                override fun onItemLongClicked(
                    binding: ItemKeyboardNewsBinding,
                    data: TemplateTextModel,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<TemplateTextModel>,
                ) {
                }

                override fun setViewBinding(parent: ViewGroup): ItemKeyboardNewsBinding {
                    return ItemKeyboardNewsBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                    )
                }

                override fun setupInitComponent(
                    binding: ItemKeyboardNewsBinding,
                    data: TemplateTextModel,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<TemplateTextModel>,
                ) {
                    binding.apply {
                        tvItemKeyboardMain.text = data.text
                    }
                }
            }

            rvKeyboardMain.injectorBinding<TemplateTextModel, ItemKeyboardNewsBinding>()
                .addData(data)
                .createLayoutLinearVertical(false)
                .addCallback(adapterCallback)
                .build()
        }
    }

}