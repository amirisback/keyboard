package com.frogobox.appkeyboard.ui.keyboard.templatetext

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import com.frogobox.appkeyboard.databinding.ItemKeyboardNewsBinding
import com.frogobox.appkeyboard.databinding.KeyboardAutotextBinding
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.model.KeyboardFeatureType.AUTO_TEXT
import com.frogobox.appkeyboard.model.KeyboardFeatureType.FORM
import com.frogobox.appkeyboard.model.KeyboardFeatureType.MOVIE
import com.frogobox.appkeyboard.model.KeyboardFeatureType.NEWS
import com.frogobox.appkeyboard.model.KeyboardFeatureType.SETTING
import com.frogobox.appkeyboard.model.KeyboardFeatureType.TEMPLATE_TEXT_APP
import com.frogobox.appkeyboard.model.KeyboardFeatureType.TEMPLATE_TEXT_GAME
import com.frogobox.appkeyboard.model.KeyboardFeatureType.TEMPLATE_TEXT_GREETING
import com.frogobox.appkeyboard.model.KeyboardFeatureType.TEMPLATE_TEXT_LOVE
import com.frogobox.appkeyboard.model.KeyboardFeatureType.TEMPLATE_TEXT_SALE
import com.frogobox.appkeyboard.model.KeyboardFeatureType.WEB
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

    override fun setupViewBinding(): KeyboardAutotextBinding {
        return KeyboardAutotextBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun onCreate() {
        initView()
    }

    private fun setupTitleContent(templateTextType: KeyboardFeatureType) {
        binding?.apply {
            tvToolbarTitle.text = when (templateTextType) {
                NEWS -> ""
                MOVIE -> ""
                WEB -> ""
                FORM -> ""
                SETTING -> ""
                KeyboardFeatureType.CHANGE_KEYBOARD -> ""
                AUTO_TEXT -> ""
                TEMPLATE_TEXT_GAME -> getTitleText(TEMPLATE_TEXT_GAME.name)
                TEMPLATE_TEXT_APP -> getTitleText(TEMPLATE_TEXT_APP.name)
                TEMPLATE_TEXT_SALE -> getTitleText(TEMPLATE_TEXT_SALE.name)
                TEMPLATE_TEXT_GREETING -> getTitleText(TEMPLATE_TEXT_GREETING.name)
                TEMPLATE_TEXT_LOVE -> getTitleText(TEMPLATE_TEXT_LOVE.name)
            }
        }
    }

    private fun setupRvContent(templateTextType: KeyboardFeatureType) {
        setupRv(
            when (templateTextType) {
                NEWS -> listOf()
                MOVIE -> listOf()
                WEB -> listOf()
                FORM -> listOf()
                AUTO_TEXT -> listOf()
                SETTING -> listOf()
                KeyboardFeatureType.CHANGE_KEYBOARD -> listOf()
                TEMPLATE_TEXT_GAME -> TemplateTextUtils.getTextGame(context)
                TEMPLATE_TEXT_APP -> TemplateTextUtils.getTextApp(context)
                TEMPLATE_TEXT_SALE -> TemplateTextUtils.getTextSale(context)
                TEMPLATE_TEXT_GREETING -> TemplateTextUtils.getTextGreeting(context)
                TEMPLATE_TEXT_LOVE -> TemplateTextUtils.getTextLove(context)
            }
        )
    }

    private fun initView() {
        typePlayStore?.let { setupTitleContent(it) }
        typePlayStore?.let { setupRvContent(it) }
    }

    fun setupTemplateTextType(templateTextType: KeyboardFeatureType) {
        this.typePlayStore = templateTextType
        setupTitleContent(templateTextType)
        setupRvContent(templateTextType)
    }

    private fun getTitleText(title: String): String {
        return title.replace("TEMPLATE_TEXT_", "")
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