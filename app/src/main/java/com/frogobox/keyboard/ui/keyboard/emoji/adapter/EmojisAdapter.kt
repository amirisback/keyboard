package com.frogobox.keyboard.ui.keyboard.emoji.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.emoji2.text.EmojiCompat
import androidx.recyclerview.widget.RecyclerView
import com.frogobox.keyboard.R

class EmojisAdapter(
    val context: Context,
    var items: List<String>,
    val itemClick: (emoji: String) -> Unit,
) : RecyclerView.Adapter<EmojisAdapter.ViewHolder>() {
    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = R.layout.item_keyboard_emoji
        val view = layoutInflater.inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bindView(item) { itemView ->
            setupEmoji(itemView, item)
        }
    }

    override fun getItemCount() = items.size

    private fun setupEmoji(view: View, emoji: String) {
        val processed = EmojiCompat.get().process(emoji)
        view.findViewById<TextView>(R.id.emoji_value).text = processed
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(emoji: String, callback: (itemView: View) -> Unit): View {
            return itemView.apply {
                callback(this)

                setOnClickListener {
                    itemClick.invoke(emoji)
                }
            }
        }
    }
}
