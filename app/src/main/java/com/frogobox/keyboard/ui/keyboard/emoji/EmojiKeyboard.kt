package com.frogobox.keyboard.ui.keyboard.emoji

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.emoji2.text.EmojiCompat
import com.frogobox.keyboard.R
import com.frogobox.keyboard.common.base.BaseKeyboard
import com.frogobox.keyboard.databinding.KeyboardEmojiBinding
import com.frogobox.keyboard.common.ext.applyColorFilter
import com.frogobox.keyboard.common.ext.onScroll
import com.frogobox.keyboard.ui.keyboard.emoji.adapter.AutoGridLayoutManager
import com.frogobox.keyboard.ui.keyboard.emoji.adapter.EmojisAdapter
import com.frogobox.keyboard.ui.keyboard.emoji.adapter.parseRawEmojiSpecsFile
import com.frogobox.keyboard.ui.keyboard.main.ItemMainKeyboard
import com.frogobox.keyboard.ui.keyboard.main.OnKeyboardActionListener

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
    fun setupEmojiPalette(toolbarColor: Int, backgroundColor: Int, textColor: Int) {
        binding?.apply {
            emojiPaletteTopBar.background = ColorDrawable(toolbarColor)
            emojiPaletteHolder.background = ColorDrawable(backgroundColor)
            emojiPaletteLabel.setTextColor(textColor)
            emojiPaletteClose.applyColorFilter(textColor)
        }
        setupEmojis()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun openEmojiPalette() {
        setupEmojis()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupEmojis() {
        ensureBackgroundThread {
            val fullEmojiList = parseRawEmojiSpecsFile(context, ItemMainKeyboard.EMOJI_SPEC_FILE_PATH)
            val systemFontPaint = Paint().apply {
                typeface = Typeface.DEFAULT
            }

            val emojis = fullEmojiList.filter { emoji ->
                systemFontPaint.hasGlyph(emoji) || (EmojiCompat.get().loadState == EmojiCompat.LOAD_STATE_SUCCEEDED && EmojiCompat.get()
                    .getEmojiMatch(emoji, emojiCompatMetadataVersion) == EmojiCompat.EMOJI_SUPPORTED)
            }

            Handler(Looper.getMainLooper()).post {
                setupEmojiAdapter(emojis)
            }
        }
    }

    private fun setupEmojiAdapter(emojis: List<String>) {
        binding?.emojisList?.apply {
            val emojiItemWidth = context.resources.getDimensionPixelSize(R.dimen.emoji_item_size)
            val emojiTopBarElevation =
                context.resources.getDimensionPixelSize(com.frogobox.ui.R.dimen.frogo_dimen_4dp).toFloat()

            layoutManager = AutoGridLayoutManager(context, emojiItemWidth)
            adapter = EmojisAdapter(data = emojis) {
                mOnKeyboardActionListener!!.onText(it)
            }

            onScroll {
                binding!!.emojiPaletteTopBar.elevation =
                    if (it > 4) emojiTopBarElevation else 0f
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