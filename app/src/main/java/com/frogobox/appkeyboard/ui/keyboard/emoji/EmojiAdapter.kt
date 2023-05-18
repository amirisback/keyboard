package com.frogobox.appkeyboard.ui.keyboard.emoji

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.emoji2.text.EmojiCompat
import androidx.recyclerview.widget.RecyclerView
import com.frogobox.appkeyboard.databinding.ItemKeyboardEmojiBinding

class EmojiAdapter(
    val itemClick: (emoji: String) -> Unit,
) : RecyclerView.Adapter<EmojiAdapter.ViewHolder>() {

    private val dataEmoji = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemKeyboardEmojiBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(dataEmoji[position])
    }

    override fun getItemCount() = dataEmoji.size

    fun addData(data: List<String>) {
        dataEmoji.clear()
        dataEmoji.addAll(data)
    }

    inner class ViewHolder(private val binding: ItemKeyboardEmojiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(emoji: String) {
            val processed = EmojiCompat.get().process(emoji)

            binding.apply {
                tvEmoji.text = processed

                tvEmoji.setOnClickListener {
                    itemClick.invoke(emoji)
                }
            }

        }

    }

}