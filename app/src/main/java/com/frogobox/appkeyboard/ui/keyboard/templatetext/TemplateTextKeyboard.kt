package com.frogobox.appkeyboard.ui.keyboard.templatetext

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.frogobox.appkeyboard.databinding.ItemKeyboardNewsBinding
import com.frogobox.appkeyboard.databinding.KeyboardAutotextBinding
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.model.KeyboardFeatureType.TEMPLATE_TEXT_APP
import com.frogobox.appkeyboard.model.KeyboardFeatureType.TEMPLATE_TEXT_GAME
import com.frogobox.appkeyboard.model.KeyboardFeatureType.TEMPLATE_TEXT_GREETING
import com.frogobox.appkeyboard.model.KeyboardFeatureType.TEMPLATE_TEXT_LOVE
import com.frogobox.appkeyboard.model.KeyboardFeatureType.TEMPLATE_TEXT_SALE
import com.frogobox.appkeyboard.model.TemplateText
import com.frogobox.libkeyboard.common.core.BaseKeyboard
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding

class TemplateTextKeyboard(
    context: Context,
    attrs: AttributeSet?,
) : BaseKeyboard<KeyboardAutotextBinding>(context, attrs) {

    private var typePlayStore: KeyboardFeatureType? = null

    override fun setupViewBinding(inflater: LayoutInflater, parent: LinearLayout): KeyboardAutotextBinding {
        return KeyboardAutotextBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun onCreate() {
        initView()
    }

    private fun setupContent(templateTextType: KeyboardFeatureType) {
        val title: String
        val list: List<TemplateText>

        when (templateTextType) {
            TEMPLATE_TEXT_GAME -> {
                title = getTitleText(TEMPLATE_TEXT_GAME.name)
                list = TemplateTextUtils.getTextGame(context)
            }

            TEMPLATE_TEXT_APP -> {
                title = getTitleText(TEMPLATE_TEXT_APP.name)
                list = TemplateTextUtils.getTextApp(context)
            }

            TEMPLATE_TEXT_SALE -> {
                title = getTitleText(TEMPLATE_TEXT_SALE.name)
                list = TemplateTextUtils.getTextSale(context)
            }

            TEMPLATE_TEXT_GREETING -> {
                title = getTitleText(TEMPLATE_TEXT_GREETING.name)
                list = TemplateTextUtils.getTextGreeting(context)
            }

            TEMPLATE_TEXT_LOVE -> {
                title = getTitleText(TEMPLATE_TEXT_LOVE.name)
                list = TemplateTextUtils.getTextLove(context)
            }

            else -> {
                title = ""
                list = listOf()
            }

        }

        binding.tvToolbarTitle.text = title
        setupRv(list)
    }

    private fun initView() {
        typePlayStore?.let { setupContent(it) }
    }

    fun setupTemplateTextType(templateTextType: KeyboardFeatureType) {
        this.typePlayStore = templateTextType
        setupContent(templateTextType)
    }

    private fun getTitleText(title: String): String {
        return title.replace("TEMPLATE_TEXT_", "")
    }

    private fun setupRv(data: List<TemplateText>) {
        binding.apply {

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

                override fun areContentsTheSame(
                    oldItem: TemplateText,
                    newItem: TemplateText
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areItemsTheSame(
                    oldItem: TemplateText,
                    newItem: TemplateText
                ): Boolean {
                    return oldItem.id == newItem.id
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