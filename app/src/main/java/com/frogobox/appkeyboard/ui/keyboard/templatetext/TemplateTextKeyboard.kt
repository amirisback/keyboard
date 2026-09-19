package com.frogobox.appkeyboard.ui.keyboard.templatetext

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.frogobox.appkeyboard.R
import com.frogobox.appkeyboard.databinding.ItemKeyboardTemplateCategoryBinding
import com.frogobox.appkeyboard.databinding.ItemKeyboardTemplateTextBinding
import com.frogobox.appkeyboard.databinding.KeyboardTemplateTextBinding
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

data class TemplateCategoryItem(
    val type: KeyboardFeatureType,
    val icon: String,
    val title: String,
    val isSelected: Boolean
)

class TemplateTextKeyboard(
    context: Context,
    attrs: AttributeSet?,
) : BaseKeyboard<KeyboardTemplateTextBinding>(context, attrs) {

    companion object {
        val CATEGORIES = listOf(
            Pair(TEMPLATE_TEXT_GAME, Pair("🎮", "Game")),
            Pair(TEMPLATE_TEXT_APP, Pair("📱", "App")),
            Pair(TEMPLATE_TEXT_SALE, Pair("💰", "Sale")),
            Pair(TEMPLATE_TEXT_GREETING, Pair("👋", "Greeting")),
            Pair(TEMPLATE_TEXT_LOVE, Pair("❤️", "Love"))
        )
    }

    private var currentType: KeyboardFeatureType? = null

    override fun setupViewBinding(inflater: LayoutInflater, parent: LinearLayout): KeyboardTemplateTextBinding {
        return KeyboardTemplateTextBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun initUI() {
        super.initUI()
        val type = currentType ?: TEMPLATE_TEXT_GAME
        currentType = type
        setupCategories(type)
        setupContent(type)
    }

    fun setupTemplateTextType(templateTextType: KeyboardFeatureType) {
        this.currentType = templateTextType
        setupCategories(templateTextType)
        setupContent(templateTextType)
    }

    private fun setupCategories(selectedType: KeyboardFeatureType) {
        val categoryData = CATEGORIES.map { (type, meta) ->
            TemplateCategoryItem(
                type = type,
                icon = meta.first,
                title = meta.second,
                isSelected = type == selectedType
            )
        }

        val callback = object : IFrogoBindingAdapter<TemplateCategoryItem, ItemKeyboardTemplateCategoryBinding> {
            override fun areContentsTheSame(oldItem: TemplateCategoryItem, newItem: TemplateCategoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: TemplateCategoryItem, newItem: TemplateCategoryItem): Boolean {
                return oldItem.type == newItem.type
            }

            override fun setViewBinding(parent: ViewGroup): ItemKeyboardTemplateCategoryBinding {
                return ItemKeyboardTemplateCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
            }

            override fun setupInitComponent(
                binding: ItemKeyboardTemplateCategoryBinding,
                data: TemplateCategoryItem,
                position: Int,
                notifyListener: FrogoRecyclerNotifyListener<TemplateCategoryItem>
            ) {
                binding.apply {
                    tvCategoryIcon.text = data.icon
                    tvCategoryName.text = data.title

                    if (data.isSelected) {
                        llCategoryContainer.setBackgroundResource(R.drawable.bg_feature_chip_active)
                        tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.color_feature_chip_active_text))
                    } else {
                        llCategoryContainer.setBackgroundResource(R.drawable.bg_feature_chip_inactive)
                        tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.color_feature_chip_text))
                    }
                }
            }

            override fun onItemClicked(
                binding: ItemKeyboardTemplateCategoryBinding,
                data: TemplateCategoryItem,
                position: Int,
                notifyListener: FrogoRecyclerNotifyListener<TemplateCategoryItem>
            ) {
                if (currentType != data.type) {
                    currentType = data.type
                    setupCategories(data.type)
                    setupContent(data.type)
                }
            }
        }

        binding.rvTemplateCategories.injectorBinding<TemplateCategoryItem, ItemKeyboardTemplateCategoryBinding>()
            .addData(categoryData)
            .createLayoutLinearHorizontal(false)
            .addCallback(callback)
            .build()
    }

    private fun setupContent(templateTextType: KeyboardFeatureType) {
        val title: String
        val list: List<TemplateText>

        when (templateTextType) {
            TEMPLATE_TEXT_GAME -> {
                title = "Game Templates"
                list = TemplateTextUtils.getTextGame(context)
            }

            TEMPLATE_TEXT_APP -> {
                title = "App Templates"
                list = TemplateTextUtils.getTextApp(context)
            }

            TEMPLATE_TEXT_SALE -> {
                title = "Sale / Store Templates"
                list = TemplateTextUtils.getTextSale(context)
            }

            TEMPLATE_TEXT_GREETING -> {
                title = "Greeting Templates"
                list = TemplateTextUtils.getTextGreeting(context)
            }

            TEMPLATE_TEXT_LOVE -> {
                title = "Love & Sweet Templates"
                list = TemplateTextUtils.getTextLove(context)
            }

            else -> {
                title = "Quick Templates"
                list = listOf()
            }
        }

        binding.tvToolbarTitle.text = title
        setupRv(list)
    }

    private fun setupRv(data: List<TemplateText>) {
        binding.apply {
            val adapterCallback = object :
                IFrogoBindingAdapter<TemplateText, ItemKeyboardTemplateTextBinding> {
                override fun onItemClicked(
                    binding: ItemKeyboardTemplateTextBinding,
                    data: TemplateText,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<TemplateText>,
                ) {
                    currentInputConnection?.commitText(data.text, 1)
                }

                override fun onItemLongClicked(
                    binding: ItemKeyboardTemplateTextBinding,
                    data: TemplateText,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<TemplateText>,
                ) {}

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

                override fun setViewBinding(parent: ViewGroup): ItemKeyboardTemplateTextBinding {
                    return ItemKeyboardTemplateTextBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                    )
                }

                override fun setupInitComponent(
                    binding: ItemKeyboardTemplateTextBinding,
                    data: TemplateText,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<TemplateText>,
                ) {
                    binding.tvItemTemplateText.text = data.text
                }
            }

            rvKeyboardMain.injectorBinding<TemplateText, ItemKeyboardTemplateTextBinding>()
                .addData(data)
                .createLayoutLinearVertical(false)
                .addCallback(adapterCallback)
                .build()
        }
    }

}