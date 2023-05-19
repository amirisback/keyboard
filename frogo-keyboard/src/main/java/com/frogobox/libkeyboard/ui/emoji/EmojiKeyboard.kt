package com.frogobox.libkeyboard.ui.emoji

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.emoji2.text.EmojiCompat
import com.frogobox.libkeyboard.R
import com.frogobox.libkeyboard.common.core.BaseKeyboard
import com.frogobox.libkeyboard.databinding.ItemKeyboardEmojiBinding
import com.frogobox.libkeyboard.databinding.KeyboardEmojiBinding
import com.frogobox.libkeyboard.ui.main.OnKeyboardActionListener
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding

/**
 * Created by Faisal Amir on 11/12/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

class EmojiKeyboard(
    context: Context,
    attrs: AttributeSet?,
) : BaseKeyboard<KeyboardEmojiBinding>(context, attrs) {

    private var emojiCompatMetadataVersion = 0

    var mOnKeyboardActionListener: OnKeyboardActionListener? = null

    override fun setupViewBinding(): KeyboardEmojiBinding {
        return KeyboardEmojiBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun onCreate() {
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun openEmojiPalette() {
        setupEmojis(EmojiCategoryType.GENERAL.path)
        setupEmojiCategory()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupEmojiCategory() {
        val adapterCallback =
            object : IFrogoBindingAdapter<EmojiCategory, ItemKeyboardEmojiBinding> {

                override fun onItemClicked(
                    binding: ItemKeyboardEmojiBinding,
                    data: EmojiCategory,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<EmojiCategory>
                ) {
                    setupEmojis(data.path)
                }

                override fun onItemLongClicked(
                    binding: ItemKeyboardEmojiBinding,
                    data: EmojiCategory,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<EmojiCategory>
                ) {
                }

                override fun setViewBinding(parent: ViewGroup): ItemKeyboardEmojiBinding {
                    return ItemKeyboardEmojiBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                    )
                }

                override fun setupInitComponent(
                    binding: ItemKeyboardEmojiBinding,
                    data: EmojiCategory,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<EmojiCategory>
                ) {
                    val processed = EmojiCompat.get().process(data.icon)
                    binding.tvEmoji.text = processed
                }
            }

        binding?.apply {
            emojiCategoryList.injectorBinding<EmojiCategory, ItemKeyboardEmojiBinding>()
                .addData(getEmojiCategory())
                .addCallback(adapterCallback)
                .createLayoutLinearHorizontal(false)
                .build()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupEmojis(path: String) {
        ensureBackgroundThread {
            val fullEmojiList = parseRawEmojiSpecsFile(context, path)
            val systemFontPaint = Paint().apply {
                typeface = Typeface.DEFAULT
            }

            val emojis = fullEmojiList.filter { emoji ->
                systemFontPaint.hasGlyph(emoji) || (EmojiCompat.get().loadState == EmojiCompat.LOAD_STATE_SUCCEEDED
                        && EmojiCompat.get().getEmojiMatch(
                    emoji,
                    emojiCompatMetadataVersion
                ) == EmojiCompat.EMOJI_SUPPORTED)
            }

            Handler(Looper.getMainLooper()).post {
                setupEmojiAdapter(emojis)
            }

        }
    }

    private fun setupEmojiAdapter(emojis: List<String>) {

        val adapterCallback = object : IFrogoBindingAdapter<String, ItemKeyboardEmojiBinding> {
            override fun onItemClicked(
                binding: ItemKeyboardEmojiBinding,
                data: String,
                position: Int,
                notifyListener: FrogoRecyclerNotifyListener<String>
            ) {
                mOnKeyboardActionListener!!.onText(data)
            }

            override fun onItemLongClicked(
                binding: ItemKeyboardEmojiBinding,
                data: String,
                position: Int,
                notifyListener: FrogoRecyclerNotifyListener<String>
            ) {
                mOnKeyboardActionListener!!.onText(data)
            }

            override fun setViewBinding(parent: ViewGroup): ItemKeyboardEmojiBinding {
                return ItemKeyboardEmojiBinding.inflate(LayoutInflater.from(context), parent, false)
            }

            override fun setupInitComponent(
                binding: ItemKeyboardEmojiBinding,
                data: String,
                position: Int,
                notifyListener: FrogoRecyclerNotifyListener<String>
            ) {
                val processed = EmojiCompat.get().process(data)
                binding.tvEmoji.text = processed
            }
        }

        val emojiItemWidth = context.resources.getDimensionPixelSize(R.dimen.emoji_item_size)
        val mLayoutManager = AutoGridLayoutManager(context, emojiItemWidth)

        binding?.apply {
            emojiList.injectorBinding<String, ItemKeyboardEmojiBinding>()
                .addCallback(adapterCallback)
                .addData(emojis)
                .build()

            emojiList.layoutManager = mLayoutManager
        }
    }

    private fun ensureBackgroundThread(callback: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Thread {
                callback()
            }.start()
        } else {
            callback()
        }
    }

}