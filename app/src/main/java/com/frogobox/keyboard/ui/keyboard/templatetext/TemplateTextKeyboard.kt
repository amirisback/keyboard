package com.frogobox.keyboard.ui.keyboard.templatetext

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import com.frogobox.keyboard.common.base.BaseKeyboard
import com.frogobox.keyboard.databinding.ItemKeyboardNewsBinding
import com.frogobox.keyboard.databinding.KeyboardAutotextBinding
import com.frogobox.keyboard.model.TemplateText
import com.frogobox.keyboard.model.TemplateTextType
import com.frogobox.keyboard.model.TemplateTextType.*
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
                LOVE -> LOVE.name
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
                LOVE -> LOVE.name
            }
        }
        setupRv(
            when (templateTextType) {
                GAME -> TemplateTextUtils.getTextGame(context)
                APP -> TemplateTextUtils.getTextApp(context)
                SALE -> TemplateTextUtils.getTextSale(context)
                LOVE -> TemplateTextUtils.getTextLove(context)
            }
        )
    }

    private fun setupData() {
        setupRv(
            when (typePlayStore) {
                GAME -> TemplateTextUtils.getTextGame(context)
                APP -> TemplateTextUtils.getTextApp(context)
                SALE -> TemplateTextUtils.getTextSale(context)
                LOVE -> TemplateTextUtils.getTextLove(context)
                else -> {
                    TemplateTextUtils.getTextGame(context)}
            }
        )
    }

    private fun setupRv(data: List<TemplateText>) {
        binding?.apply {

            val adapterCallback = object :
                IFrogoBindingAdapter<TemplateText, ItemKeyboardNewsBinding> {
                override fun onItemClicked(
                    binding: ItemKeyboardNewsBinding,
                    data: TemplateText,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<TemplateText>,
                ) {
                    // Your Clicked
                    val output = data.text
                    currentInputConnection?.commitText(output, 1)
                }

                override fun onItemLongClicked(
                    binding: ItemKeyboardNewsBinding,
                    data: TemplateText,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<TemplateText>,
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
                    data: TemplateText,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<TemplateText>,
                ) {
                    binding.apply {
                        tvItemKeyboardMain.text = data.text
                    }
                }
            }

            rvKeyboardMain.injectorBinding<TemplateText, ItemKeyboardNewsBinding>()
                .addData(data)
                .createLayoutLinearVertical(false)
                .addCallback(adapterCallback)
                .build()
        }
    }

}