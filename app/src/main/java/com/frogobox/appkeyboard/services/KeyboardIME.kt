package com.frogobox.appkeyboard.services

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import com.frogobox.appkeyboard.common.ext.getKeyboardType
import com.frogobox.appkeyboard.databinding.ItemKeyboardHeaderBinding
import com.frogobox.appkeyboard.databinding.KeyboardImeBinding
import com.frogobox.appkeyboard.model.KeyboardFeature
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.ui.main.MainActivity
import com.frogobox.libkeyboard.common.core.BaseKeyboardIME
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding
import com.frogobox.sdk.ext.gone
import com.frogobox.sdk.ext.invisible
import com.frogobox.sdk.ext.visible


class KeyboardIME : BaseKeyboardIME<KeyboardImeBinding>() {

    override fun setupViewBinding(): KeyboardImeBinding {
        return KeyboardImeBinding.inflate(LayoutInflater.from(this), null, false)
    }

    override fun initialSetupKeyboard() {
        binding?.keyboardMain?.setKeyboard(keyboard!!)
        binding?.mockMeasureHeightKeyboardMain?.setKeyboard(keyboard!!)
    }

    override fun setupBinding() {
        initialSetupKeyboard()
        binding?.keyboardMain?.mOnKeyboardActionListener = this
        binding?.keyboardEmoji?.mOnKeyboardActionListener = this
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
            keyboardMain.gone()
            keyboardHeader.gone()
            mockMeasureHeightKeyboard.invisible()
        }
    }

    override fun showMainKeyboard() {
        binding?.apply {
            keyboardMain.visible()
            mockMeasureHeightKeyboard.gone()
            if (KeyboardUtil().menuKeyboard().isEmpty()) {
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
            keyboardEmoji.binding?.emojiList?.scrollToPosition(0)
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
            keyboardAutotext.binding?.toolbarBack?.setOnClickListener {
                keyboardAutotext.gone()
                showMainKeyboard()
            }

            keyboardNews.binding?.toolbarBack?.setOnClickListener {
                keyboardNews.gone()
                showMainKeyboard()
            }

            keyboardMoview.binding?.toolbarBack?.setOnClickListener {
                keyboardMoview.gone()
                showMainKeyboard()
            }

            keyboardWebview.binding?.toolbarBack?.setOnClickListener {
                keyboardWebview.gone()
                showMainKeyboard()
            }

            keyboardForm.binding?.toolbarBack?.setOnClickListener {
                keyboardForm.gone()
                showMainKeyboard()
            }

            keyboardEmoji.binding?.toolbarBack?.setOnClickListener {
                keyboardEmoji.gone()
                keyboardEmoji.binding?.emojiList?.scrollToPosition(0)
                showMainKeyboard()
            }

            keyboardTemplateText.binding?.toolbarBack?.setOnClickListener {
                keyboardTemplateText.gone()
                showMainKeyboard()
            }

        }
    }

    override fun setupFeatureKeyboard() {
        val maxMenu = 4
        val gridSize = if (KeyboardUtil().menuKeyboard().size <= maxMenu) {
            KeyboardUtil().menuKeyboard().size
        } else if (KeyboardUtil().menuKeyboard().size.mod(maxMenu) == 0) {
            maxMenu
        } else {
            maxMenu + 1
        }

        binding?.apply {
            if (KeyboardUtil().menuKeyboard().isEmpty()) {
                keyboardHeader.gone()
                mockKeyboardHeader.gone()
            } else {
                keyboardHeader.visible()
                mockKeyboardHeader.visible()
                keyboardHeader.injectorBinding<KeyboardFeature, ItemKeyboardHeaderBinding>()
                    .addData(KeyboardUtil().menuKeyboard())
                    .addCallback(object :
                        IFrogoBindingAdapter<KeyboardFeature, ItemKeyboardHeaderBinding> {

                        override fun setViewBinding(parent: ViewGroup): ItemKeyboardHeaderBinding {
                            return ItemKeyboardHeaderBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            )
                        }

                        override fun setupInitComponent(
                            binding: ItemKeyboardHeaderBinding,
                            data: KeyboardFeature,
                            position: Int,
                            notifyListener: FrogoRecyclerNotifyListener<KeyboardFeature>,
                        ) {
                            binding.ivIcon.setImageResource(data.icon)
                            binding.tvTitle.text = data.type.title

                            if (data.state) {
                                binding.root.visible()
                            } else {
                                binding.root.gone()
                            }

                        }

                        override fun onItemClicked(
                            binding: ItemKeyboardHeaderBinding,
                            data: KeyboardFeature,
                            position: Int,
                            notifyListener: FrogoRecyclerNotifyListener<KeyboardFeature>,
                        ) {

                            when (data.type) {
                                KeyboardFeatureType.NEWS -> {
                                    hideMainKeyboard()
                                    keyboardNews.visible()
                                }

                                KeyboardFeatureType.MOVIE -> {
                                    hideMainKeyboard()
                                    keyboardMoview.visible()
                                }

                                KeyboardFeatureType.WEB -> {
                                    mockMeasureHeightKeyboard.invisible()
                                    keyboardHeader.gone()
                                    keyboardWebview.visible()
                                }

                                KeyboardFeatureType.FORM -> {
                                    hideMainKeyboard()

                                    keyboardForm.visible()
                                    keyboardForm.binding?.etText?.showKeyboardExt()
                                    keyboardForm.binding?.etText2?.showKeyboardExt()
                                    keyboardForm.binding?.etText3?.showKeyboardExt()

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
                                    binding.root.context.startActivity(
                                        Intent(binding.root.context, MainActivity::class.java).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    })
                                }

                            }

                        }

                        override fun onItemLongClicked(
                            binding: ItemKeyboardHeaderBinding,
                            data: KeyboardFeature,
                            position: Int,
                            notifyListener: FrogoRecyclerNotifyListener<KeyboardFeature>,
                        ) {
                        }


                    })
                    .createLayoutGrid(gridSize)
                    .build()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onKey(code: Int) {
        val formView = binding?.keyboardForm
        var inputConnection = currentInputConnection

        if (formView?.visibility == View.VISIBLE) {
            val et1 = formView.binding?.etText
            val et1Connection = et1?.onCreateInputConnection(EditorInfo())

            val et2 = formView.binding?.etText2
            val et2Connection = et2?.onCreateInputConnection(EditorInfo())

            val et3 = formView.binding?.etText3
            val et3Connection = et3?.onCreateInputConnection(EditorInfo())

            if (et1?.isFocused == true) {
                inputConnection = et1Connection
            } else if (et2?.isFocused == true) {
                inputConnection = et2Connection
            } else if (et3?.isFocused == true) {
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun runEmojiBoard() {
        binding?.keyboardEmoji?.visible()
        hideMainKeyboard()
        binding?.keyboardEmoji?.openEmojiPalette()
    }

    override fun getKeyboardLayoutXML(): Int {
        return baseContext.getKeyboardType()
    }

}