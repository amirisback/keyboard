package com.frogobox.keyboard.ui.keyboard.emoji.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.emoji2.text.EmojiCompat
import androidx.recyclerview.widget.RecyclerView
import com.frogobox.keyboard.databinding.ItemKeyboardEmojiBinding

class EmojisAdapter(
    val data: List<String>,
    val itemClick: (emoji: String) -> Unit,
) : RecyclerView.Adapter<EmojisAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemKeyboardEmojiBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(data[position])
    }

    override fun getItemCount() = data.size


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