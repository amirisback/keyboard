package com.frogobox.appkeyboard.services

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.frogobox.appkeyboard.R
import com.frogobox.appkeyboard.databinding.ItemKeyboardHeaderBinding
import com.frogobox.appkeyboard.databinding.KeyboardImeBinding
import com.frogobox.appkeyboard.model.KeyboardFeatureModel
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.model.ThemeType
import com.frogobox.appkeyboard.ui.main.MainActivity
import com.frogobox.libkeyboard.common.core.BaseKeyboardIME
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding
import com.frogobox.sdk.delegate.preference.PreferenceDelegates
import com.frogobox.sdk.ext.getColorExt
import com.frogobox.sdk.ext.gone
import com.frogobox.sdk.ext.invisible
import com.frogobox.sdk.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class KeyboardIME : BaseKeyboardIME<KeyboardImeBinding>() {

    @Inject
    lateinit var pref: PreferenceDelegates

    @Inject
    lateinit var keyboardUtil: KeyboardUtil

    override fun setupViewBinding(): KeyboardImeBinding {
        return KeyboardImeBinding.inflate(LayoutInflater.from(this), null, false)
    }

    override fun setupTheme() {
        binding?.apply {

            val background = pref.getPrefInt(
                KeyboardUtil.KEYBOARD_COLOR,
                R.color.color_bg_keyboard_default
            )

            val backgroundType = ThemeType.valueOf(
                pref.getPrefString(
                    KeyboardUtil.KEYBOARD_COLOR_TYPE,
                    ThemeType.COLOR.name
                )
            )

            when (backgroundType) {
                ThemeType.COLOR -> {
                    ivBackgroundKeyboard.setBackgroundColor(getColorExt(background))
                }
                ThemeType.IMAGE -> {
                    ivBackgroundKeyboard.setImageResource(background)
                }
            }
        }
    }

    override fun initialSetupKeyboard() {
        binding?.keyboardMain?.setKeyboard(keyboard!!)
    }

    override fun setupBinding() {
        super.setupBinding()
        binding?.apply {
            keyboardMain.mOnKeyboardActionListener = this@KeyboardIME
            keyboardEmoji.mOnKeyboardActionListener = this@KeyboardIME
        }

    }

    override fun invalidateKeyboard() {
        binding?.keyboardAutotext?.setupData()
        setupFeatureKeyboard()
    }

    override fun initCurrentInputConnection() {
        binding?.apply {
            keyboardAutotext.setInputConnection(currentInputConnection)
            keyboardNews.setInputConnection(currentInputConnection)
            keyboardMoview.setInputConnection(currentInputConnection)
            keyboardWebview.setInputConnection(currentInputConnection)
            keyboardForm.setInputConnection(currentInputConnection)
            keyboardEmoji.setInputConnection(currentInputConnection)
            keyboardTemplateText.setInputConnection(currentInputConnection)
        }
    }

    override fun hideMainKeyboard() {
        binding?.apply {
            keyboardMain.invisible()
            keyboardHeader.invisible()
        }
    }

    override fun showMainKeyboard() {
        binding?.apply {
            keyboardMain.visible()
            if (keyboardUtil.menuKeyboard().isEmpty()) {
                keyboardHeader.gone()
            } else {
                keyboardHeader.visible()
            }
            keyboardAutotext.gone()
            keyboardNews.gone()
            keyboardMoview.gone()
            keyboardWebview.gone()
            keyboardForm.gone()
            keyboardEmoji.gone()
            keyboardEmoji.binding.emojiList.scrollToPosition(0)
        }
    }

    override fun showOnlyKeyboard() {
        binding?.keyboardMain?.visible()
    }

    override fun hideOnlyKeyboard() {
        binding?.keyboardMain?.gone()
    }

    override fun EditText.showKeyboardExt() {
        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showOnlyKeyboard()
            }
        }
        setOnClickListener {
            showOnlyKeyboard()
        }
    }

    override fun initBackToMainKeyboard() {
        binding?.apply {
            keyboardAutotext.binding.toolbarBack.setOnClickListener {
                keyboardAutotext.gone()
                showMainKeyboard()
            }

            keyboardNews.binding.toolbarBack.setOnClickListener {
                keyboardNews.gone()
                showMainKeyboard()
            }

            keyboardMoview.binding.toolbarBack.setOnClickListener {
                keyboardMoview.gone()
                showMainKeyboard()
            }

            keyboardWebview.binding.toolbarBack.setOnClickListener {
                keyboardWebview.gone()
                showMainKeyboard()
            }

            keyboardForm.binding.toolbarBack.setOnClickListener {
                keyboardForm.gone()
                showMainKeyboard()
            }

            keyboardEmoji.binding.toolbarBack.setOnClickListener {
                keyboardEmoji.gone()
                keyboardEmoji.binding.emojiList.scrollToPosition(0)
                showMainKeyboard()
            }

            keyboardTemplateText.binding.toolbarBack.setOnClickListener {
                keyboardTemplateText.gone()
                showMainKeyboard()
            }

        }
    }

    override fun setupFeatureKeyboard() {
        val maxMenu = 4
        val gridSize = if (keyboardUtil.menuKeyboard().size <= maxMenu) {
            keyboardUtil.menuKeyboard().size
        } else if (keyboardUtil.menuKeyboard().size.mod(maxMenu) == 0) {
            maxMenu
        } else {
            maxMenu + 1
        }

        binding?.apply {
            if (keyboardUtil.menuKeyboard().isEmpty()) {
                keyboardHeader.gone()
            } else {
                keyboardHeader.visible()
                keyboardHeader.injectorBinding<KeyboardFeatureModel, ItemKeyboardHeaderBinding>()
                    .addData(keyboardUtil.menuKeyboard()).addCallback(object :
                        IFrogoBindingAdapter<KeyboardFeatureModel, ItemKeyboardHeaderBinding> {

                            override fun areContentsTheSame(
                            oldItem: KeyboardFeatureModel,
                            newItem: KeyboardFeatureModel
                        ): Boolean {
                            return oldItem == newItem
                        }

                        override fun areItemsTheSame(
                            oldItem: KeyboardFeatureModel,
                            newItem: KeyboardFeatureModel
                        ): Boolean {
                            return oldItem.id == newItem.id
                        }

                        override fun setViewBinding(parent: ViewGroup): ItemKeyboardHeaderBinding {
                            return ItemKeyboardHeaderBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false
                            )
                        }

                        override fun setupInitComponent(
                            binding: ItemKeyboardHeaderBinding,
                            data: KeyboardFeatureModel,
                            position: Int,
                            notifyListener: FrogoRecyclerNotifyListener<KeyboardFeatureModel>,
                        ) {
                            binding.ivIcon.setImageResource(data.icon)
                            binding.tvTitle.text = data.text

                            if (getStateToggle(data.id)) {
                                binding.root.visible()
                            } else {
                                binding.root.gone()
                            }

                        }

                        override fun onItemClicked(
                            binding: ItemKeyboardHeaderBinding,
                            data: KeyboardFeatureModel,
                            position: Int,
                            notifyListener: FrogoRecyclerNotifyListener<KeyboardFeatureModel>,
                        ) {

                            when (KeyboardFeatureType.from(data.id)) {
                                KeyboardFeatureType.NEWS -> {
                                    hideMainKeyboard()
                                    keyboardNews.visible()
                                }

                                KeyboardFeatureType.MOVIE -> {
                                    hideMainKeyboard()
                                    keyboardMoview.visible()
                                }

                                KeyboardFeatureType.WEB -> {
                                    keyboardHeader.gone()
                                    keyboardWebview.visible()
                                }

                                KeyboardFeatureType.FORM -> {
                                    keyboardHeader.gone()
                                    keyboardForm.visible()
                                    keyboardForm.binding.etText.showKeyboardExt()
                                    keyboardForm.binding.etText2.showKeyboardExt()
                                    keyboardForm.binding.etText3.showKeyboardExt()

                                    keyboardForm.setOnClickListener {
                                        hideOnlyKeyboard()
                                    }
                                }

                                KeyboardFeatureType.AUTO_TEXT -> {
                                    hideMainKeyboard()
                                    keyboardAutotext.visible()
                                }

                                KeyboardFeatureType.TEMPLATE_TEXT_GAME -> {
                                    hideMainKeyboard()
                                    keyboardTemplateText.setupTemplateTextType(KeyboardFeatureType.TEMPLATE_TEXT_GAME)
                                    keyboardTemplateText.visible()
                                }

                                KeyboardFeatureType.TEMPLATE_TEXT_APP -> {
                                    hideMainKeyboard()
                                    keyboardTemplateText.setupTemplateTextType(KeyboardFeatureType.TEMPLATE_TEXT_APP)
                                    keyboardTemplateText.visible()
                                }

                                KeyboardFeatureType.TEMPLATE_TEXT_SALE -> {
                                    hideMainKeyboard()
                                    keyboardTemplateText.setupTemplateTextType(KeyboardFeatureType.TEMPLATE_TEXT_SALE)
                                    keyboardTemplateText.visible()
                                }

                                KeyboardFeatureType.TEMPLATE_TEXT_LOVE -> {
                                    hideMainKeyboard()
                                    keyboardTemplateText.setupTemplateTextType(KeyboardFeatureType.TEMPLATE_TEXT_LOVE)
                                    keyboardTemplateText.visible()
                                }

                                KeyboardFeatureType.TEMPLATE_TEXT_GREETING -> {
                                    hideMainKeyboard()
                                    keyboardTemplateText.setupTemplateTextType(KeyboardFeatureType.TEMPLATE_TEXT_GREETING)
                                    keyboardTemplateText.visible()
                                }

                                KeyboardFeatureType.CHANGE_KEYBOARD -> {
                                    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
                                }

                                KeyboardFeatureType.SETTING -> {
                                    binding.root.context.startActivity(Intent(
                                        binding.root.context, MainActivity::class.java
                                    ).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    })
                                }

                            }

                        }

                    }).createLayoutGrid(gridSize).build()
            }
        }
    }


    override fun onKey(code: Int) {
        val formView = binding?.keyboardForm
        var inputConnection = currentInputConnection

        if (formView?.visibility == View.VISIBLE) {
            val et1 = formView.binding.etText
            val et1Connection = et1.onCreateInputConnection(EditorInfo())

            val et2 = formView.binding.etText2
            val et2Connection = et2.onCreateInputConnection(EditorInfo())

            val et3 = formView.binding.etText3
            val et3Connection = et3.onCreateInputConnection(EditorInfo())

            if (et1.isFocused) {
                inputConnection = et1Connection
            } else if (et2.isFocused) {
                inputConnection = et2Connection
            } else if (et3.isFocused) {
                inputConnection = et3Connection
            }

        } else if (binding?.keyboardWebview?.visibility == View.VISIBLE) {
            inputConnection =
                binding?.keyboardWebview?.binding?.webview?.onCreateInputConnection(EditorInfo())
        } else {
            inputConnection = currentInputConnection
        }
        onKeyExt(code, inputConnection)
    }

    override fun initView() {
        setupFeatureKeyboard()
        initBackToMainKeyboard()
    }

    override fun invalidateAllKeys() {
        binding?.keyboardMain?.invalidateAllKeys()
    }

    
    override fun runEmojiBoard() {
        binding?.keyboardEmoji?.visible()
        binding?.keyboardMain?.invisible()
        binding?.keyboardHeader?.gone()
        binding?.keyboardEmoji?.openEmojiPalette()
    }

    override fun getKeyboardLayoutXML(): Int {
        return pref.getPrefInt(
            KeyboardUtil.KEYBOARD_TYPE, com.frogobox.libkeyboard.R.xml.keys_letters_qwerty
        )
    }

    private fun getStateToggle(key: String): Boolean {
        return pref.getPrefBoolean(key, true)
    }

}