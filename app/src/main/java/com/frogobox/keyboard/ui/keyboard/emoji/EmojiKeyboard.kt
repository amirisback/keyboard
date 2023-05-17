package com.frogobox.keyboard.ui.keyboard.emoji

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
import com.frogobox.keyboard.R
import com.frogobox.keyboard.common.base.BaseKeyboard
import com.frogobox.keyboard.databinding.ItemKeyboardEmojiBinding
import com.frogobox.keyboard.databinding.KeyboardEmojiBinding
import com.frogobox.keyboard.ui.keyboard.main.OnKeyboardActionListener
import com.frogobox.keyboard.util.AutoGridLayoutManager
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
) : BaseKeyboard<KeyboardEmojiBinding>(context, attrs),
    IFrogoBindingAdapter<EmojiCategory, ItemKeyboardEmojiBinding> {

    private var emojiCompatMetadataVersion = 0

    var mOnKeyboardActionListener: OnKeyboardActionListener? = null

    override fun setupViewBinding(): KeyboardEmojiBinding {
        return KeyboardEmojiBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun onCreate() {
    }

    @RequiresApi(Build.VERSION_CODES.M)
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
        return ItemKeyboardEmojiBinding.inflate(LayoutInflater.from(context), parent, false)
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

    @RequiresApi(Build.VERSION_CODES.M)
    fun openEmojiPalette() {
        setupEmojiCategory()
    }

    private fun setupEmojiCategory() {
        binding?.apply {
            emojiCategoryList.injectorBinding<EmojiCategory, ItemKeyboardEmojiBinding>()
                .addData(getEmojiCategory())
                .addCallback(this@EmojiKeyboard)
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
                systemFontPaint.hasGlyph(emoji) || (EmojiCompat.get().loadState == EmojiCompat.LOAD_STATE_SUCCEEDED && EmojiCompat.get()
                    .getEmojiMatch(
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
        binding?.emojiList?.apply {
            val emojiItemWidth = context.resources.getDimensionPixelSize(R.dimen.emoji_item_size)

            layoutManager = AutoGridLayoutManager(context, emojiItemWidth)
            adapter = EmojiAdapter {
                mOnKeyboardActionListener!!.onText(it)
            }.apply {
                addData(emojis)
            }

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